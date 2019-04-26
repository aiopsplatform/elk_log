package com.ai.platform.config;

import com.ai.platform.util.SloveHardCountBean;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class TransportClientConfig {

    public static TransportClient client;

    //获取ELK客户端
    @Bean
    public TransportClient getClient() throws UnknownHostException {
        if (client == null) {
            //指定ES集群
            Settings settings = Settings.builder().put(SloveHardCountBean.getClusterName(), SloveHardCountBean.getAPPNAME()).put("client.transport.sniff", true).build();
            //创建访问ES的客户端
            client = new PreBuiltTransportClient(settings).addTransportAddress(new TransportAddress(InetAddress.getByName(SloveHardCountBean.getINETADDR()), SloveHardCountBean.getCLIENTPORT()));
        }
        return client;
    }
}
