package com.ai.platform.dao;


import com.ai.platform.service.TailService;
import com.ai.platform.util.*;
import com.ai.pojo.*;
import com.google.gson.Gson;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.stereotype.Repository;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@Repository
public class TailServiceImpl extends RequestFieldsBean implements TailService {

    public static TransportClient client;

    //获取ELK客户端
    public static TransportClient getClient() throws UnknownHostException {
        if (client == null) {
            //指定ES集群
            Settings settings = Settings.builder().put(SloveHardCountBean.getClusterName(), SloveHardCountBean.getAPPNAME()).put("client.transport.sniff", true).build();
            //创建访问ES的客户端
            client = new PreBuiltTransportClient(settings).addTransportAddress(new TransportAddress(InetAddress.getByName(SloveHardCountBean.getINETADDR()), SloveHardCountBean.getCLIENTPORT()));
        }
        return client;
    }


    //查询ES中所有的索引
    private Map<Integer, String> getIndex() {
        TransportClient client = null;
        try {
            client = getClient();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        ActionFuture<IndicesStatsResponse> isr = client.admin().indices().stats(new IndicesStatsRequest().all());
        Set<String> set = isr.actionGet().getIndices().keySet();

        Map<Integer, String> map = new HashMap<>();
        int i = 0;
        for (String set1 : set) {
            map.put(i, set1);
            i++;
        }
        return map;
    }

    //获取所有索引名称返回给前端
    @Override
    public List<String> tailList() {
        Map map1 = this.getIndex();
        return new ArrayList<String>(map1.values());
    }

    /**
     * 慢请求统计0-1秒的请求
     */
    @Override
    public Long selectSlowCount1(SlowCountBean slowCountBean) {

        String index = slowCountBean.getIndex();
        String beginTime = slowCountBean.getStartTime();
        String endTime = slowCountBean.getEndTime();

        List list = tailList();
        List elkLogTypeList = new ArrayList();
        Indexs indexs;
        for (int i = 0; i < list.size(); i++) {
            indexs = new Indexs(i, list.get(i).toString(), "");
            elkLogTypeList.add(indexs);
        }
        Gson gson = new Gson();
        String s1 = gson.toJson(elkLogTypeList);
        JSONArray jsonArray = JSONArray.fromObject(s1);
        JSONObject jsonObject = jsonArray.getJSONObject(Integer.parseInt(index));
        String indexName = jsonObject.get(RequestFieldsBean.getNAME()).toString();

        //按时间进行范围查询
        QueryBuilder qbTime = QueryBuilders.rangeQuery(FieldBean.getCREATTIME()).from(beginTime).to(endTime);
        //按请求0-1秒时间进行统计
        QueryBuilder qb1 = QueryBuilders.rangeQuery(FieldBean.getOFFSET()).from(Integer.parseInt(NumberIdBean.getZERO())).to(NumberIdBean.getONETHOUSAND(), true);

        //按offset字段进行分组
        AggregationBuilder termsCount = AggregationBuilders.count("offsetCount").field(FieldBean.getOFFSET());

        SearchResponse sr = client.prepareSearch(indexName).
                setQuery(qbTime).
                setQuery(qb1).
                addAggregation(termsCount).execute().actionGet();
        ValueCount valueCount = sr.getAggregations().get("offsetCount");
        Long value1 = valueCount.getValue();
        return value1;
    }

    /**
     * 慢请求统计1-2秒的请求
     */
    @Override
    public Long selectSlowCount2(SlowCountBean slowCountBean) {

        String index = slowCountBean.getIndex();
        String beginTime = slowCountBean.getStartTime();
        String endTime = slowCountBean.getEndTime();

        List list = tailList();
        List elkLogTypeList = new ArrayList();
        Indexs indexs;
        for (int i = 0; i < list.size(); i++) {
            indexs = new Indexs(i, list.get(i).toString(), "");
            elkLogTypeList.add(indexs);
        }
        Gson gson = new Gson();
        String s1 = gson.toJson(elkLogTypeList);
        JSONArray jsonArray = JSONArray.fromObject(s1);
        JSONObject jsonObject = jsonArray.getJSONObject(Integer.parseInt(index));
        String indexName = jsonObject.get(RequestFieldsBean.getNAME()).toString();

        //按时间进行范围查询
        QueryBuilder qbTime = QueryBuilders.rangeQuery(FieldBean.getCREATTIME()).from(beginTime).to(endTime);
        //按请求1-2秒时间进行统计
        QueryBuilder qb1 = QueryBuilders.rangeQuery(FieldBean.getOFFSET()).from(NumberIdBean.getONETHOUSANDONE()).to(NumberIdBean.getTWOTHOUSAND(), true);

        //按offset字段进行分组
        AggregationBuilder termsCount = AggregationBuilders.count("offsetCount").field(FieldBean.getOFFSET());

        SearchResponse sr = client.prepareSearch(indexName).
                setQuery(qbTime).
                setQuery(qb1).
                addAggregation(termsCount).execute().actionGet();
        ValueCount valueCount = sr.getAggregations().get("offsetCount");
        Long value2 = valueCount.getValue();
        return value2;
    }

    /**
     * 慢请求统计2-3秒的请求
     */
    @Override
    public Long selectSlowCount3(SlowCountBean slowCountBean) {

        String index = slowCountBean.getIndex();
        String beginTime = slowCountBean.getStartTime();
        String endTime = slowCountBean.getEndTime();

        List list = tailList();
        List elkLogTypeList = new ArrayList();
        Indexs indexs;
        for (int i = 0; i < list.size(); i++) {
            indexs = new Indexs(i, list.get(i).toString(), "");
            elkLogTypeList.add(indexs);
        }
        Gson gson = new Gson();
        String s1 = gson.toJson(elkLogTypeList);
        JSONArray jsonArray = JSONArray.fromObject(s1);
        JSONObject jsonObject = jsonArray.getJSONObject(Integer.parseInt(index));
        String indexName = jsonObject.get(RequestFieldsBean.getNAME()).toString();

        //按时间进行范围查询
        QueryBuilder qbTime = QueryBuilders.rangeQuery(FieldBean.getCREATTIME()).from(beginTime).to(endTime);
        //按请求2-3秒时间进行统计
        QueryBuilder qb1 = QueryBuilders.rangeQuery(FieldBean.getOFFSET()).from(NumberIdBean.getTWOTHOUSANDONE()).to(NumberIdBean.getTHREETHOUSAND(), true);

        //按offset字段进行分组
        AggregationBuilder termsCount = AggregationBuilders.count("offsetCount").field(FieldBean.getOFFSET());

        SearchResponse sr = client.prepareSearch(indexName).
                setQuery(qbTime).
                setQuery(qb1).
                addAggregation(termsCount).execute().actionGet();
        ValueCount valueCount = sr.getAggregations().get("offsetCount");
        Long value1 = valueCount.getValue();
        return value1;
    }

    /**
     * 慢请求统计3-4秒的请求
     */
    @Override
    public Long selectSlowCount4(SlowCountBean slowCountBean) {

        String index = slowCountBean.getIndex();
        String beginTime = slowCountBean.getStartTime();
        String endTime = slowCountBean.getEndTime();

        List list = tailList();
        List elkLogTypeList = new ArrayList();
        Indexs indexs;
        for (int i = 0; i < list.size(); i++) {
            indexs = new Indexs(i, list.get(i).toString(), "");
            elkLogTypeList.add(indexs);
        }
        Gson gson = new Gson();
        String s1 = gson.toJson(elkLogTypeList);
        JSONArray jsonArray = JSONArray.fromObject(s1);
        JSONObject jsonObject = jsonArray.getJSONObject(Integer.parseInt(index));
        String indexName = jsonObject.get(RequestFieldsBean.getNAME()).toString();

        //按时间进行范围查询
        QueryBuilder qbTime = QueryBuilders.rangeQuery(FieldBean.getCREATTIME()).from(beginTime).to(endTime);
        //按请求3-4秒时间进行统计
        QueryBuilder qb1 = QueryBuilders.rangeQuery(FieldBean.getOFFSET()).from(NumberIdBean.getTHREETHOUSANDONE()).to(NumberIdBean.getFOURTHOUSAND(), true);

        //按offset字段进行分组
        AggregationBuilder termsCount = AggregationBuilders.count("offsetCount").field(FieldBean.getOFFSET());

        SearchResponse sr = client.prepareSearch(indexName).
                setQuery(qbTime).
                setQuery(qb1).
                addAggregation(termsCount).execute().actionGet();
        ValueCount valueCount = sr.getAggregations().get("offsetCount");
        Long value1 = valueCount.getValue();
        return value1;
    }

    /**
     * 慢请求统计4-5秒的请求
     */
    @Override
    public Long selectSlowCount5(SlowCountBean slowCountBean) {

        String index = slowCountBean.getIndex();
        String beginTime = slowCountBean.getStartTime();
        String endTime = slowCountBean.getEndTime();

        List list = tailList();
        List elkLogTypeList = new ArrayList();
        Indexs indexs;
        for (int i = 0; i < list.size(); i++) {
            indexs = new Indexs(i, list.get(i).toString(), "");
            elkLogTypeList.add(indexs);
        }
        Gson gson = new Gson();
        String s1 = gson.toJson(elkLogTypeList);
        JSONArray jsonArray = JSONArray.fromObject(s1);
        JSONObject jsonObject = jsonArray.getJSONObject(Integer.parseInt(index));
        String indexName = jsonObject.get(RequestFieldsBean.getNAME()).toString();

        //按时间进行范围查询
        QueryBuilder qbTime = QueryBuilders.rangeQuery(FieldBean.getCREATTIME()).from(beginTime).to(endTime);
        //按请求4-5秒时间进行统计
        QueryBuilder qb1 = QueryBuilders.rangeQuery(FieldBean.getOFFSET()).from(NumberIdBean.getFOURTHOUSANDONE()).to(NumberIdBean.getFIVETHOUSAND(), true);

        //按offset字段进行分组
        AggregationBuilder termsCount = AggregationBuilders.count("offsetCount").field(FieldBean.getOFFSET());

        SearchResponse sr = client.prepareSearch(indexName).
                setQuery(qbTime).
                setQuery(qb1).
                addAggregation(termsCount).execute().actionGet();
        ValueCount valueCount = sr.getAggregations().get("offsetCount");
        Long value1 = valueCount.getValue();
        return value1;
    }

    /**
     * 慢请求统计5-6秒的请求
     */
    @Override
    public Long selectSlowCount6(SlowCountBean slowCountBean) {

        String index = slowCountBean.getIndex();
        String beginTime = slowCountBean.getStartTime();
        String endTime = slowCountBean.getEndTime();

        List list = tailList();
        List elkLogTypeList = new ArrayList();
        Indexs indexs;
        for (int i = 0; i < list.size(); i++) {
            indexs = new Indexs(i, list.get(i).toString(), "");
            elkLogTypeList.add(indexs);
        }
        Gson gson = new Gson();
        String s1 = gson.toJson(elkLogTypeList);
        JSONArray jsonArray = JSONArray.fromObject(s1);
        JSONObject jsonObject = jsonArray.getJSONObject(Integer.parseInt(index));
        String indexName = jsonObject.get(RequestFieldsBean.getNAME()).toString();

        //按时间进行范围查询
        QueryBuilder qbTime = QueryBuilders.rangeQuery(FieldBean.getCREATTIME()).from(beginTime).to(endTime);
        //按请求5-6秒时间进行查询
        QueryBuilder qb1 = QueryBuilders.rangeQuery(FieldBean.getOFFSET()).from(NumberIdBean.getFIVETHOUSANDONE()).to(NumberIdBean.getSIXTHOUSAND(), true);

        //按offset字段进行分组
        AggregationBuilder termsCount = AggregationBuilders.count("offsetCount").field(FieldBean.getOFFSET());

        SearchResponse sr = client.prepareSearch(indexName).
                setQuery(qbTime).
                setQuery(qb1).
                addAggregation(termsCount).execute().actionGet();
        ValueCount valueCount = sr.getAggregations().get("offsetCount");
        Long value1 = valueCount.getValue();
        return value1;
    }





}
