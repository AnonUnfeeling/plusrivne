package com.example.jdroidcoder.directory;

import java.util.Arrays;

public class ModelCategoryList implements Comparable<ModelCategoryList>{

    private String CATEGORY;
    private String[] cat_tags;

    public String[] getCat_tags() {
        return cat_tags;
    }

    public void setCat_tags(String[] cat_tags) {
        this.cat_tags = cat_tags;
    }

    private int id, special;

    public int getSpecial() {
        return special;
    }

    public void setSpecial(int special) {
        this.special = special;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCATEGORY() {
        return CATEGORY;
    }

    public void setCATEGORY(String CATEGORY) {
        this.CATEGORY = CATEGORY;
    }

    public ModelCategoryList(String RUBRIKA) {
        this.CATEGORY = RUBRIKA;
    }

    @Override
    public int compareTo(ModelCategoryList another) {
        return another.getCATEGORY().compareTo(CATEGORY);
    }

    @Override
    public String toString() {
        return "ModelCategoryList{" +
                "CATEGORY='" + CATEGORY + '\'' +
                ", cat_tags=" + Arrays.toString(cat_tags) +
                ", id=" + id +
                ", special=" + special +
                '}';
    }
}
