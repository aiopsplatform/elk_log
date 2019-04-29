package com.ai.platform.service.impl;

import com.ai.platform.dao.ConditionQueryDao;
import com.ai.platform.service.ConditionQueryService;
import com.ai.platform.util.RequestFieldsBean;
import com.ai.pojo.IndexDate;
import com.ai.pojo.KeyWord;
import com.ai.pojo.Log;
import com.google.gson.Gson;
import net.sf.json.JSONObject;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Service
public class ConditionQueryServiceImpl implements ConditionQueryService {

    @Autowired
    private ConditionQueryDao conditionQueryDao;



    @Override
    public String selectByTime(JSONObject jsonObject) {
        Gson selectGson = new Gson();
        String json;
        //解析begin_time和end_time对应的开始时间
        String start_Time = jsonObject.get(RequestFieldsBean.getBEGINTIME()).toString();
        String end_Time = jsonObject.get(RequestFieldsBean.getENDTIME()).toString();
        int page = Integer.parseInt(jsonObject.get(RequestFieldsBean.getPAGE()).toString());
        //解析索引名称对应的id
        String indexes = jsonObject.get(RequestFieldsBean.getINDEX()).toString();
        IndexDate indexDate = new IndexDate(indexes, start_Time, end_Time, page);
        //将所有日志存放到list数组中
        List<SearchHit> selectIndexByTimeList = conditionQueryDao.selectByTime(indexDate);
        //将list转换为json格式返回给前端
        json = selectGson.toJson(selectIndexByTimeList);
        return json;
    }

    @Override
    public String queryKeyword(JSONObject jsonObject) {
        String json;
        //索引名称
        String index = jsonObject.get(RequestFieldsBean.getINDEX()).toString();
        //开始时间
        String beginTime = jsonObject.get(RequestFieldsBean.getBEGINTIME()).toString();
        //结束时间
        String endTime = jsonObject.get(RequestFieldsBean.getENDTIME()).toString();
        //关键字
        String keyWord = jsonObject.get(RequestFieldsBean.getKEYWORD()).toString();
        KeyWord ky = new KeyWord(index, beginTime, endTime, keyWord);
        List keywordList = conditionQueryDao.queryKeyWord(ky);
        Gson keywordGson = new Gson();
        //将list转换为json格式返回给前端
        json = keywordGson.toJson(keywordList);
        return json;
    }

    @Override
    public void testDownload(HttpServletRequest request, HttpServletResponse res) {
        String start_Time = request.getParameter(RequestFieldsBean.getBEGINTIME());
        String end_Time = request.getParameter(RequestFieldsBean.getENDTIME());
        //解析索引名称对应的id
        String indexes = request.getParameter(RequestFieldsBean.getINDEX());
        Log log = new Log(indexes, start_Time, end_Time);
        String downLoadLog = conditionQueryDao.downLoadLog(log);
        String fileName = "log.txt";
        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        OutputStream os = null;
        try {
            //获取response中的字节输出流
            os = res.getOutputStream();
            //使用response的字节输出流输出在内存中的查询结果
            os.write(downLoadLog.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.flush();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }







}
