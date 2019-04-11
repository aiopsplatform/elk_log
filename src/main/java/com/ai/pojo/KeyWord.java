package com.ai.pojo;

public class KeyWord {

    private String indexName;
    private String beginTime;
    private String endTime;
    private String keyWord;

    public KeyWord(String indexName, String beginTime, String endTime, String keyWord) {
        this.indexName = indexName;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.keyWord = keyWord;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }
}
