package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;

import com.github.catvod.bean.Class;
import com.github.catvod.bean.Filter;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.bean.hdk.GoodsListTBData;
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
public class CPSTB extends Spider {

    private static final String apikey = "shunziyouxuan";
    private static final String siteUrlV2 = "http://v2.api.haodanku.com";
    private static final String siteUrlV3 = "https://v3.api.haodanku.com";
    private static final String ratesUrl = "http://v3.api.haodanku.com/ratesurl";//转链
    private static final String supersearch = "http://v3.api.haodanku.com/supersearch";//超级搜索
    private String page = "1";
    private String searchPage = "1";
    private String searchPageTB = "1";


    @Override
    public void init(Context context, String extend) {
    }

    @Override
    public String homeContent(boolean filter) throws Exception {

        ArrayList<Class> classes = new ArrayList<>();
//        classes.add(new Class("sale_type1", "2小时实时榜"));
        classes.add(new Class("sale_type2", "今日热销榜"));
        classes.add(new Class("brand_realtime", "品牌实时榜"));
//        classes.add(new Class("sale_type3", "昨日爆单榜"));
//        classes.add(new Class("super_selected", " 超级U选"));
        classes.add(new Class("get_free_shipping_data", "偏远包邮"));
        classes.add(new Class("low_price_Pinkage_data1", "低价精选"));
        classes.add(new Class("low_price_Pinkage_data2", "9.9专区"));
        classes.add(new Class("low_price_Pinkage_data3", "6.9专区"));
        classes.add(new Class("low_price_Pinkage_data4", "3.9专区"));
        classes.add(new Class("foodie_items", "淘宝吃货"));
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
        if (tid.startsWith("sale_type")) {//销量榜
            url = siteUrlV2 + "/sales_list";
            params.put("sale_type", tid.replace("sale_type", ""));
        }

        if (tid.startsWith("super_selected")) {//超级优选
            url = siteUrlV3 + "/super_selected";
        }

        if (tid.startsWith("get_free_shipping_data")) {//偏远包邮
            url = siteUrlV2 + "/get_free_shipping_data";
        }

        if (tid.startsWith("low_price_Pinkage_data")) {//低价包邮
            url = siteUrlV2 + "/low_price_Pinkage_data";
            params.put("type", tid.replace("low_price_Pinkage_data", ""));
        }

        if (tid.startsWith("brand_realtime")) {//品牌实时榜
            url = siteUrlV2 + "/brand_realtime";
        }

        if (tid.startsWith("foodie_items")) {//淘宝吃货
            url = siteUrlV2 + "/foodie_items";
        }

        //
        String body = OkHttp.string(url, params, new HashMap<>());


        JSONObject  joBody = new JSONObject(body);
        page = joBody.getString("min_id");
        List<GoodsListTBData> datas = GoodsListTBData.arrayFrom(new JSONObject(body).getJSONArray("data").toString());
        for (GoodsListTBData data : datas) list.add(data.vod());
        return Result.string(list);
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        Vod vod = new Vod();

        Map<String, String> params = new HashMap<>();
        params.put("apikey", apikey);
        params.put("itemid", ids.get(0));
        params.put("tb_name", "最美初相恋");
        params.put("get_taoword", "1");
        params.put("title", "1");
        params.put("pid", "mm_43453386_2079400019_115835200266");
        String body = OkHttp.post(ratesUrl, params);
        JSONObject data = new JSONObject(body).getJSONObject("data");
        String h5 = data.optString("link");
//        String kl = data.optString("taoword");
//        String long_h5 = data.optString("long_h5");
//        String deeplink = data.optString("link");
        vod.setVodId(ids.get(0));
        List<String> playFrom = new ArrayList<>();
        List<String> playUrls = new ArrayList<>();
        playFrom.add("淘宝券");
        playUrls.add(h5);
//        if (!TextUtils.isEmpty(deeplink))
            playUrls.add(h5.replace("https", "taobao"));
//            playUrls.add(kl + "\n复制口令打开淘宝");
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
        params.put("tb_p", searchPageTB);
        params.put("search_type", "1");
        params.put("is_coupon", "1");
        params.put("keyword", URLEncoder.encode(URLEncoder.encode(key)));
        //
        String body = OkHttp.string(supersearch, params, new HashMap<>());

        JSONObject  joBody = new JSONObject(body);
        searchPage = joBody.getString("min_id");
        searchPageTB = joBody.getString("tb_p");
        List<GoodsListTBData> datas = GoodsListTBData.arrayFrom(new JSONObject(body).getJSONArray("data").toString());
        for (GoodsListTBData data : datas) list.add(data.vod());
        return Result.string(list);
    }

}

