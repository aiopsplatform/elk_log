package com.ai.platform.service.impl;

import com.ai.platform.dao.FieldStatisticsDao;
import com.ai.platform.service.FieldStatisticsService;
import com.ai.platform.util.RequestFieldsBean;
import com.ai.pojo.FieldBlockAggregateStatics;
import com.ai.pojo.FieldCount;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class FieldStatisticsServiceImpl implements FieldStatisticsService {

    @Autowired
    private FieldStatisticsDao fieldStatisticsDao;

    @Override
    public List getIndexMetaData(@RequestBody JSONObject jsonObject) {

        //获取前端发来的请求携带的参数
        String index = jsonObject.get(RequestFieldsBean.getINDEX()).toString();
        List fieldList = fieldStatisticsDao.selectFieldsList(index);
        return fieldList;
    }

    @Override
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
        List fieldsList = fieldStatisticsDao.fieldsCount(fieldCount);
        return fieldsList;
    }

    /**
     * 字段统计(第二大类---按时间再进行分段)
     *
     * @param jsonObject--请求中携带的json对象
     * @return --- 返回值为list数组中封装的对象
     */
    @Override
    public List timeAggMma(JSONObject jsonObject) {
        String start_Time = jsonObject.get(RequestFieldsBean.getBEGINTIME()).toString();
        String end_Time = jsonObject.get(RequestFieldsBean.getENDTIME()).toString();
        //解析索引名称对应的id
        String indexes = jsonObject.get(RequestFieldsBean.getINDEX()).toString();

        String fieldId = jsonObject.get(RequestFieldsBean.getFIELD()).toString();
        //从前端获取的时间聚合规则(按分钟、小时、天数)
        String timeSlicing = jsonObject.get(RequestFieldsBean.getTIMESLICING()).toString();
        //从前端获取的统计类型(总和/最大值/最小值/平均值)
        String staticalType = jsonObject.get(RequestFieldsBean.getSTATICALTYPE()).toString();

        FieldBlockAggregateStatics fieldBlockAggregateStatics =
                new FieldBlockAggregateStatics(indexes, start_Time, end_Time, fieldId, timeSlicing, staticalType);
        List timeBlockAggregateStatics = fieldStatisticsDao.timeBlockAggregateStatics(fieldBlockAggregateStatics);
        return timeBlockAggregateStatics;
    }


}
