package com.ai.pojo;

public class ExceptionValue {

    private Object name;
    private Object val;

    public ExceptionValue(Object name, Object val) {
        this.name = name;
        this.val = val;
    }

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public Object getVal() {
        return val;
    }

    public void setVal(Object val) {
        this.val = val;
    }
}
