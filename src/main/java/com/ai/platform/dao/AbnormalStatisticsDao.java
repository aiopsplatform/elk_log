package com.ai.platform.dao;

import com.ai.pojo.ExceptionCount;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface AbnormalStatisticsDao {
    Map<Integer, Long> count(ExceptionCount exceptionCount);
}
