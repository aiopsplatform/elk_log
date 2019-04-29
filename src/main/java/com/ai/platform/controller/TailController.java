package com.ai.platform.controller;

import com.ai.platform.service.TailService;
import com.ai.platform.test.TestDao;
import com.ai.platform.util.RequestFieldsBean;
import com.ai.platform.util.SlowRequestCountBean;
import com.ai.pojo.*;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.util.*;

@RestController
@RequestMapping(value = "/index")
public class TailController {

    @Autowired
    private TailService tailService;

    @Autowired
    private TestDao testDao;

//


    /**
     * 慢统计
     */
    @PostMapping(value = "slowRequestCount")
    @ResponseBody
    public List slowCount(@RequestBody JSONObject jsonObject) throws UnknownHostException {

        System.out.println(testDao.getFieldAvg());

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



//    //字段统计——————按时间分段统计
//    @PostMapping(value = "")
//    @ResponseBody
//    public String timeAggMma(@RequestBody JSONObject jsonObject){
//        String start_Time = jsonObject.get(RequestFieldsBean.getBEGINTIME()).toString();
//        String end_Time = jsonObject.get(RequestFieldsBean.getENDTIME()).toString();
//        //解析索引名称对应的id
//        String indexes = jsonObject.get(RequestFieldsBean.getINDEX()).toString();
//
//        //从前端获取的时间聚合规则(按分钟、小时、天数)
//        String timeSlicing = "????";
//
//        //从前端获取的统计类型(总和/最大值/最小值/平均值)
//        String staticalType = "????";
//
//        //按分钟统计
//        if (timeSlicing.equals("minutes")) {
//            if (staticalType.equals(NumberIdBean.getZERO())){
//
//            }
//            if (staticalType.equals(NumberIdBean.getONE())){
//
//            }
//            if (staticalType.equals(NumberIdBean.getTWO())){
//
//            }
//            if (staticalType.equals(NumberIdBean.getTHREE())){
//
//            }
//        }
//        //按小时统计
//        if (timeSlicing.equals("hour")){
//            if (staticalType.equals(NumberIdBean.getZERO())){
//
//            }
//            if (staticalType.equals(NumberIdBean.getONE())){
//
//            }
//            if (staticalType.equals(NumberIdBean.getTWO())){
//
//            }
//            if (staticalType.equals(NumberIdBean.getTHREE())){
//
//            }
//        }
//        //按天统计
//        if (timeSlicing.equals("day")){
//            if (staticalType.equals(NumberIdBean.getZERO())){
//
//            }
//            if (staticalType.equals(NumberIdBean.getONE())){
//
//            }
//            if (staticalType.equals(NumberIdBean.getTWO())){
//
//            }
//            if (staticalType.equals(NumberIdBean.getTHREE())){
//
//            }
//        }
//
//
//        return null;
//    }












}






