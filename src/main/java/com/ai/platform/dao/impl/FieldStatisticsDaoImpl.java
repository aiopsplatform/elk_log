package com.ai.platform.dao.impl;

import com.ai.platform.config.TransportClientConfig;
import com.ai.platform.dao.FieldStatisticsDao;
import com.ai.platform.service.QueryIndexService;
import com.ai.platform.test.TestDao;
import com.ai.platform.util.FieldBean;
import com.ai.platform.util.NumberIdBean;
import com.ai.platform.util.RequestFieldsBean;
import com.ai.pojo.*;
import com.google.gson.Gson;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.elasticsearch.search.aggregations.metrics.max.InternalMax;
import org.elasticsearch.search.aggregations.metrics.min.InternalMin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.net.UnknownHostException;
import java.util.*;

@Repository
public class FieldStatisticsDaoImpl implements FieldStatisticsDao {

    @Autowired
    private QueryIndexService queryIndexService;

    @Autowired
    private TestDao testDao;

    /**
     * 根据索引名称查询字段名称
     */
    @Override
    public List selectFieldsList(String index) {
        List<String> list = new ArrayList<>();
        Indexs indexs;
        ImmutableOpenMap<String, MappingMetaData> mappings;
        String mapping = "";
        List list1 = queryIndexService.tailList();
        List<Indexs> elkLogTypeList = new ArrayList<>();
        for (int i = 0; i < list1.size(); i++) {
            indexs = new Indexs(i, list1.get(i).toString(), "");
            elkLogTypeList.add(indexs);
        }
        Gson gson = new Gson();
        String s1 = gson.toJson(elkLogTypeList);
        JSONArray jsonArray = JSONArray.fromObject(s1);
        JSONObject jsonObjectIndex = jsonArray.getJSONObject(Integer.parseInt(index));
        String indexName = jsonObjectIndex.get(RequestFieldsBean.getNAME()).toString();
        try {
            TransportClient client = TransportClientConfig.client;
            mappings = client.admin().cluster()
                    .prepareState().execute().actionGet().getState()
                    .getMetaData().getIndices().get(indexName)
                    .getMappings();
            mapping = mappings.get(FieldBean.getELKTYPE()).source().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = JSONObject.fromObject(mapping);
        String doc = jsonObject.getString(FieldBean.getELKTYPE());
        JSONObject jsonObject1 = JSONObject.fromObject(doc);
        String properties = jsonObject1.getString(FieldBean.getPROPERTIES());
//        JSONObject jsonObject2 = JSONObject.fromObject(properties);
        Map<String, Map<String, String>> map = JSONObject.fromObject(properties);
        Map<String, String> mp = new HashMap<>();
        String key;
        for (Map.Entry<String, Map<String, String>> str : map.entrySet()) {
            if (!str.getKey().contains(FieldBean.getTIMEPSTAMP()) & !str.getKey().contains(FieldBean.getOFFSET()) & !str.getKey().contains(FieldBean.getSOURCE()) & !str.getKey().contains(FieldBean.getTAGS())) {
                key = str.getKey();
                for (Map.Entry<String, String> ms : str.getValue().entrySet()) {
                    if (ms.getKey().equals(FieldBean.getTYPE())) {
                        list.add(key);
//                        //返回字段名称和字段类型
                        mp.put(key, ms.getValue());
                    }
                }
            }
        }
        List<Indexs> ls = new ArrayList<>();
        int indexOf = list.indexOf(FieldBean.getRESPONSE());
        for (int i = 0; i < 1; i++) {
            indexs = new Indexs(i, list.get(indexOf), "");
            ls.add(indexs);
        }
        return ls;
    }


    /**
     * 字段统计
     */
    @Override
    public List fieldsCount(FieldCount fieldCount) {
        //获取查询条件(从IndexDate类中获取)
        //此类条件对应的都是id
        String index = fieldCount.getIndex();
        List listIndex = queryIndexService.tailList();
        List elkLogTypeList = new ArrayList();
        Indexs indexs;
        for (int i = 0; i < listIndex.size(); i++) {
            indexs = new Indexs(i, listIndex.get(i).toString(), "");
            elkLogTypeList.add(indexs);
        }
        Gson gsonIndex = new Gson();
        String s1 = gsonIndex.toJson(elkLogTypeList);
        JSONArray jsonArrayIndex = JSONArray.fromObject(s1);
        JSONObject jsonObjectIndex = jsonArrayIndex.getJSONObject(Integer.parseInt(index));
        String indexName = jsonObjectIndex.get(RequestFieldsBean.getNAME()).toString();
        //获取开始时间
        String beginTime = fieldCount.getBeginTime();
        //获取结束时间
        String endTime = fieldCount.getEndTime();
        //获取请求中携带的字段id
        int fieldNameId = Integer.parseInt(fieldCount.getFieldNameId());
        //获取字段对应的id
        List fieldsList = selectFieldsList(index);
        //根据字段id获取对应的字段名称
        Gson gson = new Gson();
        String json = gson.toJson(fieldsList);
        JSONArray jsonArray = JSONArray.fromObject(json);
        String s = jsonArray.get(fieldNameId).toString();
        JSONObject jsonObject = JSONObject.fromObject(s);
        String fieldName = jsonObject.get(RequestFieldsBean.getNAME()).toString();
        //查询条件为json数组
        JSONArray querysCondition = fieldCount.getQueryCondition();
        //分段规则为string类型
//        String segmentationRules = fieldCount.getRule();
//        String[] ListsubsectionNumerical = segmentationRules.split("-");
//        for (String val : ListsubsectionNumerical) {
//            String first = val;
//        }
        //可以用在分段规则上，按照给定的值进行分段
        AggregationBuilder res = AggregationBuilders
                .range("range")
                .field(fieldName)
                .addUnboundedTo(100).addRange(100, 300).addRange(300, 500).addUnboundedFrom(500);
        //获取复选框查询条件中的字段
        int fieldsId;
        String symbol;
        int number;
        //按照时间范围进行查询
        QueryBuilder rangQuery = QueryBuilders
                .rangeQuery(FieldBean.getCREATTIME())
                .from(beginTime).to(endTime);
        //使用多条件查询
        BoolQueryBuilder blQuerys = QueryBuilders.boolQuery();
        //使用boolQuery中的must().filter()条件
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
//        //聚合统计个数
//        AggregationBuilder arge = AggregationBuilders
//                .count("aCount")
//                .field(fieldName);
        AggregationBuilder termsCount = AggregationBuilders
                .terms("count")
                .field(fieldName);
        SearchResponse searchResponse;
        if (querysCondition == null) {
            searchResponse = TransportClientConfig.client.prepareSearch(indexName).
                    setQuery(rangQuery).
                    addAggregation(termsCount).
                    execute().actionGet();
        } else {
            for (int i = 0; i < querysCondition.size(); i++) {
                //按照字段进行条件查询 10 < x < 20
                //得到的为查询条件中的字段所对应的ID值
                fieldsId = querysCondition.getJSONObject(i).getInt(RequestFieldsBean.getFIELDS());
                //通过ID 和 字段对应的ID List来查询实际的字段名称
                Gson gs = new Gson();
                String jsonFieldId = gs.toJson(fieldsList);
                JSONArray jsArray = JSONArray.fromObject(jsonFieldId);
                String str = jsArray.get(fieldsId).toString();
                JSONObject jsObject = JSONObject.fromObject(str);
                String fieldsName = jsObject.get(RequestFieldsBean.getNAME()).toString();
                //得到符号symbol所对应的ID值
                symbol = querysCondition.getJSONObject(i).getString(RequestFieldsBean.getSYMBOL());
                //得到number 所对应额值
                number = querysCondition.getJSONObject(i).getInt(RequestFieldsBean.getNUMBER());
                QueryBuilder qbSymbol = null;
                if (symbol.equals(NumberIdBean.getZERO())) {
                    qbSymbol = QueryBuilders.termQuery(fieldsName, number);
                }
                if (symbol.equals(NumberIdBean.getONE())) {
                    qbSymbol = QueryBuilders.rangeQuery(fieldsName).lt(number);
                }
                if (symbol.equals(NumberIdBean.getTWO())) {
                    qbSymbol = QueryBuilders.rangeQuery(fieldsName).gt(number);
                }
                if (symbol.equals(NumberIdBean.getTHREE())) {
                    qbSymbol = QueryBuilders.rangeQuery(fieldsName).lte(number);
                }
                if (symbol.equals(NumberIdBean.getFOUR())) {
                    qbSymbol = QueryBuilders.rangeQuery(fieldsName).gte(number);
                }
                //循环将查询条件添加到boolQuery中
                blQuerys = blQuerys
                        .should(qbSymbol);
                boolQuery.must(rangQuery).filter(blQuerys);
            }
            searchResponse = TransportClientConfig.client.prepareSearch(indexName).
                    setQuery(boolQuery).
                    addAggregation(termsCount).
                    execute().actionGet();
        }
        Terms terms = searchResponse.getAggregations().get("count");
        List list = new ArrayList();
        ChartCount chartCount;
        //循环遍历bucket桶
        for (Terms.Bucket entry : terms.getBuckets()) {
            chartCount = new ChartCount(entry.getKey().toString(), entry.getDocCount());
            list.add(chartCount);
        }
        return list;
    }

    /**
     * 按时间分组进行(总和/最大值/最小值/平均值统计)
     *
     * @param fieldBlockAggregateStatics
     * @return
     */
    @Override
    public List timeBlockAggregateStatics(FieldBlockAggregateStatics fieldBlockAggregateStatics) {

        String startTime = fieldBlockAggregateStatics.getStartTime();
        String endTime = fieldBlockAggregateStatics.getEndTime();
        //从前端获取的时间聚合规则(按分钟、小时、天数)
        String timeSlicing = fieldBlockAggregateStatics.getTimeSlicing();
        //从前端获取的统计类型(总和/最大值/最小值/平均值)
        String staticalType = fieldBlockAggregateStatics.getStaticalType();

        //根据前端传来的indexID,查询实际的index名称
        String index = fieldBlockAggregateStatics.getIndex();
        List listIndex = queryIndexService.tailList();
        List elkLogTypeList = new ArrayList();
        Indexs indexs;
        for (int i = 0; i < listIndex.size(); i++) {
            indexs = new Indexs(i, listIndex.get(i).toString(), "");
            elkLogTypeList.add(indexs);
        }
        Gson gsonIndex = new Gson();
        String s1 = gsonIndex.toJson(elkLogTypeList);
        JSONArray jsonArrayIndex = JSONArray.fromObject(s1);
        JSONObject jsonObjectIndex = jsonArrayIndex.getJSONObject(Integer.parseInt(index));
        String indexName = jsonObjectIndex.get(RequestFieldsBean.getNAME()).toString();

        //获取请求中携带的字段id
        int fieldId = Integer.parseInt(fieldBlockAggregateStatics.getField());
        //获取字段对应的id
        List fieldsList = selectFieldsList(index);
        //根据字段id获取对应的字段名称
        Gson gson = new Gson();
        String json = gson.toJson(fieldsList);
        JSONArray jsonArray = JSONArray.fromObject(json);
        String s = jsonArray.get(fieldId).toString();
        JSONObject jsonObject = JSONObject.fromObject(s);
        String fieldName = jsonObject.get(RequestFieldsBean.getNAME()).toString();
        String fieldConfigSum = testDao.getFieldSum();
        String fieldConfigMax = testDao.getFieldMax();
        String fieldConfigMin = testDao.getFieldMin();
        String fieldConfigAvg = testDao.getFieldAvg();

        //按时间范围进行查询!!!必要条件
        //按时间进行范围查询
        QueryBuilder rangQuery = QueryBuilders
                .rangeQuery(FieldBean.getCREATTIME())
                .from(startTime)
                .to(endTime);
        //按异常进行分组
        AggregationBuilder termsBuilder = AggregationBuilders.terms("by_response").field(fieldConfigSum);
        AggregationBuilder dateSlicing;
        AggregationBuilder maxMinAvg;
        SearchResponse response;
        List timeRuleList = null;
        AggregationRule aggregationRule = null;
        MaxMinAvg mma = null;

        switch (staticalType){
            //总和
            case "0":
                if (timeSlicing.equals("minute")) {
                    dateSlicing = AggregationBuilders.dateHistogram("timeAgg").field(FieldBean.getCREATTIME())
                            .dateHistogramInterval(DateHistogramInterval.MINUTE);
                    response = TransportClientConfig.client.prepareSearch(indexName)
                            .setQuery(rangQuery)
                            .addAggregation(dateSlicing.subAggregation(termsBuilder))
//                        .setSize(1)
                            .execute().actionGet();
                    Histogram timeAgg = response.getAggregations().get("timeAgg");
                    for (Histogram.Bucket entry : timeAgg.getBuckets()) {
                        Map<String, Aggregation> map = entry.getAggregations().asMap();
                        StringTerms stringTerms = (StringTerms) map.get("by_response");
                        Iterator<StringTerms.Bucket> it = stringTerms.getBuckets().iterator();
                        while (it.hasNext()) {
                            Terms.Bucket gradBucket = it.next();
                            aggregationRule = new AggregationRule(entry.getKeyAsString(), gradBucket.getKeyAsString(), gradBucket.getDocCount());
                        }
                    }
                }
                if (timeSlicing.equals("hour")) {
                    dateSlicing = AggregationBuilders.dateHistogram("timeAgg").field(FieldBean.getCREATTIME())
                            .dateHistogramInterval(DateHistogramInterval.HOUR);
                    response = TransportClientConfig.client.prepareSearch(indexName)
                            .setQuery(rangQuery)
                            .addAggregation(dateSlicing.subAggregation(termsBuilder))
//                        .setSize(1)
                            .execute().actionGet();
                    Histogram timeAgg = response.getAggregations().get("timeAgg");
                    for (Histogram.Bucket entry : timeAgg.getBuckets()) {
                        Map<String, Aggregation> map = entry.getAggregations().asMap();
                        StringTerms stringTerms = (StringTerms) map.get("by_response");
                        Iterator<StringTerms.Bucket> it = stringTerms.getBuckets().iterator();
                        while (it.hasNext()) {
                            Terms.Bucket gradBucket = it.next();
                            aggregationRule = new AggregationRule(entry.getKeyAsString(), gradBucket.getKeyAsString(), gradBucket.getDocCount());
                        }
                    }
                }
                if (timeSlicing.equals("day")) {
                    dateSlicing = AggregationBuilders.dateHistogram("timeAgg").field(FieldBean.getCREATTIME())
                            .dateHistogramInterval(DateHistogramInterval.DAY);
                    response = TransportClientConfig.client.prepareSearch(indexName)
                            .setQuery(rangQuery)
                            .addAggregation(dateSlicing.subAggregation(termsBuilder))
//                        .setSize(1)
                            .execute().actionGet();
                    Histogram timeAgg = response.getAggregations().get("timeAgg");
                    for (Histogram.Bucket entry : timeAgg.getBuckets()) {
                        Map<String, Aggregation> map = entry.getAggregations().asMap();
                        StringTerms stringTerms = (StringTerms) map.get("by_response");
                        Iterator<StringTerms.Bucket> it = stringTerms.getBuckets().iterator();
                        while (it.hasNext()) {
                            Terms.Bucket gradBucket = it.next();
                            aggregationRule = new AggregationRule(entry.getKeyAsString(), gradBucket.getKeyAsString(), gradBucket.getDocCount());
                        }
                    }
                }
                timeRuleList.add(aggregationRule);
                break;

            //最大值
            case "1":
                if (timeSlicing.equals("minute")) {
                    maxMinAvg = AggregationBuilders.max("maxAgg").field(fieldConfigMax);
                    dateSlicing = AggregationBuilders.dateHistogram("timeAgg").field(FieldBean.getCREATTIME())
                            .dateHistogramInterval(DateHistogramInterval.MINUTE);
                    response = TransportClientConfig.client.prepareSearch(indexName)
                            .setQuery(rangQuery)
                            .addAggregation(dateSlicing.subAggregation(maxMinAvg))
//                        .setSize(1)
                            .execute().actionGet();
                    Histogram timeAgg = response.getAggregations().get("timeAgg");
                    for (Histogram.Bucket entry : timeAgg.getBuckets()) {
                        if (entry.getDocCount() != 0) {
                            System.out.println("---------------------");
                            Map<String, Aggregation> map = entry.getAggregations().asMap();
                            InternalMax max1 = (InternalMax) map.get("maxAgg");
                            System.out.println(entry.getKey() + ":" + max1.getValue());
                            mma = new MaxMinAvg(entry.getKeyAsString(), max1.getValue());
                        }
                    }
                }
                if (timeSlicing.equals("hour")) {
                    maxMinAvg = AggregationBuilders.max("maxAgg").field(fieldConfigMax);
                    dateSlicing = AggregationBuilders.dateHistogram("timeAgg").field(FieldBean.getCREATTIME())
                            .dateHistogramInterval(DateHistogramInterval.HOUR);
                    response = TransportClientConfig.client.prepareSearch(indexName)
                            .setQuery(rangQuery)
                            .addAggregation(dateSlicing.subAggregation(maxMinAvg))
//                        .setSize(1)
                            .execute().actionGet();
                    Histogram timeAgg = response.getAggregations().get("timeAgg");
                    for (Histogram.Bucket entry : timeAgg.getBuckets()) {
                        if (entry.getDocCount() != 0) {
                            System.out.println("---------------------");
                            Map<String, Aggregation> map = entry.getAggregations().asMap();
                            InternalMax max1 = (InternalMax) map.get("maxAgg");
                            System.out.println(entry.getKey() + ":" + max1.getValue());
                            mma = new MaxMinAvg(entry.getKeyAsString(), max1.getValue());
                        }
                    }
                }
                if (timeSlicing.equals("day")) {
                    maxMinAvg = AggregationBuilders.max("maxAgg").field(fieldConfigMax);
                    dateSlicing = AggregationBuilders.dateHistogram("timeAgg").field(FieldBean.getCREATTIME())
                            .dateHistogramInterval(DateHistogramInterval.DAY);
                    response = TransportClientConfig.client.prepareSearch(indexName)
                            .setQuery(rangQuery)
                            .addAggregation(dateSlicing.subAggregation(maxMinAvg))
//                        .setSize(1)
                            .execute().actionGet();
                    Histogram timeAgg = response.getAggregations().get("timeAgg");
                    for (Histogram.Bucket entry : timeAgg.getBuckets()) {
                        if (entry.getDocCount() != 0) {
                            System.out.println("---------------------");
                            Map<String, Aggregation> map = entry.getAggregations().asMap();
                            InternalMax max1 = (InternalMax) map.get("maxAgg");
                            System.out.println(entry.getKey() + ":" + max1.getValue());
                            mma = new MaxMinAvg(entry.getKeyAsString(), max1.getValue());
                        }
                    }
                }
                timeRuleList.add(mma);
                break;
            //最小值
            case "2":
                if (timeSlicing.equals("minute")) {
                    maxMinAvg = AggregationBuilders.min("minAgg").field(fieldConfigMin);
                    dateSlicing = AggregationBuilders.dateHistogram("timeAgg").field(FieldBean.getCREATTIME())
                            .dateHistogramInterval(DateHistogramInterval.MINUTE);
                    response = TransportClientConfig.client.prepareSearch(indexName)
                            .setQuery(rangQuery)
                            .addAggregation(dateSlicing.subAggregation(maxMinAvg))
//                        .setSize(1)
                            .execute().actionGet();
                    Histogram timeAgg = response.getAggregations().get("timeAgg");
                    for (Histogram.Bucket entry : timeAgg.getBuckets()) {
                        if (entry.getDocCount() != 0) {
                            Map<String, Aggregation> map = entry.getAggregations().asMap();
                            InternalMin min = (InternalMin) map.get("minAgg");
                            mma = new MaxMinAvg(entry.getKeyAsString(), min.getValue());
                        }
                    }
                }
                if (timeSlicing.equals("hour")) {
                    maxMinAvg = AggregationBuilders.max("minAgg").field(fieldConfigMin);
                    dateSlicing = AggregationBuilders.dateHistogram("timeAgg").field(FieldBean.getCREATTIME())
                            .dateHistogramInterval(DateHistogramInterval.HOUR);
                    response = TransportClientConfig.client.prepareSearch(indexName)
                            .setQuery(rangQuery)
                            .addAggregation(dateSlicing.subAggregation(maxMinAvg))
//                        .setSize(1)
                            .execute().actionGet();
                    Histogram timeAgg = response.getAggregations().get("timeAgg");
                    for (Histogram.Bucket entry : timeAgg.getBuckets()) {
                        if (entry.getDocCount() != 0) {
                            Map<String, Aggregation> map = entry.getAggregations().asMap();
                            InternalMin min = (InternalMin) map.get("minAgg");
                            mma = new MaxMinAvg(entry.getKeyAsString(), min.getValue());
                        }
                    }
                }
                if (timeSlicing.equals("day")) {
                    maxMinAvg = AggregationBuilders.max("minAgg").field(fieldConfigMin);
                    dateSlicing = AggregationBuilders.dateHistogram("timeAgg").field(FieldBean.getCREATTIME())
                            .dateHistogramInterval(DateHistogramInterval.DAY);
                    response = TransportClientConfig.client.prepareSearch(indexName)
                            .setQuery(rangQuery)
                            .addAggregation(dateSlicing.subAggregation(maxMinAvg))
//                        .setSize(1)
                            .execute().actionGet();
                    Histogram timeAgg = response.getAggregations().get("timeAgg");
                    for (Histogram.Bucket entry : timeAgg.getBuckets()) {
                        if (entry.getDocCount() != 0) {
                            Map<String, Aggregation> map = entry.getAggregations().asMap();
                            InternalMin min = (InternalMin) map.get("minAgg");
                            mma = new MaxMinAvg(entry.getKeyAsString(), min.getValue());
                        }
                    }
                }
                timeRuleList.add(mma);
                break;
            //平均值
            case "3":
                if (timeSlicing.equals("minute")) {
                    maxMinAvg = AggregationBuilders.min("avgAgg").field(fieldConfigAvg);
                    dateSlicing = AggregationBuilders.dateHistogram("timeAgg").field(FieldBean.getCREATTIME())
                            .dateHistogramInterval(DateHistogramInterval.MINUTE);
                    response = TransportClientConfig.client.prepareSearch(indexName)
                            .setQuery(rangQuery)
                            .addAggregation(dateSlicing.subAggregation(maxMinAvg))
//                        .setSize(1)
                            .execute().actionGet();
                    Histogram timeAgg = response.getAggregations().get("timeAgg");
                    for (Histogram.Bucket entry : timeAgg.getBuckets()) {
                        if (entry.getDocCount() != 0) {
                            Map<String, Aggregation> map = entry.getAggregations().asMap();
                            InternalAvg avg = (InternalAvg) map.get("avgAgg");
                            mma = new MaxMinAvg(entry.getKeyAsString(), avg.getValue());
                        }
                    }
                }
                if (timeSlicing.equals("hour")) {
                    maxMinAvg = AggregationBuilders.max("avgAgg").field(fieldConfigAvg);
                    dateSlicing = AggregationBuilders.dateHistogram("timeAgg").field(FieldBean.getCREATTIME())
                            .dateHistogramInterval(DateHistogramInterval.HOUR);
                    response = TransportClientConfig.client.prepareSearch(indexName)
                            .setQuery(rangQuery)
                            .addAggregation(dateSlicing.subAggregation(maxMinAvg))
//                        .setSize(1)
                            .execute().actionGet();
                    Histogram timeAgg = response.getAggregations().get("timeAgg");
                    for (Histogram.Bucket entry : timeAgg.getBuckets()) {
                        if (entry.getDocCount() != 0) {
                            Map<String, Aggregation> map = entry.getAggregations().asMap();
                            InternalAvg avg = (InternalAvg) map.get("avgAgg");
                            mma = new MaxMinAvg(entry.getKeyAsString(), avg.getValue());
                        }
                    }
                }
                if (timeSlicing.equals("day")) {
                    maxMinAvg = AggregationBuilders.max("avgAgg").field(fieldConfigAvg);
                    dateSlicing = AggregationBuilders.dateHistogram("timeAgg").field(FieldBean.getCREATTIME())
                            .dateHistogramInterval(DateHistogramInterval.DAY);
                    response = TransportClientConfig.client.prepareSearch(indexName)
                            .setQuery(rangQuery)
                            .addAggregation(dateSlicing.subAggregation(maxMinAvg))
//                        .setSize(1)
                            .execute().actionGet();
                    Histogram timeAgg = response.getAggregations().get("timeAgg");
                    for (Histogram.Bucket entry : timeAgg.getBuckets()) {
                        if (entry.getDocCount() != 0) {
                            Map<String, Aggregation> map = entry.getAggregations().asMap();
                            InternalAvg avg = (InternalAvg) map.get("avgAgg");
                            mma = new MaxMinAvg(entry.getKeyAsString(), avg.getValue());
                        }
                    }
                }
                timeRuleList.add(mma);
                break;
        }
        return timeRuleList;
    }


}
