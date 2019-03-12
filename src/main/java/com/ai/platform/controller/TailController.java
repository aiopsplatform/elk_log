package com.ai.platform.controller;

import com.ai.platform.service.TailDao;
import com.ai.platform.util.FieldBean;
import com.ai.platform.util.RequestFieldsBean;
import com.ai.platform.util.SlowRequestCountBean;
import com.ai.pojo.*;
import com.google.gson.Gson;
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
    private TailDao tailDao;

    //下拉框，查询所有索引文件的名称
    @GetMapping("/indexAll")
    public String tailList() throws UnknownHostException {

        List ls = tailDao.tailList();

        Gson gson = new Gson();

        String st = gson.toJson(ls);

        return st;

        //System.out.println(st);
    }


    //使用假索引名称数据返回给前端
    @GetMapping("/indexList")
    public String getIndex() {
        Gson gson = new Gson();

        Index index = new Index(1, "indexlog", "string", "logstash", "logstash");
        Index index2 = new Index(2, "indexlog2", "string2", "nginx", "nginx");
        Index index3 = new Index(3, "indexlog3", "string3", "Tomcat", "Tomcat");
        List ls = new ArrayList();
        ls.add(index);
        ls.add(index2);
        ls.add(index3);

        String st = gson.toJson(ls);

        return st;
    }


    //查询ElkLogType
    @GetMapping("/getElkLogType")
    public List getElkLogType() throws UnknownHostException {
        List elkLogTypeList = new ArrayList();

        List list = tailDao.tailList();

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
        String start_Time = jsonObject.get(RequestFieldsBean.getBeginTime()).toString();
        String end_Time = jsonObject.get(RequestFieldsBean.getEndTime()).toString();

        //解析索引名称对应的id
        String indexes = jsonObject.get(RequestFieldsBean.getIndex()).toString();

        IndexDate indexDate = new IndexDate(indexes, start_Time, end_Time);

        //将所有日志存放到list数组中
        List<SearchHit> selectIndexByTimeList = tailDao.selectByTime(indexDate);

        List list3 = new ArrayList();

        //需要将list转换成json格式
        for (SearchHit logMessage : selectIndexByTimeList) {
            String message = logMessage.getSourceAsMap().get(FieldBean.getMessage()).toString();
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
        String indexes = jsonObject.get(RequestFieldsBean.getIndex()).toString();

        List<SearchHit> selectRealTime = tailDao.selectRealTimeQuery(indexes);

        //需要将list转换成json格式
        for (SearchHit logMessage : selectRealTime) {
            String message = logMessage.getSourceAsMap().get(FieldBean.getMessage()).toString();
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
         * type -- 索引类型
         * beginTime -- 开始时间
         * endTime -- 结束时间
         */
        String indexes = jsonObject.get(RequestFieldsBean.getIndex()).toString();
        String type = jsonObject.get(RequestFieldsBean.getType()).toString();
        String beginTime = jsonObject.get(RequestFieldsBean.getBeginTime()).toString();
        String endTime = jsonObject.get(RequestFieldsBean.getEndTime()).toString();

        ExceptionCount exceptionCount = new ExceptionCount(indexes, type, beginTime, endTime);
        Map selectExceptionCount = tailDao.count(exceptionCount);
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
        String index = jsonObject.get(RequestFieldsBean.getIndex()).toString();
        String beginTime = jsonObject.get(RequestFieldsBean.getBeginTime()).toString();
        String endTime = jsonObject.get(RequestFieldsBean.getEndTime()).toString();
        List list = new ArrayList();
        SlowCountBean slowCountBean = new SlowCountBean(index, beginTime, endTime);

        //统计0-1秒的请求次数
        Long value1 = tailDao.selectSlowCount1(slowCountBean);
        //统计1-2秒的请求次数
        Long value2 = tailDao.selectSlowCount2(slowCountBean);
        //统计2-3秒的请求次数
        Long value3 = tailDao.selectSlowCount3(slowCountBean);
        //统计3-4秒的请求次数
        Long value4 = tailDao.selectSlowCount4(slowCountBean);
        //统计4-5秒的请求次数
        Long value5 = tailDao.selectSlowCount5(slowCountBean);
        //统计5-6秒的请求次数
        Long value6 = tailDao.selectSlowCount6(slowCountBean);

        PartCount partCount1 = new PartCount(SlowRequestCountBean.getOne(), value1);
        PartCount partCount2 = new PartCount(SlowRequestCountBean.getTwo(), value2);
        PartCount partCount3 = new PartCount(SlowRequestCountBean.getThree(), value3);
        PartCount partCount4 = new PartCount(SlowRequestCountBean.getFour(), value4);
        PartCount partCount5 = new PartCount(SlowRequestCountBean.getFive(), value5);
        PartCount partCount6 = new PartCount(SlowRequestCountBean.getSix(), value6);
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
        String index = jsonObject.get(RequestFieldsBean.getIndex()).toString();
        List fieldList = tailDao.selectFieldsList(index);
        return fieldList;
    }

    /**
     * 字段统计
     */
    @PostMapping(value = "fieldStatistics")
    @ResponseBody
    public List selectFieldCount(@RequestBody JSONObject jsonObject) {

        //索引名称
        String index = jsonObject.get(RequestFieldsBean.getIndex()).toString();
        //开始时间
        String beginTime = jsonObject.get(RequestFieldsBean.getBeginTime()).toString();
        //结束时间
        String endTime = jsonObject.get(RequestFieldsBean.getEndTime()).toString();
        //字段名称
        String fieldNameId = jsonObject.get(RequestFieldsBean.getField()).toString();
        //查询条件
        JSONObject queryCondition = jsonObject.getJSONObject(RequestFieldsBean.getQueryCondition());
//        if (queryCondition){
//
//        }
        //分段规则
        String rule = null;
        try {
            rule = jsonObject.get(RequestFieldsBean.getRule()).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }


        FieldCount fieldCount = new FieldCount(index, beginTime, endTime, fieldNameId, queryCondition, rule);

        List fieldsList = tailDao.fieldsCount(fieldCount);

        return fieldsList;
    }


}
