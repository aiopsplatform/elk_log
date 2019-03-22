package com.ai.platform.controller;

import com.ai.platform.service.TailService;
import com.ai.platform.util.FieldBean;
import com.ai.platform.util.RequestFieldsBean;
import com.ai.platform.util.SlowRequestCountBean;
import com.ai.pojo.*;
import com.google.gson.Gson;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/index")
public class TailController {


    @Autowired
    private TailService tailService;

    //下拉框，查询所有索引文件的名称
    @GetMapping("/indexAll")
    public String tailList() throws UnknownHostException {

        List ls = tailService.tailList();

        Gson gson = new Gson();

        String st = gson.toJson(ls);

        return st;

        //System.out.println(st);
    }


    //查询ElkLogType
    @GetMapping("/getElkLogType")
    public List getElkLogType() throws UnknownHostException {
        List elkLogTypeList = new ArrayList();

        List list = tailService.tailList();

        Indexs indexs = null;
        for (int i = 0; i < list.size(); i++) {
            indexs = new Indexs(i, list.get(i).toString());
            elkLogTypeList.add(indexs);
        }

        return elkLogTypeList;
    }


    /**
     * 通过索引名称，开始时间和结束时间查询日志数据
     */
    @PostMapping(value = "selectByIndex")
    @ResponseBody
    public String selectByTime(@RequestBody JSONObject jsonObject) throws Exception {

        Gson selectGson = new Gson();


        //解析begin_time和end_time对应的开始时间
        String start_Time = jsonObject.get(RequestFieldsBean.getBEGINTIME()).toString();
        String end_Time = jsonObject.get(RequestFieldsBean.getENDTIME()).toString();

        //解析索引名称对应的id
        String indexes = jsonObject.get(RequestFieldsBean.getINDEX()).toString();

        IndexDate indexDate = new IndexDate(indexes, start_Time, end_Time);

        //将所有日志存放到list数组中
        List<SearchHit> selectIndexByTimeList = tailService.selectByTime(indexDate);

        List list3 = new ArrayList();

        //需要将list转换成json格式
        for (SearchHit logMessage : selectIndexByTimeList) {
            String message = logMessage.getSourceAsMap().get(FieldBean.getMESSAGE()).toString();
            list3.add(message);
        }
        //将list转换为json格式返回给前端
        String json = selectGson.toJson(list3);

        return json;
    }


    /**
     * 实时查询，每个一秒接受一个请求，从后台进行查询
     */
    @PostMapping(value = "selectRealTimeQuery")
    @ResponseBody
    public String selectRealTimeQuery(@RequestBody JSONObject jsonObject) throws UnknownHostException {

        Gson realTimeGson = new Gson();
        List realTimeList = new ArrayList();

        //解析索引名称对应的id
        String indexes = jsonObject.get(RequestFieldsBean.getINDEX()).toString();

        List<SearchHit> selectRealTime = tailService.selectRealTimeQuery(indexes);

        //需要将list转换成json格式
        for (SearchHit logMessage : selectRealTime) {
            String message = logMessage.getSourceAsMap().get(FieldBean.getMESSAGE()).toString();
            realTimeList.add(message);
        }
        //将list转换为json格式返回给前端
        String realJson = realTimeGson.toJson(realTimeList);

        return realJson;

    }

    /**
     * 异常统计
     */
    @PostMapping(value = "exceptionCount")
    @ResponseBody
    public List exceptionCount(@RequestBody JSONObject jsonObject) throws Exception {

        /*
         * indexes -- 索引名称
         * beginTime -- 开始时间
         * endTime -- 结束时间
         */
        String indexes = jsonObject.get(RequestFieldsBean.getINDEX()).toString();
        String beginTime = jsonObject.get(RequestFieldsBean.getBEGINTIME()).toString();
        String endTime = jsonObject.get(RequestFieldsBean.getENDTIME()).toString();

        ExceptionCount exceptionCount = new ExceptionCount(indexes, beginTime, endTime);
        Map selectExceptionCount = tailService.count(exceptionCount);
        List list = new ArrayList();
        for (Object key : selectExceptionCount.keySet()) {
            Object value = selectExceptionCount.get(key);
            ExceptionValue value1 = new ExceptionValue(key, value);
            list.add(value1);
        }
        return list;
    }


