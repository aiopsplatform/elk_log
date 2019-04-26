package com.ai.platform.controller;

import com.ai.platform.service.ConditionQueryService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 条件查询
 */
@RestController
@RequestMapping(value = "/index")
public class ConditionQueryController {

    @Autowired
    private ConditionQueryService conditionQueryService;

    /**
     * 通过索引名称，开始时间和结束时间查询日志数据
     */
    @PostMapping(value = "selectByIndex")
    @ResponseBody
    public String selectByCondition(@RequestBody JSONObject jsonObject) {
        String selectByConditionList = conditionQueryService.selectByTime(jsonObject);
        return selectByConditionList;
    }

    /**
     * 关键字查询
     */
    @PostMapping(value = "queryKeyword")
    @ResponseBody
    public String queryKeyword(@RequestBody JSONObject jsonObject) {
        String keyWordQuery = conditionQueryService.queryKeyword(jsonObject);
        return keyWordQuery;
    }


    //文件导出功能
    @GetMapping(value = "exportLogs")
    public void testDownload(HttpServletRequest request, HttpServletResponse res) {
        conditionQueryService.testDownload(request, res);
    }


}
