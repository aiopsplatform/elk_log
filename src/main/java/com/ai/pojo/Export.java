package com.ai.pojo;

public final class Export {

    private String indexes;
    private String startTime;
    private String endTime;

    public Export(String indexes, String startTime, String endTime) {
        this.indexes = indexes;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getIndexes() {
        return indexes;
    }

    public void setIndexes(String indexes) {
        this.indexes = indexes;
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
