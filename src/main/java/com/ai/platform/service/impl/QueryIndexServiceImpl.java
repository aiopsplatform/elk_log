package com.ai.platform.service.impl;

import com.ai.platform.dao.QueryIndexDao;
import com.ai.platform.service.QueryIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class QueryIndexServiceImpl implements QueryIndexService {

    @Autowired
    private QueryIndexDao queryIndexDao;

    //获取所有索引名称返回给前端
    @Override
    public List<String> tailList() {
        Map map1 = queryIndexDao.getIndex();
        return new ArrayList<String>(map1.values());
    }

}
