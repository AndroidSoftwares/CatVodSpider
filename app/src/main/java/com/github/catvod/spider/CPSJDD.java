package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;

import com.github.catvod.bean.Class;
import com.github.catvod.bean.Filter;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.bean.hdk.GoodsListMSata;
import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;
import com.github.catvod.net.OkResult;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 聚推客
 */
public class CPSJDD extends Spider {

    private static final String apikey = "shunziyouxuan";
    private static final String siteUrlV2 = "http://v2.api.haodanku.com";
    private static final String siteUrlV3 = "https://v3.api.haodanku.com";
    private static final String ratesUrl = "https://msapi.maishou88.com/api/v1/goods/getTargetUrl";//转链
    private static final String ratesUrlHdk = "http://v2.api.haodanku.com/get_jditems_link";//转链
    private static final String cate = "https://msapi.maishou88.com/api/v1/shop/getGoodsList";//超级搜索
    private static final String supersearch = "https://appapi.maishou88.com/api/v1/homepage/searchList";//超级搜索
    private String page = "1";
    private String searchPage = "1";
    private String jdApp = "openApp.jdMobile://virtual?params=%7B%22url%22:%22replace%22,%22M_sourceFrom%22:%22h5auto%22,%22des%22:%22m%22,%22sourceType%22:%22babel%22,%22msf_type%22:%22auto%22,%22sourceValue%22:%22babel-act%22,%22category%22:%22jump%22%7D";
    private String test = "{\"category\":\"jump\",\"des\":\"m\",\"sourceValue\":\"babel-act\",\"sourceType\":\"babel\",\"url\":\"replace\",\"M_sourceFrom\":\"h5auto\",\"msf_type\":\"auto\"}";

    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("openid", "75c6a35263109a2d5394e4604d65b5cf");
        header.put("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOlwvXC9tc2FwaS5tYWlzaG91ODguY29tXC9hcGlcL3YxXC9hdXRoXC9wYXNzd29yZExvZ2luIiwiaWF0IjoxNzMzNTY2OTkzLCJleHAiOjE3NTk0ODY5OTMsIm5iZiI6MTczMzU2Njk5MywianRpIjoiRklMdVJ3Z2ZzeWxJQnBBSyIsInN1YiI6MTAyMDQwMCwicHJ2IjoiNzExMDdlZTZhYzhlZGViYTBjODFkNTA1MmU3Mjc2NTVkMTdjNmIxMSJ9.H_JuH6WraEhNlDn74vWVMyXcuxem-PX5Un2R3VUJaTM");
        return header;
    }

    @Override
    public void init(Context context, String extend) {
    }

    @Override
    public String homeContent(boolean filter) throws Exception {

        ArrayList<Class> classes = new ArrayList<>();
        classes.add(new Class("61", "精选"));
        classes.add(new Class("53", "热销"));
        classes.add(new Class("57", "数码家电"));
        classes.add(new Class("58", "母婴家具"));
        classes.add(new Class("59", "家居日用"));
        classes.add(new Class("60", "美妆搭配"));
        classes.add(new Class("62", "图文文具"));
        LinkedHashMap<String, List<Filter>> filters = new LinkedHashMap<>();
        return Result.string(classes, filters);
    }


    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        List<Vod> list = new ArrayList<>();
        Map<String, String> params = new HashMap<>();
        params.put("id", tid);
        params.put("page", pg);
        params.put("inviteCode", "222222");
        params.put("isShare", "1");
        params.put("sourceType", "2");
        OkResult body = OkHttp.post(cate, params, new HashMap<>());
        List<GoodsListMSata> datas = GoodsListMSata.arrayFrom(new JSONObject(body.getBody()).getJSONArray("data").toString());
        for (GoodsListMSata data : datas) list.add(data.vod());
        return Result.string(list);
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        Vod vod = new Vod();

        String[] goodsIdAndActivityId = ids.get(0).split("&&&&");
        String goodsId = "";
        String couponUrl = "";
        goodsId = goodsIdAndActivityId[0];
        if (goodsIdAndActivityId.length > 1) {
            couponUrl = goodsIdAndActivityId[1];
        }


        Map<String, String> params = new HashMap<>();
        params.put("apikey", apikey);
        params.put("material_id", goodsId);
        params.put("union_id", "1003924746");
        params.put("pid", "1003924746_4100844162_3101147746");
        params.put("coupon_url", couponUrl);
        String body = OkHttp.post(ratesUrlHdk, params);
        JSONObject data = new JSONObject(body).getJSONObject("data");
        String h5 = data.optString("short_url");
        vod.setVodId(ids.get(0));
        List<String> playFrom = new ArrayList<>();
        List<String> playUrls = new ArrayList<>();
        playFrom.add("京东券");
        playUrls.add(h5);
        playUrls.add(jdApp.replace("replace", h5));
        List<String> playUrl = new ArrayList<>();
        for (int i = 0; i < playFrom.size(); i++) playUrl.add(TextUtils.join("#", playUrls));


        vod.setVodPlayUrl(TextUtils.join("$$$", playUrl));
        vod.setVodPlayFrom(TextUtils.join("$$$", playFrom));
        return Result.string(vod);

    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        return searchContent(key, false, "1");
    }

    @Override
    public String searchContent(String key, boolean quick, String pg) throws Exception {
        List<Vod> list = new ArrayList<>();
        Map<String, String> params = new HashMap<>();
        params.put("page", pg);
        params.put("isCoupon", "1");
        params.put("sourceType", "2");
        params.put("keyword", key);
        //
        OkResult body = OkHttp.post(supersearch, params, getHeader());
        List<GoodsListMSata> datas = GoodsListMSata.arrayFrom(new JSONObject(body.getBody()).getJSONArray("data").toString());
        for (GoodsListMSata data : datas) list.add(data.vod());
        return Result.string(list);
    }

}

