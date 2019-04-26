package com.ai.pojo;

public class MaxMinAvg {

    private String timeAgg;
    private double valueAgg;

    public MaxMinAvg(String timeAgg, double valueAgg) {
        this.timeAgg = timeAgg;
        this.valueAgg = valueAgg;
    }

    public String getTimeAgg() {
        return timeAgg;
    }

    public void setTimeAgg(String timeAgg) {
        this.timeAgg = timeAgg;
    }

    public double getValueAgg() {
        return valueAgg;
    }

    public void setValueAgg(double valueAgg) {
        this.valueAgg = valueAgg;
    }
}
