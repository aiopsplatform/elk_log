package com.ai.platform.controller;

import com.ai.platform.service.RealTimeQueryService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 实时查询
 */
@RestController
@RequestMapping(value = "/index")
public class RealTimeQueryController {

    @Autowired
    private RealTimeQueryService realTimeQueryService;

    /**
     * 实时查询，每个一秒接受一个请求，从后台进行查询
     */
    @PostMapping(value = "selectRealTimeQuery")
    @ResponseBody
    public String selectRealTimeQuery(@RequestBody JSONObject jsonObject){

        String realJson = realTimeQueryService.selectRealTimeQuery(jsonObject);

        return realJson;

    }

}
