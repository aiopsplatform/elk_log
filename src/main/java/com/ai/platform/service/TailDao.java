package com.ai.platform.service;

import com.ai.pojo.*;
import org.elasticsearch.search.SearchHit;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

public interface TailDao {

    List<Tail> tailList() throws UnknownHostException; //获取所有用户的列表

    List<Indexs> selectLog() throws UnknownHostException;//根据指定索引文件名称获取对应的所有日志文件

    List getElkLogType() throws UnknownHostException;

    List<SearchHit> selectByIndex(String indexes) throws UnknownHostException;//根据索引名称、开始时间和结束时间进行查询

    List<SearchHit> selectByTime(IndexDate indexDate) throws UnknownHostException;//根据开始时间和结束时间进行查询

    List<SearchHit> selectRealTimeQuery(String indexes) throws UnknownHostException;//实时查询

    Map count(ExceptionCount exceptionCount) throws UnknownHostException;//异常统计

    Long selectSlowCount1(SlowCountBean slowCountBean) throws UnknownHostException;
    Long selectSlowCount2(SlowCountBean slowCountBean) throws UnknownHostException;
    Long selectSlowCount3(SlowCountBean slowCountBean) throws UnknownHostException;
    Long selectSlowCount4(SlowCountBean slowCountBean) throws UnknownHostException;
    Long selectSlowCount5(SlowCountBean slowCountBean) throws UnknownHostException;
    Long selectSlowCount6(SlowCountBean slowCountBean) throws UnknownHostException;
}
