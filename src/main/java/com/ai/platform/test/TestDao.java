package com.ai.platform.test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "myconfig")
@PropertySource(value = "classpath:custom.yml")
public class TestDao {

    @Value("${fieldSum}")
    private String fieldSum;

    @Value("${fieldMax}")
    private String fieldMax;

    @Value("${fieldMin}")
    private String fieldMin;

    @Value("${fieldAvg}")
    private String fieldAvg;

    public String getFieldSum() {
        return fieldSum;
    }

    public void setFieldSum(String fieldSum) {
        this.fieldSum = fieldSum;
    }

    public String getFieldMax() {
        return fieldMax;
    }

    public void setFieldMax(String fieldMax) {
        this.fieldMax = fieldMax;
    }

    public String getFieldMin() {
        return fieldMin;
    }

    public void setFieldMin(String fieldMin) {
        this.fieldMin = fieldMin;
    }

    public String getFieldAvg() {
        return fieldAvg;
    }

    public void setFieldAvg(String fieldAvg) {
        this.fieldAvg = fieldAvg;
    }
}
