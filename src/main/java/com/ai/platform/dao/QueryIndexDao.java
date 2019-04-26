package com.ai.platform.dao;

import org.springframework.stereotype.Repository;
import java.util.Map;

@Repository
public interface QueryIndexDao {

    Map<Integer, String> getIndex();
}
