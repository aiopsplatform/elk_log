package com.ai.platform.controller;

import com.ai.platform.service.QueryIndexService;
import com.ai.pojo.Indexs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/index")
public class QuerytIndexes {

    @Autowired
    private QueryIndexService queryIndexService;

    @GetMapping("/getElkLogType")
    public List getElkLogType() throws UnknownHostException {
        List elkLogTypeList = new ArrayList();
        List list = queryIndexService.tailList();
        Indexs indexs;
        for (int i = 0; i < 1; i++) {
            if (list.get(i).toString().contains("nginx")) {
                indexs = new Indexs(i, list.get(i).toString(), "Nginx日志");
                elkLogTypeList.add(indexs);
            }
        }
        return elkLogTypeList;
    }

}
