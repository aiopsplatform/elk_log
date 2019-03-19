package com.ai.platform.util;

public class SloveHardCountBean {

    public static String CLUSTERNAME = "cluster.name";
    public static String APPNAME = "my-application";
    public static String INETADDR = "192.168.126.122";
    public static int CLIENTPORT = 9300;
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
