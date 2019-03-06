package com.ai.platform.service;

import com.ai.pojo.*;
import org.elasticsearch.search.SearchHit;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

public interface TailDao {

    List<String> tailList() throws UnknownHostException; //获取所有用户的列表

    List<SearchHit> selectByTime(IndexDate indexDate) throws UnknownHostException;//根据索引名称等、开始时间和结束时间进行查询

    List<SearchHit> selectRealTimeQuery(String indexes) throws UnknownHostException;//实时查询

    Map count(ExceptionCount exceptionCount) throws UnknownHostException;//异常统计

    Long selectSlowCount1(SlowCountBean slowCountBean) throws UnknownHostException;
    Long selectSlowCount2(SlowCountBean slowCountBean) throws UnknownHostException;
    Long selectSlowCount3(SlowCountBean slowCountBean) throws UnknownHostException;
    Long selectSlowCount4(SlowCountBean slowCountBean) throws UnknownHostException;
    Long selectSlowCount5(SlowCountBean slowCountBean) throws UnknownHostException;
    Long selectSlowCount6(SlowCountBean slowCountBean) throws UnknownHostException;

    Map selectFieldMap(String index);//根据索引名称查询字段名称和类型
}
