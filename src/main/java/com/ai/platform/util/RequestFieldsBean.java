package com.ai.platform.util;

/**
 * 解决硬编码问题
 */
public class RequestFieldsBean {

    private static final String INDEX = "indexes";
    private static final String TYPE = "types";
    private static final String BEGINTIME = "begin_time";
    private static final String ENDTIME = "end_time";
    private static final String FIELD = "field";
    private static final String QUERYCONDITION = "queryCondition";
    private static final String RULE = "rule";
    private static final String FIELDS = "fields";
    private static final String SYMBOL = "symbol";
    private static final String NUMBER = "number";
    private static final String NAME = "name";
    private static final String PAGE = "page";
    private static final String KEYWORD = "keyword";
    private static final String TIMESLICING = "timeSlicing";
    private static final String STATICALTYPE = "staticalType";

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

    public static String getPAGE() {
        return PAGE;
    }

    public static String getKEYWORD() {
        return KEYWORD;
    }

    public static String getTIMESLICING() {
        return TIMESLICING;
    }

    public static String getSTATICALTYPE() {
        return STATICALTYPE;
    }
}
