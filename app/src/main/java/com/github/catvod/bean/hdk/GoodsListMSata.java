package com.github.catvod.bean.hdk;


import com.github.catvod.bean.Vod;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GoodsListMSata {
    @SerializedName(value = "goodsId")
    private String goodsId;
    @SerializedName(value = "title")
    private String title;
    @SerializedName(value = "picUrl")
    private String picUrl;
    @SerializedName(value = "couponPrice")
    private String couponPrice;
    @SerializedName(value = "originalPrice")
    private String originalPrice;
    @SerializedName(value = "actualPrice")
    private String actualPrice;
    @SerializedName(value = "activityId")
    private String activityId;

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getCouponPrice() {
        return couponPrice;
    }

    public void setCouponPrice(String couponPrice) {
        this.couponPrice = couponPrice;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(String actualPrice) {
        this.actualPrice = actualPrice;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public Vod vod() {
        Vod vod = new Vod(getGoodsId() + "&&&&" + getActivityId(), getTitle(), getPicUrl(), "券:" + getCouponPrice() + "元 " + "原价:" + getOriginalPrice() + "元 " + "券后:" + getActualPrice() + "元");
//        vod.setVodContent(getItemdesc());
        return vod;
    }

    public static List<GoodsListMSata> arrayFrom(String str) {
        Type listType = new TypeToken<ArrayList<GoodsListMSata>>() {
        }.getType();
        return new Gson().fromJson(str, listType);
    }

}
