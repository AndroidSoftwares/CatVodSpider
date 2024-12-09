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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 聚推客
 */
public class CPSDY extends Spider {

    private static final String ratesUrl = "https://msapi.maishou88.com/api/v1/goods/getTargetUrl";//转链
    private static final String supersearch = "https://appapi.maishou88.com/api/v1/homepage/searchList";//超级搜索
    private static final String cate = "https://msapi.maishou88.com/api/v1/shop/getGoodsList";//超级搜索
    private String page = "1";
    private String searchPage = "1";

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
//        classes.add(new Class("80", "精选推荐"));
//        classes.add(new Class("81", "大牌优选"));
//        classes.add(new Class("82", "品牌黑标"));
//        classes.add(new Class("79", "直推爆款"));
//        classes.add(new Class("78", "千万补贴"));
//        classes.add(new Class("77", "百亿补贴"));
//        classes.add(new Class("76", "好物秒杀"));
        LinkedHashMap<String, List<Filter>> filters = new LinkedHashMap<>();
        return Result.string(classes, filters);
    }


    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        if ("1".equals(pg)) page = "1";
        List<Vod> list = new ArrayList<>();
        Map<String, String> params = new HashMap<>();
        params.put("id", tid);
        params.put("page", pg);
        params.put("inviteCode", "222222");
        params.put("isShare", "1");
        params.put("sourceType", "7");
        String body = OkHttp.string(cate, params, new HashMap<>());
        List<GoodsListMSata> datas = GoodsListMSata.arrayFrom(new JSONObject(body).getJSONArray("data").toString());
        for (GoodsListMSata data : datas) list.add(data.vod());
        return Result.string(list);
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {


        String[] goodsIdAndActivityId = ids.get(0).split("&&&&");
        String goodsId = "";
        String couponUrl = "";
        goodsId = goodsIdAndActivityId[0];
        if (goodsIdAndActivityId.length > 1) {
            couponUrl = goodsIdAndActivityId[1];
        }

        Vod vod = new Vod();

        Map<String, String> params = new HashMap<>();
        params.put("bizSceneId", "");
        params.put("originalConvertUrl", "");
        params.put("supplierCode", "");
        params.put("activityId", couponUrl);
        params.put("sourceType", "7");
        params.put("goodsId", goodsId);
        OkResult body = OkHttp.post(ratesUrl, params, getHeader());
        JSONObject data = new JSONObject(body.getBody()).getJSONObject("data");
        String h5 = data.optString("h5Url");
        String schemaUrl = data.optString("schemaUrl");
        vod.setVodId(ids.get(0));
        List<String> playFrom = new ArrayList<>();
        List<String> playUrls = new ArrayList<>();
        playFrom.add("抖音");
        playUrls.add(h5);
        playUrls.add(schemaUrl);
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
        params.put("pddListId", searchPage);
        params.put("isCoupon", "1");
        params.put("sourceType", "7");
        params.put("keyword", key);
        //
        OkResult body = OkHttp.post(supersearch, params, getHeader());
        List<GoodsListMSata> datas = GoodsListMSata.arrayFrom(new JSONObject(body.getBody()).getJSONArray("data").toString());
        for (GoodsListMSata data : datas) list.add(data.vod());
        return Result.string(list);
    }

}

