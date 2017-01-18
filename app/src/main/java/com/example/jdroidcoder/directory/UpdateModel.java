package com.example.jdroidcoder.directory;

public class UpdateModel {
    String[] list;
    String last_date;

    public String[] getList() {
        return list;
    }

    public void setList(String[] list) {
        this.list = list;
    }

    public String getLast_date() {
        return last_date;
    }

    public void setLast_date(String last_date) {
        this.last_date = last_date;
    }

    public UpdateModel(String[] list, String last_date) {

        this.list = list;
        this.last_date = last_date;
    }
}
