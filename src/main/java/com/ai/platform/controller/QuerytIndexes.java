package com.ai.platform.controller;

import com.ai.platform.service.QueryIndexService;
import com.ai.pojo.Indexs;
import com.ai.pojo.ResponseParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/index")
public class QuerytIndexes {

    @Autowired
    private QueryIndexService queryIndexService;

    @GetMapping("/getElkLogType")
    public ResponseParams getElkLogType() {
        List<Indexs> elkLogTypeList = new ArrayList<>();
        List<String> list = queryIndexService.tailList();
        Indexs indexs1;
        for (int i = 0; i < 1; i++) {
            if (list.get(i).contains("nginx")) {
                indexs1 = new Indexs(i, list.get(i), "Nginx日志");
                elkLogTypeList.add(indexs1);
            }
        }
        Indexs indexs2 = new Indexs(1, "ecp_service_0232", "ecp_service_0232");
        Indexs indexs3 = new Indexs(2, "ecp_instarice_0012", "ecp_instarice_0012");
        Indexs indexs4 = new Indexs(3, "errorPoint", "错误指标");
        Indexs indexs5 = new Indexs(4, "node1", "服务器1");
        Indexs indexs6 = new Indexs(5, "10.10.1.1", "10.10.1.1");

        /**
         * elkLogTypeList --- 类型
         * serviceList --- 服务
         * instanceList --- 实例
         * targetList --- 指标
         * nodeList --- 节点
         * ipList --- ip
         */
        List<Indexs> serviceList = new ArrayList<>();
        serviceList.add(indexs2);
        List<Indexs> instanceList = new ArrayList<>();
        instanceList.add(indexs3);
        List<Indexs> targetList = new ArrayList<>();
        targetList.add(indexs4);
        List<Indexs> nodeList = new ArrayList<>();
        nodeList.add(indexs5);
        List<Indexs> ipList = new ArrayList<>();
        ipList.add(indexs6);
        ResponseParams rps = new ResponseParams(elkLogTypeList, serviceList, instanceList, targetList, nodeList, ipList);
        return rps;
    }

}
