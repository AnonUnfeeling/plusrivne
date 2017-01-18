package com.example.jdroidcoder.directory;

import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;

public class ModelNameList implements Serializable, Comparable<ModelNameList> {
    private int id, ico;
    private String name, specialize, alias, town, street, build, add_address, map_address, avatar, cover, short_description;
    private String[] phone, email, category, website, photos, tags, top;

    private Map socials, shedule;

    public int getIco() {
        return ico;
    }

    public void setIco(int ico) {
        this.ico = ico;
    }

    public String getSpecialize() {
        return specialize;
    }

    public void setSpecialize(String specialize) {
        this.specialize = specialize;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getAdd_address() {
        return add_address;
    }

    public void setAdd_address(String add_address) {
        this.add_address = add_address;
    }

    public String getMap_address() {
        return map_address;
    }

    public void setMap_address(String map_address) {
        this.map_address = map_address;
    }

    public String[] getTop() {
        return top;
    }

    public void setTop(String[] top) {
        this.top = top;
    }

    public String[] getCategory() {
        return category;
    }

    public void setCategory(String[] category) {
        this.category = category;
    }

    public String[] getWebsite() {
        return website;
    }

    public String[] getPhotos() {
        return photos;
    }

    public void setPhotos(String[] photos) {
        this.photos = photos;
    }

    public Map getSocials() {
        return socials;
    }

    public void setSocials(Map socials) {
        this.socials = socials;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public Map getShedule() {
        return shedule;
    }

    public void setShedule(Map shedule) {
        this.shedule = shedule;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setWebsite(String[] website) {
        this.website = website;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getPhone() {
        return phone;
    }

    public void setPhone(String[] phone) {
        this.phone = phone;
    }

    public String[] getEmail() {
        return email;
    }

    public void setEmail(String[] email) {
        this.email = email;
    }

    public String getAllTel() {
        String tels = "";

        for (int i = 0; i < phone.length; i++) {
            tels += phone[i];
        }
        return tels;
    }

    @Override
    public int compareTo(ModelNameList another) {
        return another.getName().compareTo(this.name);
    }

    @Override
    public String toString() {
        return "ModelNameList{" +
                "id=" + id +
                ", ico=" + ico +
                ", name='" + name + '\'' +
                ", specialize='" + specialize + '\'' +
                ", alias='" + alias + '\'' +
                ", town='" + town + '\'' +
                ", street='" + street + '\'' +
                ", build='" + build + '\'' +
                ", add_address='" + add_address + '\'' +
                ", map_address='" + map_address + '\'' +
                ", top='" + Arrays.toString(top) + '\'' +
                ", avatar='" + avatar + '\'' +
                ", cover='" + cover + '\'' +
                ", short_description='" + short_description + '\'' +
                ", phone=" + Arrays.toString(phone) +
                ", email=" + Arrays.toString(email) +
                ", category=" + Arrays.toString(category) +
                ", website=" + Arrays.toString(website) +
                ", photos=" + Arrays.toString(photos) +
                ", tags=" + Arrays.toString(tags) +
                ", shedule=" + shedule +
                ", socials=" + socials +
                '}';
    }
}
