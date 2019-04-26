package com.ai.platform.service;

import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Service
public interface ConditionQueryService {

    String selectByTime(JSONObject jsonObject);

    String queryKeyword(JSONObject jsonObject);

    void testDownload(HttpServletRequest request, HttpServletResponse res);
}
