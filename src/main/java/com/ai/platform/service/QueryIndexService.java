package com.ai.platform.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface QueryIndexService {

    //获取所有用户的列表
    List<String> tailList();
}
