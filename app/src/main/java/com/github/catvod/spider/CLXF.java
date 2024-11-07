package com.github.catvod.spider;

import android.text.TextUtils;

import com.github.catvod.bean.Class;
import com.github.catvod.bean.Filter;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 磁力先锋
 */
public class CLXF extends Spider {

    private final String siteUrl = "https://clxf.me";

    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", Util.CHROME);
        header.put("Referer", siteUrl + "/");
        return header;
    }

    private Map<String, String> getDetailHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", Util.CHROME);
        return header;
    }


    @Override
    public String detailContent(List<String> ids) throws Exception {
        String vodId = ids.get(0);
        String detailUrl = siteUrl + "/detail.php?" +  vodId;
        String html = OkHttp.string(detailUrl, getDetailHeader());
        Document doc = Jsoup.parse(html);
        Elements sourceList = doc.select("p.card-text.mb-2");
        String circuitName = "磁力先锋";
        Map<String, String> playMap = new LinkedHashMap<>();
        int i = 0;
        for (Element source : sourceList) {
            Elements aList = source.select("a");
            List<String> vodItems = new ArrayList<>();
            for (Element a : aList) {
                String episodeUrl = a.attr("href");
                String episodeName = a.text();
                if (!episodeUrl.toLowerCase().startsWith("magnet")) continue;
                vodItems.add(episodeName + "$" + episodeUrl);
            }
            if (!vodItems.isEmpty()) {
                i++;
                playMap.put(circuitName + i, TextUtils.join("#", vodItems));
            }
        }
        Vod vod = new Vod();
        vod.setVodPlayFrom(TextUtils.join("$$$", playMap.keySet()));
        vod.setVodPlayUrl(TextUtils.join("$$$", playMap.values()));

        return Result.string(vod);
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        return searchContent(key, quick, "1");
    }

    @Override
    public String searchContent(String key, boolean quick, String pg) throws Exception {
        String searchUrl = siteUrl + "/search.php?name=" + key + "&page=" + pg;

        String html = OkHttp.string(searchUrl, getHeader());
        Document doc = Jsoup.parse(html);
        Elements sourceList = doc.select("div.card.border-dashed.border-2.mb-2");

        List<Vod> list = new ArrayList<>();
        for (Element source : sourceList) {
            Elements a = source.select("h5 a");
            if (a.isEmpty()) continue;
            String vid = a.attr("onclick")
                    .replace("xiangqing('", "")
                    .replace("')", "")
                    .replace("'","");
            String [] details = vid.split(",");
            String finalVid = "";
            if (details.length > 1) {
                String sjk = details[0];
                String md5hash = details[1];
                finalVid = "md5hash=" + md5hash + "&sjk=" + sjk;
            }
            String name = a.text();
            Vod vod = new Vod(finalVid, name, "");
            vod.setVodRemarks(source.select("p.card-text.mb-1").text());
            vod.setVodContent(source.select("p.card-text").text());
            list.add(vod);
        }
        return Result.string(list);
    }
}
