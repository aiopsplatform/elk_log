package com.ai.pojo;

import java.util.List;

public class ResponseParams {

    private List typeList;
    private List serviceList;
    private List instanceList;
    private List targetList;
    private List nodeList;
    private List ipList;

    public ResponseParams(List typeList, List serviceList, List instanceList, List targetList, List nodeList, List ipList) {
        this.typeList = typeList;
        this.serviceList = serviceList;
        this.instanceList = instanceList;
        this.targetList = targetList;
        this.nodeList = nodeList;
        this.ipList = ipList;
    }

    public List getTypeList() {
        return typeList;
    }

    public void setTypeList(List typeList) {
        this.typeList = typeList;
    }

    public List getServiceList() {
        return serviceList;
    }

    public void setServiceList(List serviceList) {
        this.serviceList = serviceList;
    }

    public List getInstanceList() {
        return instanceList;
    }

    public void setInstanceList(List instanceList) {
        this.instanceList = instanceList;
    }

    public List getTargetList() {
        return targetList;
    }

    public void setTargetList(List targetList) {
        this.targetList = targetList;
    }

    public List getNodeList() {
        return nodeList;
    }

    public void setNodeList(List nodeList) {
        this.nodeList = nodeList;
    }

    public List getIpList() {
        return ipList;
    }

    public void setIpList(List ipList) {
        this.ipList = ipList;
    }

    @Override
    public String toString() {
        return "ResponseParams{" +
                "typeList=" + typeList +
                ", serviceList=" + serviceList +
                ", instanceList=" + instanceList +
                ", targetList=" + targetList +
                ", nodeList=" + nodeList +
                ", ipList=" + ipList +
                '}';
    }
}
