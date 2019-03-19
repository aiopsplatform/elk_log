package com.ai.platform.util;

/**
 * 解决硬编码问题
 */
public class RequestFieldsBean {

    private static String INDEX = "indexes";
    private static String TYPE = "types";
    private static String BEGINTIME = "begin_time";
    private static String ENDTIME = "end_time";
    private static String FIELD = "field";
    private static String QUERYCONDITION = "queryCondition";
    private static String RULE = "rule";
    private static String FIELDS = "fields";
    private static String SYMBOL = "symbol";
    private static String NUMBER = "number";
    private static String NAME = "name";

    public static String getINDEX() {
        return INDEX;
    }

    public static String getTYPE() {
        return TYPE;
    }

    public static String getBEGINTIME() {
        return BEGINTIME;
    }

    public static String getENDTIME() {
        return ENDTIME;
    }

    public static String getFIELD() {
        return FIELD;
    }

    public static String getQUERYCONDITION() {
        return QUERYCONDITION;
    }

    public static String getRULE() {
        return RULE;
    }

    public static String getFIELDS() {
        return FIELDS;
    }

    public static String getSYMBOL() {
        return SYMBOL;
    }

    public static String getNUMBER() {
        return NUMBER;
    }

    public static String getNAME() {
        return NAME;
    }
}
