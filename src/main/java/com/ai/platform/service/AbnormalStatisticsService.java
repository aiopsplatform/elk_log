package com.ai.platform.service;

import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AbnormalStatisticsService {

    List exceptionCount(JSONObject jsonObject);

}
