package com.github.catvod.spider;

import android.text.TextUtils;
import android.util.Base64;

import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.net.OkHttp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class YiSo extends Ali {

    private HashMap<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 12; V2049A Build/SP1A.210812.003; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/103.0.5060.129 Mobile Safari/537.36");
        headers.put("Referer", "https://yiso.fun/");
        headers.put("Cookie", "satoken=88f71cb7-f76b-443d-a3cb-8a5a0e13cfb4");
        return headers;
    }

    @Override
    public String searchContent(String key, boolean quick, String pg) throws Exception {
        String json = OkHttp.string("https://yiso.fun/api/search?name=" + URLEncoder.encode(key) + "&pageNo=" + pg, getHeaders());
        JSONArray array = new JSONObject(json).getJSONObject("data").getJSONArray("list");
        ArrayList<Vod> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Vod vod = new Vod();
            String name = array.getJSONObject(i).getJSONArray("fileInfos").getJSONObject(0).getString("fileName");
            String remark = array.getJSONObject(i).getString("from") + " " + array.getJSONObject(i).getString("gmtCreate");
            vod.setVodId(decrypt(array.getJSONObject(i).getString("url")));
            vod.setVodName(name);
            vod.setVodRemarks(remark);
            vod.setVodPic("");
            list.add(vod);
        }
        return Result.string(list);
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        return searchContent(key, quick, "1");
    }

    public String decrypt(String str) {
        try {
            SecretKeySpec key = new SecretKeySpec("4OToScUFOaeVTrHE".getBytes("UTF-8"), "AES");
            IvParameterSpec iv = new IvParameterSpec("9CLGao1vHKqm17Oz".getBytes("UTF-8"));
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            return new String(cipher.doFinal(Base64.decode(str.getBytes(), 0)), "UTF-8");
        } catch (Exception e) {
            return "";
        }
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
