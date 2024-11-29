package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;

import com.github.catvod.bean.Class;
import com.github.catvod.bean.Filter;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.bean.jutuike.ActListData;
import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 聚推客
 */
public class CPSJTK extends Spider {

    private static final String apikey = "Mn8d8tIXzOYm3Nzz7KvTpsiBDpNwnYOl";
    private static final String siteUrl = "http://api.jutuike.com/union";


    @Override
    public void init(Context context, String extend) {
    }

    @Override
    public String homeContent(boolean filter) throws Exception {

        ArrayList<Class> classes = new ArrayList<>();
        classes.add(new Class("饿了么", "饿了么"));
        classes.add(new Class("美团", "美团"));
        classes.add(new Class("连锁餐饮", "大牌在线点餐"));
        classes.add(new Class("本地生活", "本地生活"));
        classes.add(new Class("打车出行", "打车出行"));
        classes.add(new Class("电影票", "电影票"));
        classes.add(new Class("快递优惠", "快递优惠"));
        classes.add(new Class("特惠酒店", "特惠酒店"));
//        classes.add(new Class("电商", "电商"));
        LinkedHashMap<String, List<Filter>> filters = new LinkedHashMap<>();
        return Result.string(classes, filters);
    }


    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        List<Vod> list = new ArrayList<>();
        Map<String, String> params = new HashMap<>();
        params.put("apikey", apikey);
        params.put("page", pg);
        params.put("pageSize", "100");
        params.put("cate_name", tid);
        String body = OkHttp.string(siteUrl + "/act_list", params, new HashMap<>());
        List<ActListData> datas = ActListData.arrayFrom(new JSONObject(body).getJSONObject("data").getJSONArray("data").toString());
        for (ActListData data : datas) list.add(data.vod());
        return Result.string(list);
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        Vod vod = new Vod();

        Map<String, String> params = new HashMap<>();
        params.put("apikey", apikey);
        params.put("sid", "pansou");
        params.put("act_id", ids.get(0));
        String body = OkHttp.string(siteUrl + "/act", params, new HashMap<>());
        JSONObject data = new JSONObject(body).getJSONObject("data");
        String h5 = data.optString("h5");
//        String long_h5 = data.optString("long_h5");
        String deeplink = data.optString("deeplink");
        vod.setVodId(ids.get(0));
        List<String> playFrom = new ArrayList<>();
        List<String> playUrls = new ArrayList<>();
        playFrom.add("吃喝玩乐");
        playUrls.add(h5);
//        playUrls.add(long_h5);
        if (!TextUtils.isEmpty(deeplink)) playUrls.add(deeplink);
        List<String> playUrl = new ArrayList<>();
        for (int i = 0; i < playFrom.size(); i++) playUrl.add(TextUtils.join("#", playUrls));


        vod.setVodPlayUrl(TextUtils.join("$$$", playUrl));
        vod.setVodPlayFrom(TextUtils.join("$$$", playFrom));
        return Result.string(vod);
    }

}

