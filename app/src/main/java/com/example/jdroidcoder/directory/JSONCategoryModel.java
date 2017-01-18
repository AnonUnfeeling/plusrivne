package com.example.jdroidcoder.directory;

import java.util.LinkedList;

public class JSONCategoryModel {
    private LinkedList<ModelCategoryList> list;

    public LinkedList<ModelCategoryList> getList() {
        return list;
    }

    public void setList(LinkedList<ModelCategoryList> list) {
        this.list = list;
    }

    public JSONCategoryModel(LinkedList<ModelCategoryList> list) {
        this.list = list;
    }
}
