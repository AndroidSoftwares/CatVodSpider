package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;

import com.github.catvod.bean.Class;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PiKa extends Spider {

    private final String url = "https://www.pikaso.top/";

    @Override
    public void init(Context context, String extend) throws Exception {
        OkHttp.newCall("https://mjv002.com/zh/chinese_IamOverEighteenYearsOld/19/index.html").close();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        Vod vod = new Vod();
        vod.setVodId(ids.get(0));
        List<String> playFrom = new ArrayList<>();
        List<String> playUrls = new ArrayList<>();
        playFrom.add("皮卡");
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



    private String searchContent(String key, String pg) {
        String res = OkHttp.string(url + "/search/?pan=all&type=all&q=" + key + "&page=" + pg);
        List<Vod> list = new ArrayList<>();
        int count = 0;
        for (Element div : Jsoup.parse(res).select("div.search-center-container > div.search-item")) {
            if (count < 2) {
                count++;
                continue;
            }
            String id = div.select("a").attr("href").replace(url, "");
            String name = div.select("a > h2.search-title").text();
            String remark = div.select("a > h2.search-title > img").attr("alt");
            Vod vod = new Vod(id, name, "", remark);
            vod.setVodContent(div.select("div.search-des").toString());
            list.add(vod);
        }
        return Result.string(list);
    }
}
