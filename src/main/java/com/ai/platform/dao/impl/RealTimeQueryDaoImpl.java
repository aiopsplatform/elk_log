package com.ai.platform.dao.impl;

import com.ai.platform.config.TransportClientConfig;
import com.ai.platform.dao.RealTimeQueryDao;
import com.ai.platform.service.QueryIndexService;
import com.ai.platform.util.FieldBean;
import com.ai.platform.util.RequestFieldsBean;
import com.ai.pojo.Indexs;
import com.google.gson.Gson;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RealTimeQueryDaoImpl implements RealTimeQueryDao {

    @Autowired
    private QueryIndexService queryIndexService;

    /**
     * 实时查询数据
     *
     * @param indexes
     * @return
     */
    @Override
    public List<SearchHit> selectRealTimeQuery(String indexes) {

        List realTimeList = new ArrayList();

        //根据前端传来的indexes判断id的值，同时确定indexes的真实索引名称
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

        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        SearchResponse searchResponse = TransportClientConfig.client.prepareSearch(indexName).
                setQuery(queryBuilder).
                addSort(FieldBean.getCREATTIME(), SortOrder.DESC).
                setSize(20).execute().actionGet();
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits) {
            realTimeList.add(hit);
        }

        return realTimeList;
    }


}
