package com.ai.pojo;

public class IndexDate {

    private String indexes;
    private String startTime;
    private String endTime;
    private int page;

    public IndexDate(String indexes, String startTime, String endTime, int page) {
        this.indexes = indexes;
        this.startTime = startTime;
        this.endTime = endTime;
        this.page = page;
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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
