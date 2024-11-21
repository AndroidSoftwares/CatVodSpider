package com.github.catvod.spider;

import android.content.Context;
import android.os.Build;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.catvod.bean.Class;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CLWQCL extends Spider {

    private final String url = "https://www.btsearch.love";
    private Map<String, String> getHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", Util.CHROME);
        headers.put("Referer", url);
        return headers;
    }

    @Override
    public void init(Context context) throws Exception {
        super.init(context);
        Util.loadWebView(url, new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                SpiderDebug.log(url);
            }
        });
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        List<Class> classes = new ArrayList<>();
        List<Vod> list = new ArrayList<>();
        Document doc = Jsoup.parse(OkHttp.string(url));
        for (Element a : doc.select("ul.menu").get(0).select("li > a")) {
            String typeName = a.text();
            String typeId = a.attr("href");
            if (typeId.contains(url)) classes.add(new Class(typeId.replace(url, ""), typeName));
        }
        for (Element div : doc.select("div.video-item")) {
            String id = div.select("a.video-title").attr("href").replace(url, "");
            String name = div.select("a.video-title").text();
            String pic = div.select("div.thumb > a > img").attr("data-src");
            String remark = div.select("div.date").text();
            list.add(new Vod(id, name, pic, remark));
        }
        return Result.string(classes, list);
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        List<Vod> list = new ArrayList<>();
        String target = pg.equals("1") ? url + tid : url + tid + "/" + pg + ".html";
        Document doc = Jsoup.parse(OkHttp.string(target));
        for (Element div : doc.select("div.video-item")) {
            String id = div.select("a.video-title").attr("href").replace(url, "");
            String name = div.select("a.video-title").text();
            String pic = div.select("div.thumb > a > img").attr("data-src");
            String remark = div.select("div.date").text();
            list.add(new Vod(id, name, pic, remark));
        }
        return Result.string(list);
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        String html = OkHttp.string(url + ids.get(0));
        Document doc = Jsoup.parse(html);
        String pic = doc.select("meta[property=og:image]").attr("content");
        String name = doc.select("meta[property=og:title]").attr("content");
        Vod vod = new Vod();
        vod.setVodId(ids.get(0));
        vod.setVodPic(pic);
        vod.setVodName(name);
        vod.setVodPlayFrom("玩偶姐姐");
        vod.setVodPlayUrl("播放$" + url + ids.get(0));
        return Result.string(vod);
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        return Result.get().url(id).parse().click("document.getElementById('player-wrapper').click()").string();
    }

    @Override
    public boolean manualVideoCheck() throws Exception {
        return true;
    }

    @Override
    public boolean isVideoFormat(String url) throws Exception {
        return !url.contains("afcdn.net") && url.contains(".m3u8");
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
//        return searchContent("/search?keyword=" + key);
        return searchContent(key, quick, "1");
    }

    @Override
    public String searchContent(String key, boolean quick, String pg) throws Exception {
        return searchContent("/search?keyword=" + URLEncoder.encode(key));
    }

    private String searchContent(String query) {
        Util.loadWebView(url + query, new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    view.evaluateJavascript("document.documentElement.outerHTML;", value -> {
                        // 获取到的内容在value中
                        String pageContent = value;
                        // 处理获取到的内容
                        SpiderDebug.log(pageContent);

                    });
                }
//                if (!url.contains("302")){
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            String data = OkHttp.string(url1 + query, getHeader());
//                            SpiderDebug.log(data);
//                        }
//                    }).start();
//
//                }
            }
        });
        String data = OkHttp.string(url + query, getHeader());
        List<Vod> list = new ArrayList<>();
        Document doc = Jsoup.parse(OkHttp.string(url + query, getHeader()));
        for (Element div : doc.select("div.panel.panel-default.border-radius")) {
            String id = div.select("h3.panel-title.link > a").attr("href").replace(url, "");
            String name = div.select("h3.panel-title.link > a").text();
//            String pic = div.select("div.thumb > a > img").attr("data-src");
            String remark = div.select("div.panel-footer > h4 > span").text();
            list.add(new Vod(id, name, "", remark));
        }
        return Result.string(list);
    }
}
