package com.ai.pojo;

public final class SlowCountBean {

    private String index;
    private String startTime;
    private String endTime;

    public SlowCountBean(String index, String startTime, String endTime) {
        this.index = index;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
