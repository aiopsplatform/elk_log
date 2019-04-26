package com.ai.platform.dao.impl;

import com.ai.platform.config.TransportClientConfig;
import com.ai.platform.dao.AbnormalStatisticsDao;
import com.ai.platform.service.QueryIndexService;
import com.ai.platform.util.FieldBean;
import com.ai.platform.util.RequestFieldsBean;
import com.ai.pojo.ChartCount;
import com.ai.pojo.ExceptionCount;
import com.ai.pojo.Indexs;
import com.google.gson.Gson;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AbnormalStatisticsDaoImpl implements AbnormalStatisticsDao {

    @Autowired
    private QueryIndexService queryIndexService;

    /**
     * 异常统计
     */
    @Override
    public Map count(ExceptionCount exceptionCount) {
        Map map = new HashMap();

        String indexes = exceptionCount.getIndexName();
        String beginTime = exceptionCount.getBegin_time();
        String endTime = exceptionCount.getEnd_time();

        List list = queryIndexService.tailList();
        List elkLogTypeList = new ArrayList();
        Indexs indexs;
        for (int i = 0; i < list.size(); i++) {
            indexs = new Indexs(i, list.get(i).toString(), "");
            elkLogTypeList.add(indexs);
        }
        Gson gson = new Gson();
        String s1 = gson.toJson(elkLogTypeList);
        JSONArray jsonArray = JSONArray.fromObject(s1);
        JSONObject jsonObject = jsonArray.getJSONObject(Integer.parseInt(indexes));
        String indexName = jsonObject.get(RequestFieldsBean.getNAME()).toString();

        //按时间进行范围查询
        QueryBuilder qb1 = QueryBuilders.
                rangeQuery(FieldBean.getCREATTIME()).
                from(beginTime).
                to(endTime);

        //按异常进行分组聚合
        AggregationBuilder termsBuilder = AggregationBuilders
                .terms("by_response")
                .field(FieldBean.getRESPONSE());

        SearchResponse searchResponse = TransportClientConfig.client.prepareSearch(indexName).
                setQuery(qb1).
                addAggregation(termsBuilder).
                execute().actionGet();

        Terms terms = searchResponse.getAggregations().get("by_response");
        //循环遍历bucket桶
        for (Terms.Bucket entry : terms.getBuckets()) {
            map.put(Integer.parseInt(entry.getKey().toString()), entry.getDocCount());
        }

        Iterator<Integer> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            Integer key = iterator.next();
            if (key.equals(200)) {
                iterator.remove();
            }
        }
        return map;
    }

}