    /**
     * 慢统计
     */
    @PostMapping(value = "slowRequestCount")
    @ResponseBody
    public List slowCount(@RequestBody JSONObject jsonObject) throws UnknownHostException {

        //从请求中获取对应字段的参数
        String index = jsonObject.get(RequestFieldsBean.getINDEX()).toString();
        String beginTime = jsonObject.get(RequestFieldsBean.getBEGINTIME()).toString();
        String endTime = jsonObject.get(RequestFieldsBean.getENDTIME()).toString();
        List list = new ArrayList();
        SlowCountBean slowCountBean = new SlowCountBean(index, beginTime, endTime);

        //统计0-1秒的请求次数
        Long value1 = tailService.selectSlowCount1(slowCountBean);
        //统计1-2秒的请求次数
        Long value2 = tailService.selectSlowCount2(slowCountBean);
        //统计2-3秒的请求次数
        Long value3 = tailService.selectSlowCount3(slowCountBean);
        //统计3-4秒的请求次数
        Long value4 = tailService.selectSlowCount4(slowCountBean);
        //统计4-5秒的请求次数
        Long value5 = tailService.selectSlowCount5(slowCountBean);
        //统计5-6秒的请求次数
        Long value6 = tailService.selectSlowCount6(slowCountBean);

        PartCount partCount1 = new PartCount(SlowRequestCountBean.getONE(), value1);
        PartCount partCount2 = new PartCount(SlowRequestCountBean.getTWO(), value2);
        PartCount partCount3 = new PartCount(SlowRequestCountBean.getTHREE(), value3);
        PartCount partCount4 = new PartCount(SlowRequestCountBean.getFOUR(), value4);
        PartCount partCount5 = new PartCount(SlowRequestCountBean.getFIVE(), value5);
        PartCount partCount6 = new PartCount(SlowRequestCountBean.getSIX(), value6);
        list.add(partCount1);
        list.add(partCount2);
        list.add(partCount3);
        list.add(partCount4);
        list.add(partCount5);
        list.add(partCount6);

        return list;
    }

    /**
     * 通过索引名称获取所有字段的名称(和字段类型)
     */
    @PostMapping(value = "getIndexMetaData")
    @ResponseBody
    public List getIndexMetaData(@RequestBody JSONObject jsonObject) {
        //获取前端发来的请求携带的参数
        String index = jsonObject.get(RequestFieldsBean.getINDEX()).toString();
        List fieldList = tailService.selectFieldsList(index);
        return fieldList;
    }

    /**
     * 字段统计
     */
    @PostMapping(value = "fieldStatistics")
    @ResponseBody
    public List selectFieldCount(@RequestBody JSONObject jsonObject) {

        //索引名称
        String index = jsonObject.get(RequestFieldsBean.getINDEX()).toString();
        //开始时间
        String beginTime = jsonObject.get(RequestFieldsBean.getBEGINTIME()).toString();
        //结束时间
        String endTime = jsonObject.get(RequestFieldsBean.getENDTIME()).toString();
        //字段名称
        String fieldNameId = jsonObject.get(RequestFieldsBean.getFIELD()).toString();
        //查询条件

        JSONArray queryCondition = (JSONArray) jsonObject.get(RequestFieldsBean.getQUERYCONDITION());


        //分段规则
        String rule = null;
        try {
            rule = jsonObject.get(RequestFieldsBean.getRULE()).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }


        FieldCount fieldCount = new FieldCount(index, beginTime, endTime, fieldNameId, queryCondition, rule);

        List fieldsList = tailService.fieldsCount(fieldCount);

        return fieldsList;
    }


}
