package com.github.catvod.spider;

import android.text.TextUtils;

import com.github.catvod.api.QuarkApi;
import com.github.catvod.bean.Class;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Util;
import com.google.gson.JsonObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 木偶哥哥
 * @author zhixc
 */
public class WPMogg extends Spider {

    private final String siteUrl = "https://www.mogg.top";
    private final Pattern regexCategory = Pattern.compile("index.php/vod/type/id/(\\w+).html");
    private final Pattern regexPageTotal = Pattern.compile("\\$\\(\"\\.mac_total\"\\)\\.text\\('(\\d+)'\\);");
    private JsonObject extend;

    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", Util.CHROME);
        return header;
    }

    @Override
    public String homeContent(boolean filter) {
        List<Class> classes = new ArrayList<>();
        Document doc = Jsoup.parse(OkHttp.string(siteUrl, getHeader()));
        Elements elements = doc.select(".nav-link");
        for (Element e : elements) {
            Matcher mather = regexCategory.matcher(e.attr("href"));
            if (mather.find()) {
                classes.add(new Class(mather.group(1), e.text().trim()));
            }
        }
        return Result.string(classes, parseVodListFromDoc(doc));
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        String[] urlParams = new String[]{tid, "", "", "", "", "", "", "", pg, "", "", ""};
        if (extend != null && extend.size() > 0) {
            for (String key : extend.keySet()) {
                urlParams[Integer.parseInt(key)] = extend.get(key);
            }
        }
        Document doc = Jsoup.parse(OkHttp.string(String.format("%s/index.php/vod/show/id/%s/page/%s.html", siteUrl, tid, pg), getHeader()));
        int page = Integer.parseInt(pg), limit = 72, total = 0;
        Matcher matcher = regexPageTotal.matcher(doc.html());
        if (matcher.find()) total = Integer.parseInt(matcher.group(1));
        int count = total <= limit ? 1 : ((int) Math.ceil(total / (double) limit));
        return Result.get().vod(parseVodListFromDoc(doc)).page(page, count, limit, total).string();
    }

    private List<Vod> parseVodListFromDoc(Document doc) {
        List<Vod> list = new ArrayList<>();
        Elements elements = doc.select(".module-item");
        for (Element e : elements) {
            String vodId = e.selectFirst(".video-name a").attr("href");
            String vodPic = e.selectFirst(".module-item-pic > img").attr("data-src");
            String vodName = e.selectFirst(".video-name").text();
            String vodRemarks = e.selectFirst(".module-item-text").text();
            list.add(new Vod(vodId, vodName, vodPic, vodRemarks));
        }
        return list;
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        String vodId = ids.get(0);
        Document doc = Jsoup.parse(OkHttp.string(siteUrl + vodId, getHeader()));

        Vod vod = new Vod();
        vod.setVodId(vodId);

        List<String> shareLinks = doc.select(".module-row-text").eachAttr("data-clipboard-text");
        List<String> shareRealLinks = new ArrayList<>();
        for (int i = 0; i < shareLinks.size(); i++) {
            if (shareLinks.get(i).contains("quark")) {
                shareRealLinks.add(QuarkApi.get().getTransfer(shareLinks.get(i)));
            } else {
                shareRealLinks.add(shareLinks.get(i));
            }
        }
        List<String> playFrom = new ArrayList<>();
        playFrom.add("肉不要钱");
        List<String> playUrls = new ArrayList<>();
        playUrls.add(TextUtils.join("#", shareRealLinks));
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
        String searchURL = siteUrl + String.format("/index.php/vod/search/page/%s/wd/%s.html", pg, URLEncoder.encode(key));
        String html = OkHttp.string(searchURL, getHeader());
        Elements items = Jsoup.parse(html).select(".module-search-item");
        List<Vod> list = new ArrayList<>();
        for (Element item : items) {
            String vodId = item.select(".video-serial").attr("href");
            String name = item.select(".video-serial").attr("title");
            String pic = item.select(".module-item-pic > img").attr("data-src");
            String remark = item.select(".video-tag-icon").text();
            list.add(new Vod(vodId, name, pic, remark));
        }
        return Result.string(list);
    }
}