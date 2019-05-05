package com.ai.platform.service.impl;

import com.ai.platform.dao.RealTimeQueryDao;
import com.ai.platform.service.RealTimeQueryService;
import com.ai.platform.util.FieldBean;
import com.ai.platform.util.RequestFieldsBean;
import com.google.gson.Gson;
import net.sf.json.JSONObject;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RealTimeQueryServiceImpl implements RealTimeQueryService {

    @Autowired
    private RealTimeQueryDao realTimeQueryDao;

    @Override
    public String selectRealTimeQuery(JSONObject jsonObject){
        String realJson;
        Gson realTimeGson = new Gson();
        List<String> realTimeList = new ArrayList<>();

        //解析索引名称对应的id
        String indexes = jsonObject.get(RequestFieldsBean.getINDEX()).toString();

        List<SearchHit> selectRealTime = realTimeQueryDao.selectRealTimeQuery(indexes);

        //需要将list转换成json格式
        for (SearchHit logMessage : selectRealTime) {
            String message = logMessage.getSourceAsMap().get(FieldBean.getMESSAGE()).toString();
            realTimeList.add(message);
        }
        //将list转换为json格式返回给前端
        realJson = realTimeGson.toJson(realTimeList);

        return realJson;

    }
}
