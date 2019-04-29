package com.ai.platform.service.impl;

import com.ai.platform.dao.AbnormalStatisticsDao;
import com.ai.platform.service.AbnormalStatisticsService;
import com.ai.platform.util.RequestFieldsBean;
import com.ai.pojo.ExceptionCount;
import com.ai.pojo.ExceptionValue;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class AbnormalStatisticsServiceImpl implements AbnormalStatisticsService {

    @Autowired
    private AbnormalStatisticsDao abnormalStatisticsDao;

    @Override
    public List exceptionCount(@RequestBody JSONObject jsonObject){

        String indexes = jsonObject.get(RequestFieldsBean.getINDEX()).toString();
        String beginTime = jsonObject.get(RequestFieldsBean.getBEGINTIME()).toString();
        String endTime = jsonObject.get(RequestFieldsBean.getENDTIME()).toString();

        ExceptionCount exceptionCount = new ExceptionCount(indexes, beginTime, endTime);
        Map<Integer, Long> selectExceptionCount = abnormalStatisticsDao.count(exceptionCount);
        List<ExceptionValue> list = new ArrayList<>();
        for (Integer key : selectExceptionCount.keySet()) {
            Long value = selectExceptionCount.get(key);

            ExceptionValue value1 = new ExceptionValue(key, value);
            list.add(value1);
        }
        Collections.sort(list);

        return list;
    }

}
