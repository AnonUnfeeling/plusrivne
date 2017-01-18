package com.example.jdroidcoder.directory;

import java.util.ArrayList;

public class JSONNotificationModelFromClient {
    private ArrayList<ModelNameList> client;
    private ArrayList<InfoModel> info;

    public ArrayList<InfoModel> getInfo() {
        return info;
    }

    public void setInfo(ArrayList<InfoModel> info) {
        this.info = info;
    }

    public ArrayList<ModelNameList> getClient() {
        return client;
    }

    public void setClient(ArrayList<ModelNameList> client) {
        this.client = client;
    }

    public class InfoModel{
        private String title,short_text,long_text;public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getShort_text() {
            return short_text;
        }

        public void setShort_text(String short_text) {
            this.short_text = short_text;
        }

        public String getLong_text() {
            return long_text;
        }

        public void setLong_text(String long_text) {
            this.long_text = long_text;
        }
    }
}
