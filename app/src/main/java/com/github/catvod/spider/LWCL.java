package com.github.catvod.spider;

import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 老王磁力
 */
public class LWCL extends Spider {

    private final String siteUrl = "https://laowangpz.top";

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
        String detailUrl = siteUrl + vodId;
        String html = OkHttp.string(detailUrl, getDetailHeader());
        Document doc = Jsoup.parse(html);
        String magnet = doc.select("div.input-group.magnet-box > input.form-control").attr("value");

        Vod vod = new Vod();
        vod.setVodPlayFrom("老王磁力");
        vod.setVodPlayUrl("播放$" + magnet);
        return Result.string(vod);
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        return searchContent(key, quick, "1");
//
//        String searchUrl = siteUrl + "/search?q=" + URLEncoder.encode(key);
//
//        String html = OkHttp.string(searchUrl, getHeader());
//        Document doc = Jsoup.parse(html);
//        Elements sourceList = doc.select("table.table.table-hover.file-list > tbody > tr");
//
//        List<Vod> list = new ArrayList<>();
//        for (Element source : sourceList) {
//            Elements a = source.select("a");
//            String vid = a.attr("href");
//            String name = a.text();
//            Vod vod = new Vod(vid, name, "");
//            vod.setVodRemarks(source.select("td.td-size").text());
//            vod.setVodContent(source.select("p.sample").text());
//            list.add(vod);
//        }
//        return Result.string(list);

    }

    @Override
    public String searchContent(String key, boolean quick, String pg) throws Exception {
        String searchUrl = siteUrl + "/search?keyword=" + key + "&sos=relevance&sofs=all&sot=all&soft=all&som=auto&p=" + pg;

        String html = OkHttp.string(searchUrl, getHeader());
        Document doc = Jsoup.parse(html);
        Elements sourceList = doc.select("div.panel.panel-default.search-panel");

        List<Vod> list = new ArrayList<>();
        for (Element source : sourceList) {
            Elements a = source.select("h3 a");
            if (a.isEmpty()) continue;
            String url = a.attr("href");

            String name = a.text();
            Vod vod = new Vod(url, name, "");
            vod.setVodRemarks(source.select("div.panel-footer pbc").text());
            vod.setVodContent(source.select("div.panel-body").text());
            list.add(vod);
        }
        return Result.string(list);
    }
}
