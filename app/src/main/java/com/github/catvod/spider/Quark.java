package com.github.catvod.spider;


import com.github.catvod.api.QuarkApi;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.quark.ShareData;
import com.github.catvod.crawler.Spider;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author ColaMint & Adam & FongMi
 */
public class Quark extends Spider {
    public static final String patternQuark = "(https:\\/\\/pan\\.quark\\.cn\\/s\\/[^\"]+)";


    @Override
    public String detailContent(List<String> ids) throws Exception {
        String url = ids.get(0);
        try {

            ShareData shareData = QuarkApi.get().getShareData(url);

            return QuarkApi.get().getVod(shareData, url);
        } catch (Exception e) {
            return url;
        }
    }


    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        return QuarkApi.get().playerContent(id.split("\\+\\+"), flag);

    }

}
