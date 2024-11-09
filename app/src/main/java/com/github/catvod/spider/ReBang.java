package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;

import com.github.catvod.bean.Class;
import com.github.catvod.bean.Filter;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.bean.rebang.Data;
import com.github.catvod.bean.star.Query;
import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReBang extends Spider {

    private static final String siteUrl = "https://api-hot.imsyy.top/";

    private Map<String, String> getHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", Util.CHROME);
        headers.put("Content-Type", "application/json");
        return headers;
    }

    @Override
    public void init(Context context, String extend) {
    }

    @Override
    public String homeContent(boolean filter) throws Exception {

        ArrayList<Class> classes = new ArrayList<>();
        classes.add(new Class("zhihu?cache=true", "知乎"));
        classes.add(new Class("thepaper?cache=true", "澎湃新闻"));
        classes.add(new Class("netease-news?cache=true", "网易新闻"));
        classes.add(new Class("qq-news?cache=true", "腾讯新闻"));
        classes.add(new Class("toutiao?cache=true", "今日头条"));
        classes.add(new Class("weibo?cache=true", "微博"));
        classes.add(new Class("baidu?cache=true", "百度"));
//        classes.add(new Class("zhihu-daily?cache=true", "知乎日报"));
        classes.add(new Class("douyin?cache=true", "抖音"));
        classes.add(new Class("bilibili?cache=true", "哔哩哔哩"));
        classes.add(new Class("36kr?cache=true", "36K"));
        classes.add(new Class("tieba?cache=true", "百度贴吧"));
        classes.add(new Class("juejin?cache=true", "稀土掘金"));
        LinkedHashMap<String, List<Filter>> filters = new LinkedHashMap<>();
        return Result.string(classes, filters);
    }


    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        List<Vod> list = new ArrayList<>();
        if (!Objects.equals(pg, "1")) return Result.string(list);
        String body = OkHttp.string(siteUrl + tid, getHeader());
        List<Data> datas = Data.arrayFrom(new JSONObject(body).getJSONArray("data").toString());
        for (Data data : datas) list.add(data.vod());
        return Result.string(list);
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        Vod vod = new Vod();
        vod.setVodId(ids.get(0));
        List<String> playFrom = new ArrayList<>();
        List<String> playUrls = new ArrayList<>();
        playFrom.add("混合盘");
        playUrls.add(ids.get(0));
        vod.setVodPlayUrl(TextUtils.join("$$$", playUrls));
        vod.setVodPlayFrom(TextUtils.join("$$$", playFrom));
        return Result.string(vod);
    }

}

