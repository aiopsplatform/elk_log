package com.ai.platform.service;

import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;


@Service
public interface RealTimeQueryService {
    String selectRealTimeQuery(JSONObject jsonObject);
}
