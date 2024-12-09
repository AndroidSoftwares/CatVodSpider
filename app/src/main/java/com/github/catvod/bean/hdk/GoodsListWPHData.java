package com.github.catvod.bean.hdk;


import com.github.catvod.bean.Vod;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GoodsListWPHData {
    @SerializedName(value = "goodsid")
    private String goodsid;
    @SerializedName(value = "itempic")
    private String itempic;
    @SerializedName(value = "itemtitle")
    private String itemtitle;
    @SerializedName(value = "couponmoney")
    private String couponmoney;
    @SerializedName(value = "itemendprice")
    private String itemendprice;
    @SerializedName(value = "itemprice")
    private String itemprice;

    public String getGoodsid() {
        return goodsid;
    }

    public void setGoodsid(String goodsid) {
        this.goodsid = goodsid;
    }

    public String getItempic() {
        return itempic;
    }

    public void setItempic(String itempic) {
        this.itempic = itempic;
    }


    public String getCouponmoney() {
        return couponmoney;
    }

    public void setCouponmoney(String couponmoney) {
        this.couponmoney = couponmoney;
    }

    public String getItemendprice() {
        return itemendprice;
    }

    public void setItemendprice(String itemendprice) {
        this.itemendprice = itemendprice;
    }


    public String getItemprice() {
        return itemprice;
    }

    public void setItemprice(String itemprice) {
        this.itemprice = itemprice;
    }

    public String getItemtitle() {
        return itemtitle;
    }

    public void setItemtitle(String itemtitle) {
        this.itemtitle = itemtitle;
    }

    public Vod vod() {
        Vod vod = new Vod(getGoodsid(), getItemtitle(), getItempic(), "券:" + getCouponmoney() + "元 " + "原价:" + getItemprice() + "元 " + "券后:" + getItemendprice() + "元");
//        vod.setVodContent(getItemdesc());
        return vod;
    }

    public static List<GoodsListWPHData> arrayFrom(String str) {
        Type listType = new TypeToken<ArrayList<GoodsListWPHData>>() {
        }.getType();
        return new Gson().fromJson(str, listType);
    }

}
