package com.ai.platform.dao.impl;

import com.ai.platform.config.TransportClientConfig;
import com.ai.platform.dao.ConditionQueryDao;
import com.ai.platform.service.QueryIndexService;
import com.ai.platform.util.FieldBean;
import com.ai.platform.util.RequestFieldsBean;
import com.ai.pojo.IndexDate;
import com.ai.pojo.Indexs;
import com.ai.pojo.KeyWord;
import com.ai.pojo.Log;
import com.google.gson.Gson;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ConditionQueryDaoImpl implements ConditionQueryDao {

    @Autowired
    private QueryIndexService queryIndexService;

    /**
     * 根据索引名称、开始时间和结束时间条件进行查询
     */
    @Override
    public List<SearchHit> selectByTime(IndexDate indexDate) {

        String indexesName = indexDate.getIndexes();
        String startTime = indexDate.getStartTime();
        String endTime = indexDate.getEndTime();
        int page = indexDate.getPage();

        List list = queryIndexService.tailList();
        List<Indexs> elkLogTypeList = new ArrayList<>();
        Indexs indexs = null;
        for (int i = 0; i < list.size(); i++) {
            indexs = new Indexs(i, list.get(i).toString(), "");
            elkLogTypeList.add(indexs);
        }
        Gson gson = new Gson();
        String s1 = gson.toJson(elkLogTypeList);
        JSONArray jsonArray = JSONArray.fromObject(s1);
        JSONObject jsonObject = jsonArray.getJSONObject(Integer.parseInt(indexesName));
        String indexName = jsonObject.get(RequestFieldsBean.getNAME()).toString();

        QueryBuilder qb = QueryBuilders.rangeQuery(FieldBean.getCREATTIME()).from(startTime).to(endTime);
        SearchResponse response = TransportClientConfig.client.prepareSearch(indexName)
                .setQuery(qb)
                .setFrom(20 * (page - 1))
                .setSize(20)
                .execute().actionGet();
        List pageSearchList = scrollOutput(response);
//        SearchResponse sr = client.prepareSearch(indexName)
//                .setQuery(qb)
//                .setSize(50)
//                .execute().actionGet();
//        SearchHits hits = sr.getHits();
//        for (SearchHit hit : hits) {
//            indexByTimeList.add(hit);
//        }
        return pageSearchList;
    }

    public List scrollOutput(SearchResponse response) {
        SearchHits hits = response.getHits();
        List<String> list = new ArrayList<>();
        try {
            for (int j = 0; j < hits.getHits().length; j++) {
                String message = hits.getHits()[j].getSourceAsMap().get(FieldBean.getMESSAGE()).toString();
                list.add(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 关键字查询
     */
    @Override
    public List queryKeyWord(KeyWord ky) {

        //获取索引id，找到对应的索引名称
        String index = ky.getIndexName();
        List listIndex = queryIndexService.tailList();
        List<Indexs> elkLogTypeList = new ArrayList<>();
        Indexs indexs = null;
        for (int i = 0; i < listIndex.size(); i++) {
            indexs = new Indexs(i, listIndex.get(i).toString(), "");
            elkLogTypeList.add(indexs);
        }
        Gson gsonIndex = new Gson();
        String s1 = gsonIndex.toJson(elkLogTypeList);
        JSONArray jsonArrayIndex = JSONArray.fromObject(s1);
        JSONObject jsonObjectIndex = jsonArrayIndex.getJSONObject(Integer.parseInt(index));
        String indexName = jsonObjectIndex.get(RequestFieldsBean.getNAME()).toString();
        //获取开始时间和结束时间
        String beginTime = ky.getBeginTime();
        String endTime = ky.getEndTime();
        //获取关键字
        String keyWord = ky.getKeyWord();
        QueryBuilder boolQuery = QueryBuilders.boolQuery();
        QueryBuilder qbRang = QueryBuilders
                .rangeQuery(FieldBean.getCREATTIME())
                .from(beginTime)
                .to(endTime);
        QueryBuilder matchQuery = QueryBuilders
                .matchQuery(FieldBean.getMESSAGE(), keyWord);
        ((BoolQueryBuilder) boolQuery).must(qbRang);
        ((BoolQueryBuilder) boolQuery).must(matchQuery);
        SearchResponse searchResponse = TransportClientConfig.client
                .prepareSearch(indexName)
                .setQuery(boolQuery)
                .execute().actionGet();
        List keyWordList = scrollOutput(searchResponse);
        return keyWordList;
    }


    /**
     * 导出功能
     */
    @Override
    public String downLoadLog(Log log) {
        String indexesName = log.getIndexes();
        String startTime = log.getStartTime();
        String endTime = log.getEndTime();
        List<String> list = queryIndexService.tailList();
        List<Indexs> elkLogTypeList = new ArrayList<>();
        Indexs indexs = null;
        for (int i = 0; i < list.size(); i++) {
            indexs = new Indexs(i, list.get(i), "");
            elkLogTypeList.add(indexs);
        }
        Gson gson = new Gson();
        String s1 = gson.toJson(elkLogTypeList);
        JSONArray jsonArray = JSONArray.fromObject(s1);
        JSONObject jsonObject = jsonArray.getJSONObject(Integer.parseInt(indexesName));
        String indexName = jsonObject.get(RequestFieldsBean.getNAME()).toString();
        QueryBuilder qb = QueryBuilders.rangeQuery(FieldBean.getCREATTIME()).from(startTime).to(endTime);
        SearchResponse response = TransportClientConfig.client.prepareSearch(indexName)
                .setQuery(qb)
                .setSize(10000)
                .execute().actionGet();
        List downloadLog = scrollOutput(response);
        String dll = downloadLog.toString();
        return dll;
    }


}
