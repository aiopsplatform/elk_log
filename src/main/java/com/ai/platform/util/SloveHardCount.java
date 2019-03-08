package com.ai.platform.util;

public class SloveHardCount {

    public static String clusterName = "cluster.name";
    public static String appName = "my-application";
    public static String inetAddr = "192.168.126.122";
    public static int clientPort = 9300;
    public static String elkIndex = "logstash-nginx-access-log";

    public static String getClusterName() {
        return clusterName;
    }

    public static String getAppName() {
        return appName;
    }

    public static String getInetAddr() {
        return inetAddr;
    }

    public static int getClientPort() {
        return clientPort;
    }

    public static String getElkIndex() {
        return elkIndex;
    }
}
