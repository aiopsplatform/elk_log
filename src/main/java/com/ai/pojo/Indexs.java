package com.ai.pojo;

public final class Indexs {

    private Integer id;

    private String name;

    private String describe;

    public Indexs(Integer id, String name, String describe) {
        this.id = id;
        this.name = name;
        this.describe = describe;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
