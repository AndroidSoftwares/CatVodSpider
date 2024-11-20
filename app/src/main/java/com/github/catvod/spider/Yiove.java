package com.github.catvod.spider;

import android.text.TextUtils;

import com.github.catvod.bean.Class;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.bean.yiove.Data;
import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;
import com.github.catvod.net.OkResult;
import com.github.catvod.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Yiove extends Spider {

    private final String siteUrl = "https://shuyuan-api.yiove.com/complex/query";

    private Map<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", Util.CHROME);
        headers.put("Referer", siteUrl);
        return headers;
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        List<Class> classes = new ArrayList<>();
        List<String> typeIds = Collections.singletonList("1");
        List<String> typeNames = Collections.singletonList("综合书源");
        for (int i = 0; i < typeIds.size(); i++) classes.add(new Class(typeIds.get(i), typeNames.get(i)));
        return Result.string(classes, new ArrayList<>());
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        List<Vod> list = this.queryVodList("", pg, "");

        return Result.string(list);
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        //yuedu://booksource/importonline?src=https://shuyuan-api.yiove.com/complex/query/71171?d=1
        Vod vod = new Vod();
        vod.setVodId(ids.get(0));
        List<String> playFrom = new ArrayList<>();
        List<String> playUrls = new ArrayList<>();
        playFrom.add("书源");
        playUrls.add("yuedu://booksource/importonline?src=" + URLEncoder.encode(siteUrl + "/" + ids.get(0) + "?d=1"));
        vod.setVodPlayUrl(TextUtils.join("$$$", playUrls));
        vod.setVodPlayFrom(TextUtils.join("$$$", playFrom));
        return Result.string(vod);
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        return searchContent(key, "1");
    }

    @Override
    public String searchContent(String key, boolean quick, String pg) throws Exception {
        return searchContent(key, pg);
    }

    private String searchContent(String key, String pg) throws JSONException {
        List<Vod> list = this.queryVodList(key, pg, "");

        return Result.string(list);
    }

    private List<Vod> queryVodList(String key, String page, String user) throws JSONException {
            Map<String, String> map = new HashMap<>();
            map.put("page", page);
            map.put("search_key", key);
            map.put("size", "10");


            String body = OkHttp.string(siteUrl + "?page=" + Integer.parseInt(page) + "&search_key=" + URLEncoder.encode(key) + "&size=10", map, getHeaders());
            List<Data> datas = Data.arrayFrom(new JSONObject(body).getJSONObject("data").getJSONArray("book_source").toString());
            List<Vod> list = new ArrayList<>();
            for (Data data : datas) list.add(data.vod());
            return list;
    }
}
