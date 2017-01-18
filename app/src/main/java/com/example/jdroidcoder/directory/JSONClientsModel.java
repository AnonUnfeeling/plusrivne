package com.example.jdroidcoder.directory;

import java.io.Serializable;
import java.util.HashSet;

public class JSONClientsModel implements Serializable{
    private HashSet<ModelNameList> list;

    public HashSet<ModelNameList> getList() {
        return list;
    }

    public void setList(HashSet<ModelNameList> list) {
        this.list = list;
    }

    public JSONClientsModel(HashSet<ModelNameList> list) {
        this.list = list;
    }

    public JSONClientsModel() {
    }
}
