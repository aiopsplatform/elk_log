package com.ai.platform.util;

/**
 * 解决硬编码问题
 */
public class RequestFieldsBean {

    private static String index = "indexes";
    private static String type = "types";
    private static String beginTime = "begin_time";
    private static String endTime = "end_time";
    private static String field = "field";
    private static String chartType = "chartType";
    private static String queryCondition = "queryCondition";
    private static String rule = "rule";
    private static String fields = "fields";
    private static String symbol = "symbol";
    private static String number = "number";

    public static String getIndex() {
        return index;
    }

    public static String getType() {
        return type;
    }

    public static String getBeginTime() {
        return beginTime;
    }

    public static String getEndTime() {
        return endTime;
    }

    public static String getField() {
        return field;
    }

    public static String getChartType() {
        return chartType;
    }

    public static String getQueryCondition() {
        return queryCondition;
    }

    public static String getRule() {
        return rule;
    }

    public static String getFields() {
        return fields;
    }

    public static String getSymbol() {
        return symbol;
    }

    public static String getNumber() {
        return number;
    }
}
