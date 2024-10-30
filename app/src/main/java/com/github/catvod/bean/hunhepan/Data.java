package com.github.catvod.bean.hunhepan;

import com.github.catvod.bean.Vod;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Data {

    @SerializedName("disk_name")
    private String disk_name;
    @SerializedName("link")
    private String link;
    @SerializedName("update_time")
    private String update_time;
    @SerializedName("disk_type")
    private String disk_type;
    @SerializedName("files")
    private List<String> files;


    public String getDisk_name() {
        return disk_name;
    }

    public void setDisk_name(String disk_name) {
        this.disk_name = disk_name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }


    public String getDisk_type() {
        return disk_type;
    }

    public void setDisk_type(String disk_type) {
        this.disk_type = disk_type;
    }

    public Vod vod() {
        return new Vod(getLink(), getDisk_name(), "", getDisk_type() + " " + getUpdate_time());
    }
    public static List<Data> arrayFrom(String str) {
        Type listType = new TypeToken<ArrayList<Data>>() {}.getType();
        return new Gson().fromJson(str, listType);
    }

}
