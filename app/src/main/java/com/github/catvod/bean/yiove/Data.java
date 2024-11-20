package com.github.catvod.bean.yiove;

import com.github.catvod.bean.Vod;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Data {

    @SerializedName("name")
    private String name;

    @SerializedName("id")
    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    @SerializedName("update_time")
    private String update_time;

    @SerializedName("url")
    private String url;

    @SerializedName("valid")
    private Boolean valid;


    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public Vod vod() {
        Vod vod = new Vod(getId(), getName(), "", getUpdate_time() + " " + getUpdate_time());
        vod.setVodContent("来源：\n" + getUrl());
        return vod;
    }

    public static List<Data> arrayFrom(String str) {
        Type listType = new TypeToken<ArrayList<Data>>() {
        }.getType();
        return new Gson().fromJson(str, listType);
    }

}
