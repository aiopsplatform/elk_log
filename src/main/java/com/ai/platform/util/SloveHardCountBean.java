package com.ai.platform.util;

public class SloveHardCountBean {

    public static final String CLUSTERNAME = "cluster.name";
    public static final String APPNAME = "my-application";
    public static final String INETADDR = "192.168.126.122";
    public static final int CLIENTPORT = 9300;
//    public static String ELKINDEX = "logstash-nginx-access-log";

    public static String getClusterName() {
        return CLUSTERNAME;
    }

    public static String getAPPNAME() {
        return APPNAME;
    }

    public static String getINETADDR() {
        return INETADDR;
    }

    public static int getCLIENTPORT() {
        return CLIENTPORT;
    }

//    public static String getELKINDEX() {
//        return ELKINDEX;
//    }
}
