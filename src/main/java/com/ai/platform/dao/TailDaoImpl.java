package com.ai.platform.dao;


import com.ai.platform.service.TailDao;
import com.ai.platform.util.FieldBean;
import com.ai.platform.util.RequestFieldsBean;
import com.ai.platform.util.SloveHardCount;
import com.ai.pojo.*;
import com.google.gson.Gson;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.stereotype.Repository;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@Repository
public class TailDaoImpl implements TailDao {

    //获取ELK客户端
    public static TransportClient getClient() throws UnknownHostException {
        //指定ES集群
        Settings settings = Settings.builder().put(SloveHardCount.getClusterName(), SloveHardCount.getAppName()).build();
        //创建访问ES的客户端
        TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(new TransportAddress(InetAddress.getByName(SloveHardCount.getInetAddr()), SloveHardCount.getClientPort()));
        return client;
    }


    //查询ES中所有的索引
    private Map<Integer, String> getIndex() throws UnknownHostException {
        TransportClient client = getClient();
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
    public List<String> tailList() throws UnknownHostException {

        Map map1 = this.getIndex();

        return new ArrayList<String>(map1.values());
    }


    /**
     * 根据索引名称、开始时间和结束时间进行查询
     */
    @Override
    public List<SearchHit> selectByTime(IndexDate indexDate) throws UnknownHostException {

        List indexByTimeList = new ArrayList();

        TransportClient client = getClient();
        String indexesName = indexDate.getIndexes();
        String startTime = indexDate.getStartTime();
        String endTime = indexDate.getEndTime();

        String indexName = null;
        if (indexesName.equals("0")) {
            indexName = tailList().get(0);

        }

        RangeQueryBuilder qb = QueryBuilders.rangeQuery(FieldBean.getCreatTime()).from(startTime).to(endTime);
        SearchResponse sr = client.prepareSearch(indexName).setQuery(qb).execute().actionGet();
        SearchHits hits = sr.getHits();
        for (SearchHit hit : hits) {
            indexByTimeList.add(hit);
        }
        return indexByTimeList;
    }


    /**
     * 实时查询数据
     *
     * @param indexes
     * @return
     * @throws UnknownHostException
     */
    @Override
    public List<SearchHit> selectRealTimeQuery(String indexes) throws UnknownHostException {

        TransportClient client = getClient();
        List realTimeList = new ArrayList();

        //根据前端传来的indexes判断id的值，同时确定indexes的真实索引名称
        String indexName = null;

        if (indexes.equals("0")) {
            indexName = tailList().get(0);
        }

        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        SearchResponse searchResponse = client.prepareSearch(indexName).
                setQuery(queryBuilder).
                addSort(FieldBean.getCreatTime(), SortOrder.DESC).
                setSize(2).execute().actionGet();
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits) {
            realTimeList.add(hit);
        }

        return realTimeList;
    }


    /**
     * 异常统计
     */
    @Override
    public Map count(ExceptionCount exceptionCount) throws UnknownHostException {
        TransportClient client = getClient();
        Map map = new HashMap();

        String indexes = exceptionCount.getIndexName();
        String indexType = exceptionCount.getIndexType();
        String beginTime = exceptionCount.getBegin_time();
        String endTime = exceptionCount.getEnd_time();

        String indexName = null;
        if (indexes.equals("0")) {
            indexName = tailList().get(0);
        }

        String type = null;
        if (indexType.equals("1")) {
            type = FieldBean.getElkType();
        }

        //按时间进行范围查询
        QueryBuilder qb1 = QueryBuilders.rangeQuery(FieldBean.getCreatTime()).from(beginTime).to(endTime);

        //按异常进行分组聚合
        AggregationBuilder termsBuilder = AggregationBuilders.terms("by_response").field(FieldBean.getResponse());

        SearchResponse searchResponse = client.prepareSearch(indexName).
                setTypes(type).
                setQuery(qb1).
                addAggregation(termsBuilder).
                execute().actionGet();

        Terms terms = searchResponse.getAggregations().get("by_response");

        //循环遍历bucket桶
        for (Terms.Bucket entry : terms.getBuckets()) {
            map.put(entry.getKey().toString(), entry.getDocCount());
        }
        System.out.println(map);
        return map;
    }


