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
    public List<String> tailList() {
        Map map1 = null;
        try {
            map1 = this.getIndex();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return new ArrayList<String>(map1.values());
    }


    /**
     * 根据索引名称、开始时间和结束时间条件进行查询
     */
    @Override
    public List<SearchHit> selectByTime(IndexDate indexDate) throws UnknownHostException {

//        List indexByTimeList = new ArrayList();

        TransportClient client = getClient();
        String indexesName = indexDate.getIndexes();
        String startTime = indexDate.getStartTime();
        String endTime = indexDate.getEndTime();
        int page = indexDate.getPage();

        List list = tailList();
        List elkLogTypeList = new ArrayList();
        Indexs indexs;
        for (int i = 0; i < list.size(); i++) {
            indexs = new Indexs(i, list.get(i).toString());
            elkLogTypeList.add(indexs);
        }
        Gson gson = new Gson();
        String s1 = gson.toJson(elkLogTypeList);
        JSONArray jsonArray = JSONArray.fromObject(s1);
        JSONObject jsonObject = jsonArray.getJSONObject(Integer.parseInt(indexesName));
        String indexName = jsonObject.get(RequestFieldsBean.getNAME()).toString();

        RangeQueryBuilder qb = QueryBuilders.rangeQuery(FieldBean.getCREATTIME()).from(startTime).to(endTime);
        SearchResponse response = client.prepareSearch(indexName)
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
        List list = new ArrayList();
        for (int j = 0; j < hits.getHits().length; j++) {
            try {
                String message = hits.getHits()[j].getSourceAsMap().get("message").toString();
                list.add(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
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
        List list = tailList();
        List elkLogTypeList = new ArrayList();
        Indexs indexs;
        for (int i = 0; i < list.size(); i++) {
            indexs = new Indexs(i, list.get(i).toString());
            elkLogTypeList.add(indexs);
        }
        Gson gson = new Gson();
        String s1 = gson.toJson(elkLogTypeList);
        JSONArray jsonArray = JSONArray.fromObject(s1);
        JSONObject jsonObject = jsonArray.getJSONObject(Integer.parseInt(indexes));
        String indexName = jsonObject.get(RequestFieldsBean.getNAME()).toString();

        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        SearchResponse searchResponse = client.prepareSearch(indexName).
                setQuery(queryBuilder).
                addSort(FieldBean.getCREATTIME(), SortOrder.DESC).
                setSize(20).execute().actionGet();
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
        String beginTime = exceptionCount.getBegin_time();
        String endTime = exceptionCount.getEnd_time();

        List list = tailList();
        List elkLogTypeList = new ArrayList();
        Indexs indexs;
        for (int i = 0; i < list.size(); i++) {
            indexs = new Indexs(i, list.get(i).toString());
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

        SearchResponse searchResponse = client.prepareSearch(indexName).
                setQuery(qb1).
                addAggregation(termsBuilder).
                execute().actionGet();

        Terms terms = searchResponse.getAggregations().get("by_response");
        ChartCount chartCount;
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

//        for (Object key : map.keySet()) {
//            System.out.println(key + ":" + map.get(key));
//        }
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

        List list = tailList();
        List elkLogTypeList = new ArrayList();
        Indexs indexs;
        for (int i = 0; i < list.size(); i++) {
            indexs = new Indexs(i, list.get(i).toString());
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
    public Long selectSlowCount2(SlowCountBean slowCountBean) throws UnknownHostException {

        TransportClient client = getClient();
        String index = slowCountBean.getIndex();
        String beginTime = slowCountBean.getStartTime();
        String endTime = slowCountBean.getEndTime();

        List list = tailList();
        List elkLogTypeList = new ArrayList();
        Indexs indexs;
        for (int i = 0; i < list.size(); i++) {
            indexs = new Indexs(i, list.get(i).toString());
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
    public Long selectSlowCount3(SlowCountBean slowCountBean) throws UnknownHostException {

        TransportClient client = getClient();
        String index = slowCountBean.getIndex();
        String beginTime = slowCountBean.getStartTime();
        String endTime = slowCountBean.getEndTime();

        List list = tailList();
        List elkLogTypeList = new ArrayList();
        Indexs indexs;
        for (int i = 0; i < list.size(); i++) {
            indexs = new Indexs(i, list.get(i).toString());
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
    public Long selectSlowCount4(SlowCountBean slowCountBean) throws UnknownHostException {

        TransportClient client = getClient();
        String index = slowCountBean.getIndex();
        String beginTime = slowCountBean.getStartTime();
        String endTime = slowCountBean.getEndTime();

        List list = tailList();
        List elkLogTypeList = new ArrayList();
        Indexs indexs;
        for (int i = 0; i < list.size(); i++) {
            indexs = new Indexs(i, list.get(i).toString());
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
    public Long selectSlowCount5(SlowCountBean slowCountBean) throws UnknownHostException {

        TransportClient client = getClient();
        String index = slowCountBean.getIndex();
        String beginTime = slowCountBean.getStartTime();
        String endTime = slowCountBean.getEndTime();

        List list = tailList();
        List elkLogTypeList = new ArrayList();
        Indexs indexs;
        for (int i = 0; i < list.size(); i++) {
            indexs = new Indexs(i, list.get(i).toString());
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
    public Long selectSlowCount6(SlowCountBean slowCountBean) throws UnknownHostException {

        TransportClient client = getClient();
        String index = slowCountBean.getIndex();
        String beginTime = slowCountBean.getStartTime();
        String endTime = slowCountBean.getEndTime();

        List list = tailList();
        List elkLogTypeList = new ArrayList();
        Indexs indexs;
        for (int i = 0; i < list.size(); i++) {
            indexs = new Indexs(i, list.get(i).toString());
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


    /**
     * 根据索引名称查询字段名称
     */
    @Override
    public List selectFieldsList(String index) {
        List<String> list = new ArrayList();
        Indexs indexs;
        ImmutableOpenMap<String, MappingMetaData> mappings;
        String mapping = "";

        List list1 = tailList();
        List elkLogTypeList = new ArrayList();
        for (int i = 0; i < list1.size(); i++) {
            indexs = new Indexs(i, list1.get(i).toString());
            elkLogTypeList.add(indexs);
        }
        Gson gson = new Gson();
        String s1 = gson.toJson(elkLogTypeList);
        JSONArray jsonArray = JSONArray.fromObject(s1);
        JSONObject jsonObjectIndex = jsonArray.getJSONObject(Integer.parseInt(index));
        String indexName = jsonObjectIndex.get(RequestFieldsBean.getNAME()).toString();

        try {
            TransportClient client = getClient();
            mappings = client.admin().cluster()
                    .prepareState().execute().actionGet().getState()
                    .getMetaData().getIndices().get(indexName)
                    .getMappings();
            mapping = mappings.get(FieldBean.getELKTYPE()).source().toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = JSONObject.fromObject(mapping);
        String doc = jsonObject.getString(FieldBean.getELKTYPE());
        JSONObject jsonObject1 = JSONObject.fromObject(doc);
        String properties = jsonObject1.getString(FieldBean.getPROPERTIES());
        JSONObject jsonObject2 = JSONObject.fromObject(properties);
        Map<String, Map<String, String>> map = jsonObject2;

        Map mp = new HashMap();
        String key = null;
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
        List ls = new ArrayList();

        int indexOf = list.indexOf(FieldBean.getRESPONSE());

        for (int i = 0; i < 1; i++) {
            indexs = new Indexs(i, list.get(indexOf));
            ls.add(indexs);
        }

//        for (int i = 0; i < list.size(); i++) {
//            indexs = new Indexs(i, list.get(i));
//            ls.add(indexs);
//        }

        //写死字段名称只能为response
//        Indexs indexs1 = new Indexs(0, FieldBean.getRESPONSE());
//        ls.add(indexs1);


        return ls;
    }

    /**
     * 字段统计
     */
    @Override
    public List fieldsCount(FieldCount fieldCount) {

        TransportClient client = null;
        try {
            client = getClient();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //获取查询条件(从IndexDate类中获取)
        //此类条件对应的都是id
        String index = fieldCount.getIndex();
        List listIndex = tailList();
        List elkLogTypeList = new ArrayList();
        Indexs indexs;
        for (int i = 0; i < listIndex.size(); i++) {
            indexs = new Indexs(i, listIndex.get(i).toString());
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
//
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


        SearchResponse searchResponse ;

        if (querysCondition == null) {
            searchResponse = client.prepareSearch(indexName).
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
            searchResponse = client.prepareSearch(indexName).
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

}
