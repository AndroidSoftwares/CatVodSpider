package com.github.catvod.bean.rebang;

import com.github.catvod.bean.Vod;
import com.github.catvod.utils.Util;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Data {

    @SerializedName("title")
    private String title;
    @SerializedName("url")
    private String url;
    @SerializedName("timestamp")
    private String timestamp;
    @SerializedName("cover")
    private String cover;
    @SerializedName("desc")
    private String desc;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Vod vod() {
//        Vod vod = new Vod(getUrl(), getTitle(), getCover(), getTimestamp().isEmpty() ? "" : Util.convertTimestampToDate(Long.parseLong(getTimestamp())));
        Vod vod = new Vod(getUrl(), getTitle(), getCover(), "");
        vod.setVodContent(getDesc());
        if (getTimestamp() != null){
            vod.setVodRemarks(Util.convertTimestampToDate(Long.parseLong(getTimestamp())));
        }
        return vod;
    }
    public static List<Data> arrayFrom(String str) {
        Type listType = new TypeToken<ArrayList<Data>>() {}.getType();
        return new Gson().fromJson(str, listType);
    }

}
