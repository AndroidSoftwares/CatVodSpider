package com.github.catvod.spider;

import android.text.TextUtils;

import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhixc
 */
public class PanSearch extends Ali {

    private final String URL = "https://www.pansearch.me/";

    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", Util.CHROME);
        return header;
    }

    private Map<String, String> getSearchHeader() {
        Map<String, String> header = getHeader();
        header.put("x-nextjs-data", "1");
        header.put("referer", URL);
        return header;
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        return searchContent(key, quick, "1");
    }

    @Override
    public String searchContent(String key, boolean quick, String pg) throws Exception {
        String html = OkHttp.string(URL, getHeader());
        String data = Jsoup.parse(html).select("script[id=__NEXT_DATA__]").get(0).data();
        String buildId = new JSONObject(data).getString("buildId");
        int page = 0;
        if (pg.equals("1")) page = Integer.parseInt(pg) * 10; else page = (page + 1) * 10;
        String url = URL + "_next/data/" + buildId + "/search.json?keyword=" + URLEncoder.encode(key) + "&offset=" + page  /*+ "&pan=aliyundrive"*/;
        String result = OkHttp.string(url, getSearchHeader());
        JSONArray array = new JSONObject(result).getJSONObject("pageProps").getJSONObject("data").getJSONArray("data");
        List<Vod> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.getJSONObject(i);
            String content = item.optString("content");
            String[] split = content.split("\\n");
            if (split.length == 0) continue;
            String vodId = Jsoup.parse(content).select("a").attr("href");
            String name = split[0].replaceAll("</?[^>]+>", "");
            String remark = item.optString("pan") + " " + item.optString("time");
            String pic = item.optString("image");
            Vod vod = new Vod(vodId, name, pic, remark);
            vod.setVodContent(content);
            list.add(vod);
        }
        return Result.string(list);
    }
    @Override
    public String detailContent(List<String> ids) throws Exception {
        Vod vod = new Vod();
        vod.setVodPlayFrom(TextUtils.join("$$$", ids));
        vod.setVodPlayUrl(TextUtils.join("$$$", ids));
        return Result.string(vod);
//        if (pattern.matcher(ids.get(0)).find()) return super.detailContent(ids);
//        return "";
    }

}