    /**
     * 慢请求统计0-1秒的请求
     */
    @Override
    public Long selectSlowCount1(SlowCountBean slowCountBean) throws UnknownHostException {

        TransportClient client = getClient();
        String index = slowCountBean.getIndex();
        String beginTime = slowCountBean.getStartTime();
        String endTime = slowCountBean.getEndTime();

        String indexName = null;
        if (index.equals("0")) {
            indexName = tailList().get(0);
        }

        //按时间进行范围查询
        QueryBuilder qbTime = QueryBuilders.rangeQuery(FieldBean.getCreatTime()).from(beginTime).to(endTime);
        //按请求0-1秒时间进行统计
        QueryBuilder qb1 = QueryBuilders.rangeQuery(FieldBean.getOffset()).from(0).to(1000, true);

        //按offset字段进行分组
        AggregationBuilder termsCount = AggregationBuilders.count("offsetCount").field(FieldBean.getOffset());

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
    public Long selectSlowCount2(SlowCountBean slowCountBean) throws UnknownHostException {

        TransportClient client = getClient();
        String index = slowCountBean.getIndex();
        String beginTime = slowCountBean.getStartTime();
        String endTime = slowCountBean.getEndTime();

        String indexName = null;
        if (index.equals("0")) {
            indexName = tailList().get(0);
        }

        //按时间进行范围查询
        QueryBuilder qbTime = QueryBuilders.rangeQuery(FieldBean.getCreatTime()).from(beginTime).to(endTime);
        //按请求1-2秒时间进行统计
        QueryBuilder qb1 = QueryBuilders.rangeQuery(FieldBean.getOffset()).from(1001).to(2000, true);

        //按offset字段进行分组
        AggregationBuilder termsCount = AggregationBuilders.count("offsetCount").field(FieldBean.getOffset());

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
    public Long selectSlowCount3(SlowCountBean slowCountBean) throws UnknownHostException {

        TransportClient client = getClient();
        String index = slowCountBean.getIndex();
        String beginTime = slowCountBean.getStartTime();
        String endTime = slowCountBean.getEndTime();

        String indexName = null;
        if (index.equals("0")) {
            indexName = tailList().get(0);
        }

        //按时间进行范围查询
        QueryBuilder qbTime = QueryBuilders.rangeQuery(FieldBean.getCreatTime()).from(beginTime).to(endTime);
        //按请求2-3秒时间进行统计
        QueryBuilder qb1 = QueryBuilders.rangeQuery(FieldBean.getOffset()).from(2001).to(3000, true);

        //按offset字段进行分组
        AggregationBuilder termsCount = AggregationBuilders.count("offsetCount").field(FieldBean.getOffset());

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
    public Long selectSlowCount4(SlowCountBean slowCountBean) throws UnknownHostException {

        TransportClient client = getClient();
        String index = slowCountBean.getIndex();
        String beginTime = slowCountBean.getStartTime();
        String endTime = slowCountBean.getEndTime();

        String indexName = null;
        if (index.equals("0")) {
            indexName = tailList().get(0);
        }

        //按时间进行范围查询
        QueryBuilder qbTime = QueryBuilders.rangeQuery(FieldBean.getCreatTime()).from(beginTime).to(endTime);
        //按请求3-4秒时间进行统计
        QueryBuilder qb1 = QueryBuilders.rangeQuery(FieldBean.getOffset()).from(3001).to(4000, true);

        //按offset字段进行分组
        AggregationBuilder termsCount = AggregationBuilders.count("offsetCount").field(FieldBean.getOffset());

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
    public Long selectSlowCount5(SlowCountBean slowCountBean) throws UnknownHostException {

        TransportClient client = getClient();
        String index = slowCountBean.getIndex();
        String beginTime = slowCountBean.getStartTime();
        String endTime = slowCountBean.getEndTime();

        String indexName = null;
        if (index.equals("0")) {
            indexName = tailList().get(0);
        }

        //按时间进行范围查询
        QueryBuilder qbTime = QueryBuilders.rangeQuery(FieldBean.getCreatTime()).from(beginTime).to(endTime);
        //按请求4-5秒时间进行统计
        QueryBuilder qb1 = QueryBuilders.rangeQuery(FieldBean.getOffset()).from(4001).to(5000, true);

        //按offset字段进行分组
        AggregationBuilder termsCount = AggregationBuilders.count("offsetCount").field(FieldBean.getOffset());

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
    public Long selectSlowCount6(SlowCountBean slowCountBean) throws UnknownHostException {

        TransportClient client = getClient();
        String index = slowCountBean.getIndex();
        String beginTime = slowCountBean.getStartTime();
        String endTime = slowCountBean.getEndTime();

        String indexName = null;
        if (index.equals("0")) {
            indexName = tailList().get(0);
        }

        //按时间进行范围查询
        QueryBuilder qbTime = QueryBuilders.rangeQuery(FieldBean.getCreatTime()).from(beginTime).to(endTime);
        //按请求5-6秒时间进行查询
        QueryBuilder qb1 = QueryBuilders.rangeQuery(FieldBean.getOffset()).from(5001).to(6000, true);

        //按offset字段进行分组
        AggregationBuilder termsCount = AggregationBuilders.count("offsetCount").field(FieldBean.getOffset());

        SearchResponse sr = client.prepareSearch(indexName).
                setQuery(qbTime).
                setQuery(qb1).
                addAggregation(termsCount).execute().actionGet();
        ValueCount valueCount = sr.getAggregations().get("offsetCount");
        Long value1 = valueCount.getValue();
        return value1;
    }


    /**
     * 根据索引名称查询字段名称
     */
    @Override
    public List selectFieldsList(String index) {
        List<String> list = new ArrayList();
        Indexs indexs;
        ImmutableOpenMap<String, MappingMetaData> mappings;
        String mapping = "";
        String indexName = null;


        if (index.equals("0")){
            try {
                indexName = tailList().get(0);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        try {
            TransportClient client = getClient();
            mappings = client.admin().cluster()
                    .prepareState().execute().actionGet().getState()
                    .getMetaData().getIndices().get(indexName)
                    .getMappings();
            mapping = mappings.get(FieldBean.getElkType()).source().toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = JSONObject.fromObject(mapping);
        String doc = jsonObject.getString(FieldBean.getElkType());
        JSONObject jsonObject1 = JSONObject.fromObject(doc);
        String properties = jsonObject1.getString(FieldBean.getProperties());
        JSONObject jsonObject2 = JSONObject.fromObject(properties);
        Map<String, Map<String, String>> map = jsonObject2;

        Map mp = new HashMap();
        for (Map.Entry<String, Map<String, String>> str : map.entrySet()) {
            if (!str.getKey().contains(FieldBean.getTimepstamp()) & !str.getKey().contains(FieldBean.getOffset()) & !str.getKey().contains(FieldBean.getSource()) & !str.getKey().contains(FieldBean.getTags())) {
                String key = str.getKey();
                for (Map.Entry<String, String> ms : str.getValue().entrySet()) {
                    if (ms.getKey().equals(FieldBean.getType())) {
                        list.add(key);
//                        //返回字段明后才能和字段类型
//                        mp.put(key, ms.getValue());
                    }
                }
            }
        }
        List ls = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            indexs = new Indexs(i, list.get(i));
            ls.add(indexs);
        }

        return ls;
    }

    /**
     * 字段统计
     */
    @Override
    public List fieldsCount(FieldCount fieldCount) {

        TransportClient client = null;
        Map map = new HashMap();

        try {
            client = getClient();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //获取查询条件(从IndexDate类中获取)
        //此类条件对应的都是id
        String index = fieldCount.getIndex();
        String indexName = null;
        if (index.equals("0")){
            try {
                indexName = tailList().get(0);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        String beginTime = fieldCount.getBeginTime();
        String endTime = fieldCount.getEndTime();
        int fieldNameId =Integer.parseInt(fieldCount.getFieldNameId());

        //获取字段对应的id
        List fieldsList = selectFieldsList(index);

        //根据字段id获取对应的字段名称
        Gson gson = new Gson();
        String json = gson.toJson(fieldsList);
        JSONArray jsonArray = JSONArray.fromObject(json);
        String s = jsonArray.get(fieldNameId).toString();
        JSONObject jsonObject = JSONObject.fromObject(s);
        String fieldName = jsonObject.get("name").toString();

        //json数组
        JSONObject querysCondition = fieldCount.getQueryCondition();
        if (querysCondition.equals("")){
            querysCondition=null;
        }

        //获取复选框查询条件中的字段
        String fields;
        String symbol;
        int number;

        //按照时间范围进行查询
        QueryBuilder rangQuery = QueryBuilders
                        .rangeQuery(FieldBean.getCreatTime())
                        .from(beginTime).to(endTime);

        //使用多条件查询
        BoolQueryBuilder boolQuerys = QueryBuilders.boolQuery();

        for (int i = 0; i < querysCondition.size(); i++) {
            //按照字段进行条件查询 10 < x < 20
            fields = querysCondition.get(RequestFieldsBean.getFields()).toString();
            symbol = querysCondition.get(RequestFieldsBean.getSymbol()).toString();
            number = Integer.parseInt(querysCondition
                    .get(RequestFieldsBean.getNumber())
                    .toString());
            QueryBuilder qbEq = null;
            QueryBuilder qbGt = null;
            QueryBuilder qbLt = null;
            QueryBuilder qbLte = null;
            QueryBuilder qbGte = null;

            if (symbol.equals("1")) {
                qbEq = QueryBuilders.matchPhraseQuery(fields, number);
        }
            if (symbol.equals("2")) {
                qbGt = QueryBuilders.rangeQuery(fields).gt(number);
            }
            if (symbol.equals("3")) {
                qbLt = QueryBuilders.rangeQuery(fields).lt(number);
            }
            if (symbol.equals("4")) {
                qbLte = QueryBuilders.rangeQuery(fields).lte(number);
            }
            if (symbol.equals("5")) {
                qbGte = QueryBuilders.rangeQuery(fields).gte(number);
            }
            boolQuerys = boolQuerys
                    .must(qbEq)
                    .must(qbGt)
                    .must(qbLt)
                    .must(qbLte)
                    .must(qbGte);
        }


        //可以用在分段规则上
//        AggregationBuilder agg = AggregationBuilders
//                .range("range")
//                .field(fieldName)
//                .addUnboundedTo().addRange(, ).addUnboundedFrom();

        AggregationBuilder termsCount = AggregationBuilders
                .terms("count")
                .field(fieldName);

//        AggregationBuilder arge = AggregationBuilders
//                .count("aCount")
//                .field(fieldName);



        SearchResponse searchResponse = client.prepareSearch(indexName).
                setQuery(rangQuery).
                setQuery(boolQuerys).
                addAggregation(termsCount).
                execute().actionGet();

        Terms terms = searchResponse.getAggregations().get("count");

        List list = new ArrayList();
        ChartCount chartCount;
        //循环遍历bucket桶
        for (Terms.Bucket entry : terms.getBuckets()) {
            chartCount = new ChartCount(entry.getKey().toString(), entry.getDocCount());
            list.add(chartCount);
        }
        //System.out.println(list);
        return list;
    }




}
