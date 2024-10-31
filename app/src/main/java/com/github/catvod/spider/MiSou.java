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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiSou extends Spider {

    private final String siteUrl = "https://www.misou.fun";

    private Map<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", Util.CHROME);
        headers.put("Referer", siteUrl);
        return headers;
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        List<Class> classes = new ArrayList<>();
        List<String> typeIds = Collections.singletonList("1");
        List<String> typeNames = Collections.singletonList("自用");
        for (int i = 0; i < typeIds.size(); i++) classes.add(new Class(typeIds.get(i), typeNames.get(i)));

        List<Vod> list = this.queryVodList("", "1", "唐僧*飘柔");

        return Result.string(classes, list);
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        List<Vod> list = this.queryVodList("", pg, "唐僧*飘柔");

        return Result.string(list);
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        Vod vod = new Vod();
        vod.setVodId(ids.get(0));
        List<String> playFrom = new ArrayList<>();
        List<String> playUrls = new ArrayList<>();
        playFrom.add("米盘");
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
        //                {
//                    "page": 1,
//                        "q": "庆余年第二季",
//                        "user": "",
//                        "exact": true,
//                        "share_time": "",
//                        "size": 15,
//                        "type": "QUARK",
//                        "adv_params": {
//                    "wechat_pwd": ""
//                 }
//                }
        List<Vod> list = new ArrayList<>();
        try{
            String url = siteUrl + "/v1/search/disk";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("page", Integer.parseInt(page));
            jsonObject.put("q", key);
            jsonObject.put("user", user);
            jsonObject.put("exact", true);
            List<String> objects = new ArrayList<String>();
//        jsonObject.put("format", objects);
            jsonObject.put("share_time", "");
            jsonObject.put("size", 15);
            jsonObject.put("type", "");
//        jsonObject.put("exclude_user", objects);
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("wechat_pwd", "");
            jsonObject.put("adv_params", jsonObject1);


            OkResult result = OkHttp.post(url, jsonObject.toString(), getHeaders());
            JSONObject object = new JSONObject(result.getBody());
            JSONArray jsonArray = object.getJSONObject("data").getJSONArray("list");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                Vod vod = new Vod();
                vod.setVodId(item.getString("link"));
                // 初始化备注为 "小米资源"
                String remarks = "未知";
//                String imageUrl = ""; // 默认图片
//
//                // 检查 link 是否包含 "https://drive.uc.cn"
//                if (item.getString("link").contains("https://drive.uc.cn")) {
//                    remarks += " UC网盘";
//                    imageUrl = "https://fs-im-kefu.7moor-fs1.com/ly/4d2c3f00-7d4c-11e5-af15-41bf63ae4ea0/1726037310469/29105638h9g3.png"; // 替换为实际的图片URL
//                } else {
//                    remarks += " 夸克网盘";
//                }
                String disk_type = item.getString("disk_type");
                if (disk_type.equals("UC")){
                    remarks = "UC网盘";
                } else if (disk_type.equals("QUARK")) {
                    remarks = "夸克网盘";
                } else if (disk_type.equals("XUNLEI")){
                    remarks = "迅雷网盘";
                } else if (disk_type.equals("ALY")){
                    remarks = "阿里云盘";
                } else if (disk_type.equals("BDY")){
                    remarks = "阿里云盘";
                }
                vod.setVodRemarks(remarks);
                vod.setVodPic("");
                vod.setVodName(item.getString("disk_name").replace("<em>", "").replace("</em>", ""));
                list.add(vod);
            }
        } catch (Exception e) {
            e.printStackTrace(); // 记录异常，而不是忽略
        }
        return list;
    }
}
