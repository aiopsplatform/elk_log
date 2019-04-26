package com.ai.platform.controller;

import com.ai.platform.service.AbnormalStatisticsService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 异常统计
 */
@RestController
@RequestMapping(value = "/index")
public class AbnormalStatisticsController {

    @Autowired
    private AbnormalStatisticsService abnormalStatisticsService;

    /**
     * 异常统计
     */
    @PostMapping(value = "exceptionCount")
    @ResponseBody
    public List exceptionCount(@RequestBody JSONObject jsonObject){

        List exceptionCountList = abnormalStatisticsService.exceptionCount(jsonObject);

        return exceptionCountList;
    }

}
