package com.ai.platform.dao;

import com.ai.pojo.IndexDate;
import com.ai.pojo.KeyWord;
import com.ai.pojo.Log;
import org.elasticsearch.search.SearchHit;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConditionQueryDao {

    List<SearchHit> selectByTime(IndexDate indexDate);

    List queryKeyWord(KeyWord ky);

    String downLoadLog(Log log);

}
