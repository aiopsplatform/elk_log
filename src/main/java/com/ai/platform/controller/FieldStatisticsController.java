package com.ai.platform.controller;

import com.ai.platform.service.FieldStatisticsService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字段统计
 */
@RestController
@RequestMapping(value = "/index")
public class FieldStatisticsController {

    @Autowired
    private FieldStatisticsService fieldStatisticsService;


    /**
     * 通过索引名称获取所有字段的名称(和字段类型)
     */
    @PostMapping(value = "getIndexMetaData")
    @ResponseBody
    public List getIndexMetaData(@RequestBody JSONObject jsonObject) {

        List fieldNameList = fieldStatisticsService.getIndexMetaData(jsonObject);

        return fieldNameList;
    }

    /**
     * 字段统计(第一大类)
     *
     * @param jsonObject
     * @return
     */
    @PostMapping(value = "fieldStatistics")
    @ResponseBody
    public List selectFieldCount(@RequestBody JSONObject jsonObject) {
        List fieldCountList = fieldStatisticsService.selectFieldCount(jsonObject);
        return fieldCountList;
    }


    @PostMapping(value = "fieldStatisticsService")
    @ResponseBody
    public List timeAgg(@RequestBody JSONObject jsonObject) {
        List timeAgg = fieldStatisticsService.timeAggMma(jsonObject);
        return timeAgg;
    }


}
