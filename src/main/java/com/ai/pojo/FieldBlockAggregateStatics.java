package com.ai.pojo;

public final class FieldBlockAggregateStatics {

    private String index;
    private String startTime;
    private String endTime;
    private String field;
    private String timeSlicing;
    private String staticalType;

    public FieldBlockAggregateStatics(String index, String startTime, String endTime, String field, String timeSlicing, String staticalType) {
        this.index = index;
        this.startTime = startTime;
        this.endTime = endTime;
        this.field = field;
        this.timeSlicing = timeSlicing;
        this.staticalType = staticalType;
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

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getTimeSlicing() {
        return timeSlicing;
    }

    public void setTimeSlicing(String timeSlicing) {
        this.timeSlicing = timeSlicing;
    }

    public String getStaticalType() {
        return staticalType;
    }

    public void setStaticalType(String staticalType) {
        this.staticalType = staticalType;
    }
}
