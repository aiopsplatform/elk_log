package com.ai.platform.dao.impl;

import com.ai.platform.config.TransportClientConfig;
import com.ai.platform.dao.QueryIndexDao;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Repository
public class QueryIndexDaoImpl implements QueryIndexDao {

    //查询ES中所有的索引
    public Map<Integer, String> getIndex() {
        TransportClient client = TransportClientConfig.client;
        ActionFuture<IndicesStatsResponse> isr = client.admin().indices().stats(new IndicesStatsRequest().all());
        Set<String> set = isr.actionGet().getIndices().keySet();

        Map<Integer, String> map = new HashMap<>();
        int i = 0;
        for (String set1 : set) {
            map.put(i, set1);
            i++;
        }
        return map;
    }
}
