package com.ai.platform.service;

import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FieldStatisticsService {

    List getIndexMetaData(JSONObject jsonObject);

    List selectFieldCount(JSONObject jsonObject);

    List timeAggMma(JSONObject jsonObject);
}
