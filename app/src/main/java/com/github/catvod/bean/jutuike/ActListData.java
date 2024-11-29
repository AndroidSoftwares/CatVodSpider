package com.github.catvod.bean.jutuike;


import com.github.catvod.bean.Vod;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ActListData {
    @SerializedName(value = "act_id")
    private String act_id;
    @SerializedName("act_name")
    private String act_name;
    @SerializedName("desc")
    private String desc;
    @SerializedName("img")
    private String img;
    @SerializedName("icon")
    private String icon;
    @SerializedName("introduce")
    private String introduce;

    @SerializedName("start_date")
    private String start_date;
    @SerializedName("end_date")
    private String end_date;

    public String getAct_id() {
        return act_id;
    }

    public void setAct_id(String act_id) {
        this.act_id = act_id;
    }

    public String getAct_name() {
        return act_name;
    }

    public void setAct_name(String act_name) {
        this.act_name = act_name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getEnd_date() {
        return end_date;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public Vod vod() {
        Vod vod = new Vod(getAct_id(), getAct_name().replace("CPS", "").replace("+CPA", ""), getIcon(), getStart_date() + "-" + getEnd_date());
        vod.setVodContent(getDesc().replace("CPS", "").replace("CPA", ""));
        return vod;
    }

    public static List<ActListData> arrayFrom(String str) {
        Type listType = new TypeToken<ArrayList<ActListData>>() {
        }.getType();
        return new Gson().fromJson(str, listType);
    }

}
