package com.github.catvod.spider;

import android.text.TextUtils;

import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Util;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 云盘盘  阿里云
 */
public class WPYpPan extends Ali {
    public static final Pattern pattern2 = Pattern.compile("(https?://)?(www.aliyundrive.com|www.alipan.com)/s/([^/]+)(/folder/([^/]+))?");

    private final String host = Util.base64Decode("aHR0cHM6Ly93d3cueXBwYW4uY29t");
    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", Util.CHROME);
        return header;
    }
    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        return searchContent(key, quick, "1");
    }

    private static String getRemarks(Element i) {
        Elements select = i.select(".post-meta span");
        return select.get(0).text() + " " + select.get(2).text();
    }

    @Override
    public String searchContent(String key, boolean quick, String pg) throws Exception {
        String url = host;
        if (!Objects.equals(pg, "1")) {
            url = url + "/page/" + pg;
        }
        url += "/?s=" + URLEncoder.encode(key, Charset.defaultCharset().name());
        String doc = OkHttp.string(url, getHeader());
        Document document = Jsoup.parse(doc);
        Elements items = document.body().select("#page #main #content .post");
        ArrayList<Vod> vodList = new ArrayList<>();
        for (int i = items.size() - 1; i >= 0; i--) {
            Element item = items.get(i);
            Elements one = item.select(".post-title a");
            Vod vod = new Vod(one.attr("href"), one.text(), "", getRemarks(item));
            vodList.add(vod);

        }

        return Result.string(vodList);
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        String doc = OkHttp.string(ids.get(0), getHeader());
        Elements document = Jsoup.parse(doc).body().select("#page #main #content");
        Vod vod = new Vod();
        vod.setVodId(ids.get(0));
        vod.setVodName(document.select(".post-title").text());
        vod.setVodPic("");
        Elements entry = document.select(".entry p");
        String vodContent = "";
        if(!entry.isEmpty()) vodContent += entry.get(1).text().replace("资源简介：", "");
        if(entry.size() >= 4) {
            vodContent += entry.get(1).text();
            vod.setVodTag(entry.get(3).text().replace("关键词：", "").replace("#", ""));
        }
        vod.setVodContent(vodContent);
        System.out.println(entry.get(2).text());
        Matcher matcher = pattern2.matcher(entry.get(2).text());
        boolean b = matcher.find();
//        String test = matcher.group();
        List<String> name = new ArrayList<>();
        name.add("阿里");
        List<String> url = new ArrayList<>();
        url.add(matcher.group());
        vod.setVodPlayFrom(TextUtils.join("$$$", name));
        vod.setVodPlayUrl(TextUtils.join("$$$", url));
        return Result.string(vod);

//        List<String> list = new ArrayList<>(matcher.group().length());
//        vod.setVodPlayFrom(detailContentVodPlayFrom(list));
//        vod.setVodPlayUrl(detailContentVodPlayUrl(list));
//        return "";
    }
}
