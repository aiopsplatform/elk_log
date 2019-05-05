package com.ai.pojo;

public final class ExceptionCount {

    private String indexName;
    private String begin_time;
    private String end_time;

    public ExceptionCount(String indexName, String begin_time, String end_time) {
        this.indexName = indexName;
        this.begin_time = begin_time;
        this.end_time = end_time;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getBegin_time() {
        return begin_time;
    }

    public void setBegin_time(String begin_time) {
        this.begin_time = begin_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
}
