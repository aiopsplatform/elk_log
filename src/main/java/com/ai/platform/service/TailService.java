package com.ai.platform.service;

import com.ai.pojo.*;
import org.elasticsearch.search.SearchHit;
import org.springframework.stereotype.Service;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

@Service
public interface TailService {

    //获取所有用户的列表
    List<String> tailList() throws UnknownHostException;

    /**
     * 慢请求统计
     */
    Long selectSlowCount1(SlowCountBean slowCountBean) throws UnknownHostException;

    Long selectSlowCount2(SlowCountBean slowCountBean) throws UnknownHostException;

    Long selectSlowCount3(SlowCountBean slowCountBean) throws UnknownHostException;

    Long selectSlowCount4(SlowCountBean slowCountBean) throws UnknownHostException;

    Long selectSlowCount5(SlowCountBean slowCountBean) throws UnknownHostException;

    Long selectSlowCount6(SlowCountBean slowCountBean) throws UnknownHostException;

}
