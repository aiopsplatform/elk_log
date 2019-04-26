package com.ai.platform.dao;

import com.ai.pojo.FieldBlockAggregateStatics;
import com.ai.pojo.FieldCount;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldStatisticsDao {

    List selectFieldsList(String index);

    List fieldsCount(FieldCount fieldCount);

    List timeBlockAggregateStatics(FieldBlockAggregateStatics fieldBlockAggregateStatics);
}
