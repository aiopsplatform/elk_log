package com.ai.platform.dao;

import org.elasticsearch.search.SearchHit;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RealTimeQueryDao {

    List<SearchHit> selectRealTimeQuery(String indexes);
}
