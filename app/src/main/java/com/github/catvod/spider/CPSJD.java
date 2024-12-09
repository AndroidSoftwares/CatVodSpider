package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;

import com.github.catvod.bean.Class;
import com.github.catvod.bean.Filter;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.bean.hdk.GoodsListJDData;
import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 聚推客
 */
public class CPSJD extends Spider {

    private static final String apikey = "shunziyouxuan";
    private static final String siteUrlV2 = "http://v2.api.haodanku.com";
    private static final String siteUrlV3 = "https://v3.api.haodanku.com";
    private static final String ratesUrl = "http://v2.api.haodanku.com/get_jditems_link";//转链
    private static final String supersearch = "http://v2.api.haodanku.com/jd_goods_search";//超级搜索
    private String page = "1";
    private String searchPage = "1";
    private String jdApp = "openApp.jdMobile://virtual?params=%7B%22url%22:%22replace%22,%22M_sourceFrom%22:%22h5auto%22,%22des%22:%22m%22,%22sourceType%22:%22babel%22,%22msf_type%22:%22auto%22,%22sourceValue%22:%22babel-act%22,%22category%22:%22jump%22%7D";


    @Override
    public void init(Context context, String extend) {
    }

    @Override
    public String homeContent(boolean filter) throws Exception {

        ArrayList<Class> classes = new ArrayList<>();
        classes.add(new Class("jd_hot_rank", "实时热榜"));
        classes.add(new Class("get_jd_itemlist", "精选"));
        classes.add(new Class("jd_lowprice_list", "9.9包邮"));
        LinkedHashMap<String, List<Filter>> filters = new LinkedHashMap<>();
        return Result.string(classes, filters);
    }


    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        if ("1".equals(pg)) page = "1";
        List<Vod> list = new ArrayList<>();
        Map<String, String> params = new HashMap<>();
        params.put("apikey", apikey);
        params.put("min_id", page);
        params.put("back", "20");
        params.put("min_size", "20");
        String url = "";
        if (tid.startsWith("jd_hot_rank")) {//实时销量
            url = siteUrlV2 + "/jd_hot_rank";
        }
        if (tid.startsWith("get_jd_itemlist")) {//精选
            url = siteUrlV2 + "/get_jd_itemlist";
        }

        if (tid.startsWith("super_selected")) {//9.9
            url = siteUrlV3 + "/super_selected";
        }
        //
        String body = OkHttp.string(url, params, new HashMap<>());


        JSONObject  joBody = new JSONObject(body);
        page = joBody.getString("min_id");
        List<GoodsListJDData> datas = GoodsListJDData.arrayFrom(new JSONObject(body).getJSONArray("data").toString());
        for (GoodsListJDData data : datas) list.add(data.vod());
        return Result.string(list);
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        Vod vod = new Vod();

        Map<String, String> params = new HashMap<>();
        params.put("apikey", apikey);
        params.put("material_id", ids.get(0));
        params.put("union_id", "1003924746");
        params.put("pid", "1003924746_4100844162_3101147746");
        String body = OkHttp.post(ratesUrl, params);
        JSONObject data = new JSONObject(body).getJSONObject("data");
        String h5 = data.optString("short_url");
        vod.setVodId(ids.get(0));
        List<String> playFrom = new ArrayList<>();
        List<String> playUrls = new ArrayList<>();
        playFrom.add("京东券");
        playUrls.add(h5);
        playUrls.add(jdApp.replace("replace", h5));
        List<String> playUrl = new ArrayList<>();
        for (int i = 0; i < playFrom.size(); i++) playUrl.add(TextUtils.join("#", playUrls));


        vod.setVodPlayUrl(TextUtils.join("$$$", playUrl));
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
        Map<String, String> params = new HashMap<>();
        params.put("apikey", apikey);
        params.put("min_id", searchPage);
        params.put("has_coupon", "1");
        params.put("keyword", URLEncoder.encode(URLEncoder.encode(key)));
        //
        String body = OkHttp.string(supersearch, params, new HashMap<>());

        JSONObject  joBody = new JSONObject(body);
        searchPage = joBody.getString("min_id");
        List<GoodsListJDData> datas = GoodsListJDData.arrayFrom(new JSONObject(body).getJSONArray("data").toString());
        for (GoodsListJDData data : datas) list.add(data.vod());
        return Result.string(list);
    }

}

