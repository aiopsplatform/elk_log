package com.ai.pojo;

import java.util.List;

public class PageQuery {

    private int page;
    private List list;

    public PageQuery(int page, List list) {
        this.page = page;
        this.list = list;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }
}
