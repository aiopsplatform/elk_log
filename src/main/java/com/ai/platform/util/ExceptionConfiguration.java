package com.ai.platform.util;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "com.ai.platform.config")
@PropertySource("classpath:config/exception.properties")
public class ExceptionConfiguration {

    private String types;
    private String indexs;
    private String keyword;

    public ExceptionConfiguration(String types, String indexs, String keyword) {
        this.types = types;
        this.indexs = indexs;
        this.keyword = keyword;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getIndexs() {
        return indexs;
    }

    public void setIndexs(String indexs) {
        this.indexs = indexs;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
