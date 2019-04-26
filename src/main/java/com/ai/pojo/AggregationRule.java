package com.ai.pojo;

public class AggregationRule {

    private String aggTime;
    private String aggResponse;
    private long aggCount;

    public AggregationRule(String aggTime, String aggResponse, long aggCount) {
        this.aggTime = aggTime;
        this.aggResponse = aggResponse;
        this.aggCount = aggCount;
    }

    public String getAggTime() {
        return aggTime;
    }

    public void setAggTime(String aggTime) {
        this.aggTime = aggTime;
    }

    public String getAggResponse() {
        return aggResponse;
    }

    public void setAggResponse(String aggResponse) {
        this.aggResponse = aggResponse;
    }

    public long getAggCount() {
        return aggCount;
    }

    public void setAggCount(long aggCount) {
        this.aggCount = aggCount;
    }
}
