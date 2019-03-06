package com.ai.platform.util;

public class FieldBean {

    private static String type = "doc";
    private static String properties = "properties";
    private static String timepstamp = "@";
    private static String offset = "offset";
    private static String source = "source";
    private static String tags = "tags";
    private static String creatTime = "create_time";
    private static String response = "response";

    public static String getType() {
        return type;
    }

    public static String getProperties() {
        return properties;
    }

    public static String getTimepstamp() {
        return timepstamp;
    }

    public static String getOffset() {
        return offset;
    }

    public static String getSource() {
        return source;
    }

    public static String getTags() {
        return tags;
    }

    public static String getCreatTime() {
        return creatTime;
    }

    public static String getResponse() {
        return response;
    }
}
