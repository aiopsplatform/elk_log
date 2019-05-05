package com.ai.pojo;

import net.sf.json.JSONArray;

public final class FieldCount {

    private String index;
    private String beginTime;
    private String endTime;
    private String fieldNameId;
    private JSONArray queryCondition;
    private String rule;

    public FieldCount(String index, String beginTime, String endTime, String fieldNameId, JSONArray queryCondition, String rule) {
        this.index = index;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.fieldNameId = fieldNameId;
        this.queryCondition = queryCondition;
        this.rule = rule;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
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

    public String getFieldNameId() {
        return fieldNameId;
    }

    public void setFieldNameId(String fieldNameId) {
        this.fieldNameId = fieldNameId;
    }

    public JSONArray getQueryCondition() {
        return queryCondition;
    }

    public void setQueryCondition(JSONArray queryCondition) {
        this.queryCondition = queryCondition;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }
}
