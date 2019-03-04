package com.ai.platform.demo;

import com.google.gson.Gson;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demo {

    public static String clusterName = "cluster.name";
    public static String appName = "my-application";
    public static String inetAddr = "192.168.126.122";
    public static int clientPort = 9300;
    public static String elkIndex = "logstash-nginx-access-log";
    public static String elkType = "doc";

    //获取ELK客户端
    public static TransportClient getClient() throws UnknownHostException {
        //指定ES集群
        Settings settings = Settings.builder().put(clusterName, appName).build();
        //创建访问ES的客户端
        TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(new TransportAddress(InetAddress.getByName(inetAddr), clientPort));
        return client;
    }

    public static void main(String[] args) throws Exception {

        TransportClient client = getClient();

        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .field("0", "web")
                .endObject();

        XContentBuilder builder1 = XContentFactory.jsonBuilder()
                .startObject()
                .field("1", "nginx")
                .endObject();

        XContentBuilder builder2 = XContentFactory.jsonBuilder()
                .startObject()
                .field("2", "tomcat")
                .endObject();

        IndexResponse response = client.prepareIndex("elk_log_type", "info").setSource(builder).get();
        IndexResponse response1 = client.prepareIndex("elk_log_type", "info").setSource(builder1).get();
        IndexResponse response2 = client.prepareIndex("elk_log_type", "info").setSource(builder2).get();


    }


    /**
     * 分组求个组数据
     * 第一行 termsBuilder 就相当于根据年龄对数据进行分组 group by
     * 后面对sumBuilder avgBuilder countBuilder等就是在组内 求和 求平均数 求数量
     */
//    AggregationBuilder termsBuilder = AggregationBuilders.terms("by_age").field("age");
//    AggregationBuilder sumBuilder = AggregationBuilders.sum("ageSum").field("age");
//    AggregationBuilder avgBuilder = AggregationBuilders.avg("ageAvg").field("age");
//    AggregationBuilder countBuilder = AggregationBuilders.count("ageCount").field("age");
//
//    termsBuilder.subAggregation(sumBuilder).subAggregation(avgBuilder).subAggregation(countBuilder);
//
//    //TermsAggregationBuilder all = AggregationBuilders.terms("age").field("age");
//    //all.subAggregation(termsBuilder);
//    RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("age").from(30, true).to(36, true);
//    QueryBuilder s = QueryBuilders.boolQuery().must(rangeQueryBuilder);//.must(qb5);
//    SearchRequestBuilder sv = client.prepareSearch("accounts").setTypes("person").setQuery(s).setFetchSource(null, "gender").setFrom(0).setSize(100).addAggregation(termsBuilder);
//        logger.log(Level.INFO,sv.toString());
//    SearchResponse response = sv.get();
//
//    Aggregations terms = response.getAggregations();
//        for(
//    Aggregation a:terms)
//
//    {
//        LongTerms teamSum = (LongTerms) a;
//        for (LongTerms.Bucket bucket : teamSum.getBuckets()) {
//            logger.info(bucket.getKeyAsString() + "   " + bucket.getDocCount() + "    " + ((Sum) bucket.getAggregations().asMap().get("ageSum")).getValue() + "    " + ((Avg) bucket.getAggregations().asMap().get("ageAvg")).getValue() + "    " + ((ValueCount) bucket.getAggregations().asMap().get("ageCount")).getValue());
//
//        }
//    }


}
