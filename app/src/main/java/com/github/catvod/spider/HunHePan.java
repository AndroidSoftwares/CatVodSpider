package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;

import com.github.catvod.api.QuarkApi;
import com.github.catvod.bean.Class;
import com.github.catvod.bean.Filter;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.bean.hunhepan.Data;
import com.github.catvod.bean.star.Card;
import com.github.catvod.bean.star.Person;
import com.github.catvod.bean.star.Query;
import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;
import com.github.catvod.net.OkResult;
import com.github.catvod.utils.Json;
import com.github.catvod.utils.Util;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HunHePan extends Spider {

    private static final String apiCate = "https://api.hunhepan.com/v1/extab/list_all";
    private static final String apiCateContent = "https://api.hunhepan.com/v1/extab/raw_disks/";
    private static final String apiSearchContent = "https://hunhepan.com/open/search/disk";
    private static final String apiUrl = "https://aws.ulivetv.net/v3/web/api/filter";
    private static final String siteUrl = "https://www.histar.tv/";
    private static final String detail = siteUrl + "vod/detail/";
    private static final String data = "_next/data/";

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
        JsonArray data = Json.parse(OkHttp.string(apiCate)).getAsJsonObject().getAsJsonArray("data");
        for (int i = 0; i < data.size(); i++) {
            classes.add(new Class(data.get(i).getAsJsonObject().get("id").getAsString(), data.get(i).getAsJsonObject().get("name").getAsString()));
        }
        LinkedHashMap<String, List<Filter>> filters = new LinkedHashMap<>();
        return Result.string(classes, filters);
    }


    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {

        Query query = new Query();
        query.setPageSize(16);
        query.setPage(Integer.parseInt(pg));
        String body = OkHttp.string(apiCateContent + tid + "?page=" + Integer.parseInt(pg));
        List<Data> datas = Data.arrayFrom(new JSONObject(body).getJSONObject("data").getJSONArray("list").toString());
        List<Vod> list = new ArrayList<>();
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
        if (ids.get(0).contains("quark")) {
            playUrls.add(QuarkApi.get().getTransfer(ids.get(0)));
        } else {
            playUrls.add(ids.get(0));

        }
        vod.setVodPlayUrl(TextUtils.join("$$$", playUrls));
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
        Map<String, Object> params = new HashMap<>();
//        params.put("exact", false);
//        params.put("filter", true);
//        params.put("from", "web");
        params.put("page", Integer.parseInt(pg));
        params.put("q", key);
//        params.put("time", "");
//        params.put("type", "");
//        params.put("user_id", 0);


        OkResult json = OkHttp.post(apiSearchContent, new Gson().toJson(params), getHeader());
        JSONArray jsonArray = new JSONObject(json.getBody()).getJSONObject("data").getJSONArray("list");
        for (int i = 0; i < jsonArray.length(); i++) {
            if (!jsonArray.getJSONObject(i).getString("link").contains("tb.cn")){

               String disk_type = jsonArray.getJSONObject(i).getString("disk_type");
               String remarks = disk_type;
                if (disk_type.equals("UC")){
                    remarks = "UC网盘";
                } else if (disk_type.equals("QUARK")) {
                    remarks = "夸克网盘";
                } else if (disk_type.equals("XUNLEI")){
                    remarks = "迅雷网盘";
                } else if (disk_type.equals("ALY")){
                    remarks = "阿里云盘";
                } else if (disk_type.equals("BDY")){
                    remarks = "百度云";
                }
                String time = jsonArray.getJSONObject(i).getString("update_time");
                if (time.contains("T")) time = time.split("T")[0];

                list.add(new Vod(jsonArray.getJSONObject(i).getString("link"), jsonArray.getJSONObject(i).getString("disk_name").replace("<em>", "").replace("</em>", ""), "", remarks + "  " + time));
            }
        }
        return Result.string(list);
    }

}

