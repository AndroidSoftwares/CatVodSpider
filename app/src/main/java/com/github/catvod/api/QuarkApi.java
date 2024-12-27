package com.github.catvod.api;


import android.util.Log;

import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.bean.quark.*;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import com.github.catvod.net.OkResult;
import com.github.catvod.utils.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuarkApi {
    private String apiUrl = "https://drive-pc.quark.cn/1/clouddrive/";
    private String cookie = "_UP_A4A_11_=wb96715131b647a286052721c6c2e0cc; b-user-id=276d77f2-d01c-991c-91c8-665506c8dcf2; CwsSessionId=c4562d46-dab9-45fa-8040-70b8ec4d1322; __sdid=AAQP1F8dY1bP7P0G84VfjjVqdLvkG2Toua4uabz+jei1XLLrge4eqbmLBFUrNffnJny9KG48mJOzByMdAeLA021tHGALbyEO5PW0yWOPkEEoEQ==; _UP_D_=pc; __kps=AAQJrRbWmXFnvjAYj/213Ogl; __ktd=Vv3vknjC7f8vYDhdBoxfaQ==; __uid=AAQJrRbWmXFnvjAYj/213Ogl; xlly_s=1; __pus=0f98328d512fd3bde20e07252a43ce14AATNbZo+9+QjwBLtXVnfzI7Li4spahmMsxiCM3Ha320fpKK0TfHqYcCUlwqxzoRNTFkuUDSHDhMG67cTUOzC3GQL; __kp=0422d5c0-c370-11ef-9017-e10aa9908c95; isg=BAUFbP9hzulfZ-sYM_rgjvpDFEE_wrlUZWolzAdqMTxLnicQzhIOJLY9qMJo3tEM; tfstk=fiAmqWxfRKWb8UelrY5jd13_bZM-kr16_hFOX1IZUgS5XqT971PwXFCAQmT9qGSyjIpt5OryzwxObI3sm30MDFNO0idOsO-dVlujcOWGzNQn5rkjca7GWwKGCqsObGx97K3-9XLXl11NsDhK9OIB3xJGu-yq7g7FZOLOrXLXlzz0b2pr9lAGVCxNbhWVaz7F4ZP43h5yzNIzgNPV7uulVN5NuNWaaU7Fu5zV0h8rrNsPbbcociCPsQolhBdwuwUfOZXcj5IzsNAz9tseugVZYBQ0bM8Vq5PNc04H7UYxmWTRGBthJnhziHYBN3byYfml139wbNxrOlCXpnOCEnHUaU5c49SyToyV-sjcLIWr1qT5QLAlMIo_dULl09fJeYiABsxDdM6qF0tMriKwiTrm2GppyIWkY0FkfOvBWNxmiS-F4mezLbd81-c6rRw13a_lv-NCh214xP8Kr42FYt75lodtrRSN3a_bq40uLeWVPZZA.; __puus=b082b2a7623b44478da0664ea785caafAATrKuD6eqTxnoESfyh8a9UskB5xq+PdbiaWlTebxRxwz4yeRipMcgV5nIAeKTHAlxgTWz4BhFLR0dIxiNeFcHaDswSUJaam48+abw373cgPG7S9P1LZ5U08pT7ZG05gJnv+rx1ljUa+8Sqg31LpfG17L9vwogLpSTTZ/YKWvdtCzZPPC6+9f+HNipZboh0aqSurE6y5sCbfQAbOVz1ekKPi";
    private Map<String, Map<String, Object>> shareTokenCache = new HashMap<>();
    private String pr = "pr=ucpro&fr=pc";
    private Map<String, String> saveFileIdCaches = new HashMap<>();
    private String saveDirId = null;
    private String saveDirName = "TV";
    private boolean isVip = true;

    private static class Loader {
        static volatile QuarkApi INSTANCE = new QuarkApi();
    }

    public static QuarkApi get() {
        return Loader.INSTANCE;
    }

    public void setCookie(String token) throws Exception {
        if (StringUtils.isNoneBlank(token)) {
            this.cookie = token;
        }
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) quark-cloud-drive/2.5.20 Chrome/100.0.4896.160 Electron/18.3.5.4-b478491100 Safari/537.36 Channel/pckk_other_ch");
        headers.put("Referer", "https://pan.quark.cn/");
        headers.put("Content-Type", "application/json");
        headers.put("Cookie", cookie);
        headers.put("Host", "drive-pc.quark.cn");
        return headers;
    }

    private Map<String, String> getWebHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) quark-cloud-drive/2.5.20 Chrome/100.0.4896.160 Electron/18.3.5.4-b478491100 Safari/537.36 Channel/pckk_other_ch");
        headers.put("Referer", "https://pan.quark.cn/");
        headers.put("Cookie", cookie);
        return headers;
    }

    public void initQuark(String cookie) throws Exception {
        this.cookie = cookie;
    }

    private QuarkApi() {

    }

    public File getCache() {
        return Path.tv("quark");
    }

    public String getTransfer(String url) throws Exception {
        try {
            ShareData shareData = getShareData(url);
            getShareToken(shareData);
            List<Item> files = new ArrayList<>();
            List<Map<String, Object>> listData = listFile(1, shareData, files, shareData.getShareId(), shareData.getFolderId(), 1);
            if (listData.size() > 0) {
                //保存文件fileId
                String fileId = save(shareData.getShareId(), saveFileIdCaches.get(shareData.getShareId()), (String) listData.get(0).get("fid"), (String) listData.get(0).get("share_fid_token"), true);

                //分享获取share_id
                String shareId = getShareIdByTaskId(fileId, (String) listData.get(0).get("file_name"));
                //获取分享的URL
                String shareUrl = getShareUrl(shareId);

                return shareUrl;
            }

        } catch (Exception e) {

            return url;
        }
        return url;
    }

    private String getShareUrl(String shareId) throws Exception {

        Map<String, Object> result = Json.parseSafe(api("share/password?" + this.pr, null, ImmutableMap.of("share_id", shareId), 0, "POST"), Map.class);
        if (result.get("data") != null && ((Map<Object, Object>) result.get("data")).get("share_url") != null) {
            return (String) (((Map<Object, Object>) result.get("data")).get("share_url"));
        }
        return null;
    }

    public String playerContent(String[] split, String flag) throws Exception {

        String fileId = split[0], fileToken = split[1], shareId = split[2], stoken = split[3];
        String playUrl = "";
        if (flag.contains("原画")) {
            playUrl = this.getDownload(shareId, stoken, fileId, fileToken, true);
        } else {
            playUrl = this.getLiveTranscoding(shareId, stoken, fileId, fileToken, flag);
        }
        Map<String, String> header = getHeaders();
        header.remove("Host");
        header.remove("Content-Type");
        return Result.get().url("proxyVideoUrl(playUrl, header)").octet().header(header).string();
    }


    /**
     * @param url
     * @param params get 参数
     * @param data   post json
     * @param retry
     * @param method
     * @return
     * @throws Exception
     */
    private String api(String url, Map<String, String> params, Map<String, Object> data, Integer retry, String method) throws Exception {


        int leftRetry = retry != null ? retry : 3;

        OkResult okResult;
        if ("GET".equals(method)) {
            okResult = OkHttp.get(this.apiUrl + url, params, getHeaders());
        } else {
            okResult = OkHttp.post(this.apiUrl + url, Json.toJson(data), getHeaders());
        }
        if (okResult.getResp().get("Set-Cookie") != null) {
            Matcher matcher = Pattern.compile("__puus=([^;]+)").matcher(StringUtils.join(okResult.getResp().get("Set-Cookie"), ";;;"));
            if (matcher.find()) {
                Matcher cookieMatcher = Pattern.compile("__puus=([^;]+)").matcher(this.cookie);
                if (cookieMatcher.find() && !cookieMatcher.group(1).equals(matcher.group(1))) {
                    this.cookie = this.cookie.replaceAll("__puus=[^;]+", "__puus=" + matcher.group(1));
                } else {
                    this.cookie = this.cookie + ";__puus=" + matcher.group(1);
                }
            }
        }

        if (okResult.getCode() != 200 && leftRetry > 0) {
            SpiderDebug.log("api error code:" + okResult.getCode());
            Thread.sleep(1000);
            return api(url, params, data, leftRetry - 1, method);
        }
        return okResult.getBody();
    }

    public ShareData getShareData(String url) {
        Pattern pattern = Pattern.compile("https://pan\\.quark\\.cn/s/([^\\\\|#/]+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return new ShareData(matcher.group(1), "0");
        }
        return null;
    }


    public List<String> getPlayFormatList() {
        if (this.isVip) {
            return Arrays.asList("4K", "超清", "高清", "普画");
        } else {
            return Collections.singletonList("普画");
        }
    }

    private List<String> getPlayFormatQuarkList() {
        if (this.isVip) {
            return Arrays.asList("4k", "2k", "super", "high", "normal", "low");
        } else {
            return Collections.singletonList("low");
        }
    }

    private void getShareToken(ShareData shareData) throws Exception {
        if (!this.shareTokenCache.containsKey(shareData.getShareId())) {
            this.shareTokenCache.remove(shareData.getShareId());
            Map<String, Object> shareToken = Json.parseSafe(api("share/sharepage/token?" + this.pr, Collections.emptyMap(), ImmutableMap.of("pwd_id", shareData.getShareId(), "passcode", shareData.getSharePwd() == null ? "" : shareData.getSharePwd()), 0, "POST"), Map.class);
            if (shareToken.containsKey("data") && ((Map<String, Object>) shareToken.get("data")).containsKey("stoken")) {
                this.shareTokenCache.put(shareData.getShareId(), (Map<String, Object>) shareToken.get("data"));
            }
        }
    }

    private List<Map<String, Object>> listFile(int shareIndex, ShareData shareData, List<Item> videos, String shareId, String folderId, Integer page) throws Exception {
        int prePage = 200;
        page = page != null ? page : 1;

        Map<String, Object> listData = Json.parseSafe(api("share/sharepage/detail?" + this.pr + "&pwd_id=" + shareId + "&stoken=" + encodeURIComponent((String) this.shareTokenCache.get(shareId).get("stoken")) + "&pdir_fid=" + folderId + "&force=0&_page=" + page + "&_size=" + prePage + "&_sort=file_type:asc,file_name:asc", Collections.emptyMap(), Collections.emptyMap(), 0, "GET"), Map.class);
        if (listData.get("data") == null) return Collections.emptyList();
        List<Map<String, Object>> items = (List<Map<String, Object>>) ((Map<String, Object>) listData.get("data")).get("list");
        if (items == null) return Collections.emptyList();
        List<Map<String, Object>> subDir = new ArrayList<>();
        for (Map<String, Object> item : items) {
            if (Boolean.TRUE.equals(item.get("dir"))) {
                subDir.add(item);
            } else if (Boolean.TRUE.equals(item.get("file")) && "video".equals(item.get("obj_category"))) {
                if ((Double) item.get("size") < 1024 * 1024 * 5) continue;
                item.put("stoken", this.shareTokenCache.get(shareData.getShareId()).get("stoken"));
                videos.add(Item.objectFrom(item, shareData.getShareId(), shareIndex));
            }
        }
        if (page < Math.ceil((double) ((Map<String, Object>) listData.get("metadata")).get("_total") / prePage)) {
            List<Map<String, Object>> nextItems = listFile(shareIndex, shareData, videos, shareId, folderId, page + 1);
            items.addAll(nextItems);
        }
        return items;
    }


    public static Integer findAllIndexes(List<String> arr, String value) {

        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i).equals(value)) {
                return i;
            }
        }
        return 0;
    }


    private void clean() {
        saveFileIdCaches.clear();
    }

    private void clearSaveDir() throws Exception {

        Map<String, Object> listData = Json.parseSafe(api("file/sort?" + this.pr + "&pdir_fid=" + this.saveDirId + "&_page=1&_size=200&_sort=file_type:asc,updated_at:desc", Collections.emptyMap(), Collections.emptyMap(), 0, "GET"), Map.class);
        if (listData.get("data") != null && ((List<Map<String, Object>>) ((Map<String, Object>) listData.get("data")).get("list")).size() > 0) {
            List<String> list = new ArrayList<>();
            for (Map<String, Object> stringStringMap : ((List<Map<String, Object>>) ((Map<String, Object>) listData.get("data")).get("list"))) {
                list.add((String) stringStringMap.get("fid"));
            }
            api("file/delete?" + this.pr, Collections.emptyMap(), ImmutableMap.of("action_type", "2", "filelist", Json.toJson(list), "exclude_fids", ""), 0, "POST");
        }
    }

    private void createSaveDir(boolean clean) throws Exception {
        if (this.saveDirId != null) {
            if (clean) clearSaveDir();
            return;
        }

        Map<String, Object> listData = Json.parseSafe(api("file/sort?" + this.pr + "&pdir_fid=0&_page=1&_size=200&_sort=file_type:asc,updated_at:desc", Collections.emptyMap(), Collections.emptyMap(), 0, "GET"), Map.class);
        if (listData.get("data") != null) {
            for (Map<String, Object> item : (List<Map<String, Object>>) ((Map<String, Object>) listData.get("data")).get("list")) {
                if (this.saveDirName.equals(item.get("file_name"))) {
                    this.saveDirId = item.get("fid").toString();
                    clearSaveDir();
                    break;
                }
            }
        }
        if (this.saveDirId == null) {
            Map<String, Object> create = Json.parseSafe(api("file?" + this.pr, Collections.emptyMap(), ImmutableMap.of("pdir_fid", "0", "file_name", this.saveDirName, "dir_path", "", "dir_init_lock", "false"), 0, "POST"), Map.class);
            if (create.get("data") != null && ((Map<String, Object>) create.get("data")).get("fid") != null) {
                this.saveDirId = ((Map<String, Object>) create.get("data")).get("fid").toString();
            }
        }
    }

    private String save(String shareId, String stoken, String fileId, String fileToken, boolean clean) throws Exception {
        createSaveDir(clean);
        if (clean) {
            clean();
        }
        if (this.saveDirId == null) return null;
        if (stoken == null) {
            getShareToken(new ShareData(shareId, null));
            if (!this.shareTokenCache.containsKey(shareId)) return null;
        }

        Map<String, Object> saveResult = Json.parseSafe(api("share/sharepage/save?" + this.pr, null, ImmutableMap.of("fid_list", ImmutableList.of(fileId), "fid_token_list", ImmutableList.of(fileToken), "to_pdir_fid", this.saveDirId, "pwd_id", shareId, "stoken", stoken != null ? stoken : (String) this.shareTokenCache.get(shareId).get("stoken"), "pdir_fid", "0", "scene", "link"), 0, "POST"), Map.class);
        if (saveResult.get("data") != null && ((Map<Object, Object>) saveResult.get("data")).get("task_id") != null) {
            int retry = 0;
            while (true) {

                Map<String, Object> taskResult = Json.parseSafe(api("task?" + this.pr + "&task_id=" + ((Map<String, Object>) saveResult.get("data")).get("task_id") + "&retry_index=" + retry, Collections.emptyMap(), Collections.emptyMap(), 0, "GET"), Map.class);
                if (taskResult.get("data") != null && ((Map<Object, Object>) taskResult.get("data")).get("save_as") != null && ((Map<Object, Object>) ((Map<Object, Object>) taskResult.get("data")).get("save_as")).get("save_as_top_fids") != null && ((List<String>) ((Map<String, Object>) ((Map<String, Object>) taskResult.get("data")).get("save_as")).get("save_as_top_fids")).size() > 0) {
                    return ((List<String>) ((Map<String, Object>) ((Map<Object, Object>) taskResult.get("data")).get("save_as")).get("save_as_top_fids")).get(0);
                }
                retry++;
                if (retry > 2) break;
                Thread.sleep(1000);
            }
        }
        return null;
    }

    private String getShareIdByTaskId(String fileId, String fileName) throws Exception {

        Map<String, Object> saveResult = Json.parseSafe(api("share?" + this.pr, null, ImmutableMap.of("fid_list", ImmutableList.of(fileId), "title", fileName, "url_type", 1, "expired_type", 1), 0, "POST"), Map.class);
        if (saveResult.get("data") != null && ((Map<Object, Object>) saveResult.get("data")).get("task_id") != null) {
            int retry = 0;
            while (true) {

                Map<String, Object> taskResult = Json.parseSafe(api("task?" + this.pr + "&task_id=" + ((Map<String, Object>) saveResult.get("data")).get("task_id") + "&retry_index=" + retry, Collections.emptyMap(), Collections.emptyMap(), 0, "GET"), Map.class);
                if (taskResult.get("data") != null && ((Map<Object, Object>) taskResult.get("data")).get("share_id") != null) {
                    return (String) (((Map<Object, Object>) taskResult.get("data")).get("share_id"));
                }
                retry++;
                if (retry > 2) break;
                Thread.sleep(1000);
            }
        }
        return null;
    }

    private String getLiveTranscoding(String shareId, String stoken, String fileId, String fileToken, String flag) throws Exception {
        if (!this.saveFileIdCaches.containsKey(fileId)) {
            String saveFileId = save(shareId, stoken, fileId, fileToken, true);
            if (saveFileId == null) return null;
            this.saveFileIdCaches.put(fileId, saveFileId);
        }

        Map<String, Object> transcoding = Json.parseSafe(api("file/v2/play?" + this.pr, Collections.emptyMap(), ImmutableMap.of("fid", this.saveFileIdCaches.get(fileId), "resolutions", "normal,low,high,super,2k,4k", "supports", "fmp4"), 0, "POST"), Map.class);
        if (transcoding.get("data") != null && ((Map<Object, Object>) transcoding.get("data")).get("video_list") != null) {
            String flagId = flag.split("-")[flag.split("-").length - 1];
            int index = findAllIndexes(getPlayFormatList(), flagId);
            String quarkFormat = getPlayFormatQuarkList().get(index);
            for (Map<String, Object> video : (List<Map<String, Object>>) ((Map<Object, Object>) transcoding.get("data")).get("video_list")) {
                if (video.get("resolution").equals(quarkFormat)) {
                    return (String) ((Map<String, Object>) video.get("video_info")).get("url");
                }
            }
            return (String) ((Map<String, Object>) ((List<Map<String, Object>>) ((Map<Object, Object>) transcoding.get("data")).get("video_list")).get(index).get("video_info")).get("url");
        }
        return null;
    }

    private String getDownload(String shareId, String stoken, String fileId, String fileToken, boolean clean) throws Exception {
        if (!this.saveFileIdCaches.containsKey(fileId)) {
            String saveFileId = save(shareId, stoken, fileId, fileToken, clean);
            if (saveFileId == null) return null;
            this.saveFileIdCaches.put(fileId, saveFileId);
        }
        Map<String, Object> down = Json.parseSafe(api("file/download?" + this.pr + "&uc_param_str=", Collections.emptyMap(), ImmutableMap.of("fids", this.saveFileIdCaches.get(fileId)), 0, "POST"), Map.class);
        if (down.get("data") != null) {
            return ((List<String>) down.get("data")).get(0);
        }
        return null;
    }


    // Encoding helper method
    private String encodeURIComponent(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }


}

