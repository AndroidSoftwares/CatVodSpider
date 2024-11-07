package com.github.catvod.spider;

import android.text.TextUtils;

import com.github.catvod.bean.Class;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;
import com.github.catvod.net.OkResult;
import com.github.catvod.utils.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CiLiKu extends Spider {

    private final String siteUrl = "https://ciliku.net";

    private Map<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", Util.CHROME);
        headers.put("Referer", siteUrl);
        return headers;
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        Vod vod = new Vod();
        vod.setVodId(ids.get(0));
        List<String> playFrom = new ArrayList<>();
        List<String> playUrls = new ArrayList<>();
        playFrom.add("磁力库");
        playUrls.add(ids.get(0));
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

    private String searchContent(String key, String pg){
        List<Vod> list = this.queryVodList(key, pg, "");

        return Result.string(list);
    }

    private List<Vod> queryVodList(String key, String page, String user) {

        List<Vod> list = new ArrayList<>();
        try{
            int pageNumber = Integer.parseInt(page) - 1;
            String url = siteUrl + "/toSearch";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("page", pageNumber);
            jsonObject.put("size", 15);
            jsonObject.put("keyword", key);
            OkResult result = OkHttp.post(url, jsonObject.toString(), getHeaders());
            JSONObject object = new JSONObject(result.getBody());
            JSONArray jsonArray = object.getJSONObject("data").getJSONArray("content");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                Vod vod = new Vod();
                vod.setVodId("magnet:?xt=urn:btih:" + item.getString("btih"));
                String remarks = convertTimestampToDate(item.getLong("timestamp"));
                vod.setVodRemarks(remarks);
                vod.setVodPic("");
                vod.setVodName(item.getString("name"));
                list.add(vod);
            }
        } catch (Exception e) {
            e.printStackTrace(); // 记录异常，而不是忽略
        }
        return list;
    }
    private String convertTimestampToDate(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = dateFormat.format(date);
        return dateString;
    }
}
