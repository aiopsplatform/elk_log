package com.ai.platform.dao;


import com.ai.platform.service.TailDao;
import com.ai.pojo.*;
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
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.stereotype.Repository;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@Repository
public class TailDaoImpl implements TailDao {

    public static String clusterName = "cluster.name";
    public static String appName = "my-application";
    public static String inetAddr = "192.168.126.122";
    public static int clientPort = 9300;
    public static String elkIndex = "logstash-nginx-access-log";
    public static String elkType = "doc";
    public static Set set;
    public static Map map;

    //获取ELK客户端
    public static TransportClient getClient() throws UnknownHostException {
        //指定ES集群
        Settings settings = Settings.builder().put(clusterName, appName).build();
        //创建访问ES的客户端
        TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(new TransportAddress(InetAddress.getByName(inetAddr), clientPort));
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
    public List<Tail> tailList() throws UnknownHostException {

        Map map1 = this.getIndex();

        return new ArrayList<Tail>(map1.values());
    }


    //根据指定索引文件名称获取对应的所有日志文件
    @Override
    public List<Indexs> selectLog() throws UnknownHostException {
        List ls = new ArrayList();
        TransportClient client = getClient();
        QueryBuilder qb = QueryBuilders.matchAllQuery();
        SearchResponse sr = client.prepareSearch(elkIndex).setQuery(qb).setQuery(qb).setScroll(TimeValue.timeValueMinutes(2)).get();
        SearchHits hits = sr.getHits();
        for (SearchHit hit : hits) {
            ls.add(hit);
        }
        return ls;
    }


    //根据指定的索引文件
    @Override
    public List getElkLogType() throws UnknownHostException {
        List ls = new ArrayList();
        TransportClient client = getClient();
        QueryBuilder qb = QueryBuilders.matchAllQuery();
        SearchResponse sr = client.prepareSearch("elk_log_type").setQuery(qb).setQuery(qb).setScroll(TimeValue.timeValueMinutes(2)).get();
        SearchHits hits = sr.getHits();
        for (SearchHit hit : hits) {
            ls.add(hit.getSourceAsString());
        }
        return ls;
    }


    //根据请求中携带的请求参数indexes对应索引名称后到后台进行索引文件的查询
    @Override
    public List<SearchHit> selectByIndex(String indexes) throws UnknownHostException {
        String indexName = null;

        if (indexes.equals("0")) {
            indexName = "logstash-nginx-access-log";
        } else if (indexes.equals("1")) {
            indexName = "filebeat-6.5.3-2019.01.23";
        } else {
            indexName = ".kibana_1";
        }

        List elkList = new ArrayList();

        TransportClient client = getClient();

        QueryBuilder qb = QueryBuilders.prefixQuery("_index", indexName);
        SearchResponse sr = client.prepareSearch(indexName).setQuery(qb).get();
        SearchHits hits = sr.getHits();
        for (SearchHit hit : hits) {
            elkList.add(hit);
        }
        return elkList;

    }


    /*
    根据索引名称、开始时间和结束时间进行查询
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
            indexName = "logstash-nginx-access-log";
        }


        RangeQueryBuilder qb = QueryBuilders.rangeQuery("create_time").from(startTime).to(endTime);
        SearchResponse sr = client.prepareSearch(indexName).setQuery(qb).execute().actionGet();
        SearchHits hits = sr.getHits();
        for (SearchHit hit : hits) {
            indexByTimeList.add(hit);

        }
        return indexByTimeList;
    }


    /**
     * 实时查询数据
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
            indexName = "logstash-nginx-access-log";
        }

        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        SearchResponse searchResponse = client.prepareSearch(indexName).
                setQuery(queryBuilder).
                addSort("create_time", SortOrder.DESC).
                setSize(2).execute().actionGet();
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits) {
            realTimeList.add(hit);
        }

        return realTimeList;
    }


    /**
     * 异常统计
     *
     */
    @Override
    public Map count(ExceptionCount exceptionCount) throws UnknownHostException{
        TransportClient client = getClient();
        Map map = new HashMap();

        String indexes = exceptionCount.getIndexName();
        String indexType = exceptionCount.getIndexType();
        String beginTime = exceptionCount.getBegin_time();
        String endTime = exceptionCount.getEnd_time();

        String indexName = null;
        if (indexes.equals("0")) {
            indexName = "logstash-nginx-access-log";
        }

        String type = null;
        if(indexType.equals("1")){
            type = "doc";
        }

        //按时间进行范围查询
        QueryBuilder qb1 = QueryBuilders.rangeQuery("create_time").from(beginTime).to(endTime);

        //按异常进行分组聚合
        AggregationBuilder termsBuilder = AggregationBuilders.terms("by_response").field("response");

        SearchResponse searchResponse = client.prepareSearch(indexName).
                setTypes(type).
                setQuery(qb1).
                addAggregation(termsBuilder).
                execute().actionGet();

        Terms terms = searchResponse.getAggregations().get("by_response");

        //循环遍历bucket桶
        for (Terms.Bucket entry: terms.getBuckets() ){
            map.put(entry.getKey().toString(), entry.getDocCount());
        }
        return map;
    }


    /**
     *慢请求统计0-1秒的请求
     */
    @Override
    public Long selectSlowCount1(SlowCountBean slowCountBean) throws UnknownHostException{

        TransportClient client = getClient();
        String index = slowCountBean.getIndex();
        String beginTime = slowCountBean.getStartTime();
        String endTime = slowCountBean.getEndTime();

        String indexName = null;
        if (index.equals("0")) {
            indexName = "logstash-nginx-access-log";
        }

        //按时间进行范围查询
        QueryBuilder qbTime = QueryBuilders.rangeQuery("create_time").from(beginTime).to(endTime);
        //按请求0-1秒时间进行统计
        QueryBuilder qb1 = QueryBuilders.rangeQuery("offset").from(0).to(1000,true);

        //按offset字段进行分组
        AggregationBuilder termsCount = AggregationBuilders.count("offsetCount").field("offset");

        SearchResponse sr = client.prepareSearch(indexName).
                            setQuery(qbTime).
                            setQuery(qb1).
                            addAggregation(termsCount).execute().actionGet();
        ValueCount valueCount = sr.getAggregations().get("offsetCount");
        Long value1 = valueCount.getValue();
        return value1;
    }
    /**
     *慢请求统计1-2秒的请求
     */
    @Override
    public Long selectSlowCount2(SlowCountBean slowCountBean) throws UnknownHostException{

        TransportClient client = getClient();
        String index = slowCountBean.getIndex();
        String beginTime = slowCountBean.getStartTime();
        String endTime = slowCountBean.getEndTime();

        String indexName = null;
        if (index.equals("0")) {
            indexName = "logstash-nginx-access-log";
        }

        //按时间进行范围查询
        QueryBuilder qbTime = QueryBuilders.rangeQuery("create_time").from(beginTime).to(endTime);
        //按请求0-1秒时间进行统计
        QueryBuilder qb1 = QueryBuilders.rangeQuery("offset").from(1001).to(2000,true);

        //按offset字段进行分组
        AggregationBuilder termsCount = AggregationBuilders.count("offsetCount").field("offset");

        SearchResponse sr = client.prepareSearch(indexName).
                setQuery(qbTime).
                setQuery(qb1).
                addAggregation(termsCount).execute().actionGet();
        ValueCount valueCount = sr.getAggregations().get("offsetCount");
        Long value2 = valueCount.getValue();
        return value2;
    }
    /**
     *慢请求统计2-3秒的请求
     */
    @Override
    public Long selectSlowCount3(SlowCountBean slowCountBean) throws UnknownHostException{

        TransportClient client = getClient();
        String index = slowCountBean.getIndex();
        String beginTime = slowCountBean.getStartTime();
        String endTime = slowCountBean.getEndTime();

        String indexName = null;
        if (index.equals("0")) {
            indexName = "logstash-nginx-access-log";
        }

        //按时间进行范围查询
        QueryBuilder qbTime = QueryBuilders.rangeQuery("create_time").from(beginTime).to(endTime);
        //按请求0-1秒时间进行统计
        QueryBuilder qb1 = QueryBuilders.rangeQuery("offset").from(2001).to(3000,true);

        //按offset字段进行分组
        AggregationBuilder termsCount = AggregationBuilders.count("offsetCount").field("offset");

        SearchResponse sr = client.prepareSearch(indexName).
                setQuery(qbTime).
                setQuery(qb1).
                addAggregation(termsCount).execute().actionGet();
        ValueCount valueCount = sr.getAggregations().get("offsetCount");
        Long value1 = valueCount.getValue();
        return value1;
    }
    /**
     *慢请求统计3-4秒的请求
     */
    @Override
    public Long selectSlowCount4(SlowCountBean slowCountBean) throws UnknownHostException{

        TransportClient client = getClient();
        String index = slowCountBean.getIndex();
        String beginTime = slowCountBean.getStartTime();
        String endTime = slowCountBean.getEndTime();

        String indexName = null;
        if (index.equals("0")) {
            indexName = "logstash-nginx-access-log";
        }

        //按时间进行范围查询
        QueryBuilder qbTime = QueryBuilders.rangeQuery("create_time").from(beginTime).to(endTime);
        //按请求0-1秒时间进行统计
        QueryBuilder qb1 = QueryBuilders.rangeQuery("offset").from(3001).to(4000,true);

        //按offset字段进行分组
        AggregationBuilder termsCount = AggregationBuilders.count("offsetCount").field("offset");

        SearchResponse sr = client.prepareSearch(indexName).
                setQuery(qbTime).
                setQuery(qb1).
                addAggregation(termsCount).execute().actionGet();
        ValueCount valueCount = sr.getAggregations().get("offsetCount");
        Long value1 = valueCount.getValue();
        return value1;
    }
    /**
     *慢请求统计4-5秒的请求
     */
    @Override
    public Long selectSlowCount5(SlowCountBean slowCountBean) throws UnknownHostException{

        TransportClient client = getClient();
        String index = slowCountBean.getIndex();
        String beginTime = slowCountBean.getStartTime();
        String endTime = slowCountBean.getEndTime();

        String indexName = null;
        if (index.equals("0")) {
            indexName = "logstash-nginx-access-log";
        }

        //按时间进行范围查询
        QueryBuilder qbTime = QueryBuilders.rangeQuery("create_time").from(beginTime).to(endTime);
        //按请求0-1秒时间进行统计
        QueryBuilder qb1 = QueryBuilders.rangeQuery("offset").from(4001).to(5000,true);

        //按offset字段进行分组
        AggregationBuilder termsCount = AggregationBuilders.count("offsetCount").field("offset");

        SearchResponse sr = client.prepareSearch(indexName).
                setQuery(qbTime).
                setQuery(qb1).
                addAggregation(termsCount).execute().actionGet();
        ValueCount valueCount = sr.getAggregations().get("offsetCount");
        Long value1 = valueCount.getValue();
        return value1;
    }
    /**
     *慢请求统计5-6秒的请求
     */
    @Override
    public Long selectSlowCount6(SlowCountBean slowCountBean) throws UnknownHostException{

        TransportClient client = getClient();
        String index = slowCountBean.getIndex();
        String beginTime = slowCountBean.getStartTime();
        String endTime = slowCountBean.getEndTime();

        String indexName = null;
        if (index.equals("0")) {
            indexName = "logstash-nginx-access-log";
        }

        //按时间进行范围查询
        QueryBuilder qbTime = QueryBuilders.rangeQuery("create_time").from(beginTime).to(endTime);
        //按请求0-1秒时间进行统计
        QueryBuilder qb1 = QueryBuilders.rangeQuery("offset").from(5001).to(6000,true);

        //按offset字段进行分组
        AggregationBuilder termsCount = AggregationBuilders.count("offsetCount").field("offset");

        SearchResponse sr = client.prepareSearch(indexName).
                setQuery(qbTime).
                setQuery(qb1).
                addAggregation(termsCount).execute().actionGet();
        ValueCount valueCount = sr.getAggregations().get("offsetCount");
        Long value1 = valueCount.getValue();
        return value1;
    }


    /**
     * 根据索引名称查询字段名称和类型
     */
    @Override
    public Map selectFieldMap(String index){

        ImmutableOpenMap<String, MappingMetaData> mappings;
        String mapping = "";
        Map mp = new HashMap();
        try {
            TransportClient client = getClient();
            mappings = client.admin().cluster()
                    .prepareState().execute().actionGet().getState()
                    .getMetaData().getIndices().get(index)
                    .getMappings();
            mapping = mappings.get("doc").source().toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = JSONObject.fromObject(mapping);
        String doc = jsonObject.getString("doc");
        JSONObject jsonObject1 = JSONObject.fromObject(doc);
        String properties = jsonObject1.getString("properties");
        JSONObject jsonObject2 = JSONObject.fromObject(properties);
        Map<String,Map<String,String>> map = jsonObject2;
        for (Map.Entry<String,Map<String,String>> str :map.entrySet()){
            if (!str.getKey().contains("@")&!str.getKey().contains("offset")&!str.getKey().contains("source")&!str.getKey().contains("tags")) {
                String key = str.getKey();
                for (Map.Entry<String,String> ms :str.getValue().entrySet()){
                    if (ms.getKey().equals("type")){
                        mp.put(key, ms.getValue());
                    }
                }
            }
        }

        return mp;
    }

}
