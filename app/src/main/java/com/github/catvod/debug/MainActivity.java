package com.github.catvod.debug;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import com.github.catvod.R;
import com.github.catvod.crawler.Spider;
import com.github.catvod.spider.CLPH;
import com.github.catvod.spider.CLWQCL;
import com.github.catvod.spider.CLXF;
import com.github.catvod.spider.CiLiKu;
import com.github.catvod.spider.Czsapp;
import com.github.catvod.spider.DyGang;
import com.github.catvod.spider.Init;
import com.github.catvod.spider.HunHePan;
import com.github.catvod.spider.LWCL;
import com.github.catvod.spider.MiSou;
import com.github.catvod.spider.PanSearch;
import com.github.catvod.spider.PiKa;
import com.github.catvod.spider.WPRBYQ;
import com.github.catvod.spider.ReBang;
import com.github.catvod.spider.WJCL;
import com.github.catvod.spider.Wogg;
import com.github.catvod.spider.Yiove;
import com.github.catvod.spider.WPYpPan;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {

    private ExecutorService executor;
    private Spider spider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button homeContent = findViewById(R.id.homeContent);
        Button homeVideoContent = findViewById(R.id.homeVideoContent);
        Button categoryContent = findViewById(R.id.categoryContent);
        Button detailContent = findViewById(R.id.detailContent);
        Button playerContent = findViewById(R.id.playerContent);
        Button searchContent = findViewById(R.id.searchContent);
        homeContent.setOnClickListener(view -> executor.execute(this::homeContent));
        homeVideoContent.setOnClickListener(view -> executor.execute(this::homeVideoContent));
        categoryContent.setOnClickListener(view -> executor.execute(this::categoryContent));
        detailContent.setOnClickListener(view -> executor.execute(this::detailContent));
        playerContent.setOnClickListener(view -> executor.execute(this::playerContent));
        searchContent.setOnClickListener(view -> executor.execute(this::searchContent));
        Logger.addLogAdapter(new AndroidLogAdapter());
        executor = Executors.newCachedThreadPool();
        executor.execute(this::initSpider);
    }

    private void initSpider() {
        try {
            Init.init(getApplicationContext());
//            spider = new PTT();
//            spider = new YiSo();
//            spider = new UpYun();
//            spider = new PanSou();
            spider = new HunHePan();
            spider = new MiSou();
            spider = new CiLiKu();
            spider = new CLXF();
            spider = new ReBang();
            spider = new PiKa();
            spider = new PanSearch();
            spider = new WJCL();
            spider = new LWCL();
            spider = new Yiove();
            spider = new Wogg();
            spider = new WPRBYQ();
            spider = new CLWQCL();
            spider = new CLPH();
            spider = new DyGang();
            spider = new Czsapp();
            spider = new WPYpPan();
//            spider = new Zhaozy();
//            spider.init(this, "影視天下第一$$$test2$$$test2");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void homeContent() {
        try {
            Logger.t("homeContent").d(spider.homeContent(true));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void homeVideoContent() {
        try {
            Logger.t("homeVideoContent").d(spider.homeVideoContent());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void categoryContent() {
        try {
            HashMap<String, String> extend = new HashMap<>();
            extend.put("c", "19");
            extend.put("year", "2024");
//            Logger.t("categoryContent").d(spider.categoryContent("3", "2", true, extend));
//            Logger.t("categoryContent").d(spider.categoryContent("14", "2", true, extend));
            Logger.t("categoryContent").d(spider.categoryContent("baidu?cache=true", "1", true, extend));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void detailContent() {
        try {
//            Logger.t("detailContent").d(spider.detailContent(Arrays.asList("434686")));
//            Logger.t("detailContent").d(spider.detailContent(Arrays.asList("g1iFr6KNHku91")));//
//            Logger.t("detailContent").d(spider.detailContent(Arrays.asList("https://pan.quark.cn/s/0a9ea215a7a0")));
//            Logger.t("detailContent").d(spider.detailContent(Arrays.asList("md5hash=531f39dcf4afe8664f910af1541a718a52ab390683ed25c5c232ad27997887e6&sjk=2")));
//            Logger.t("detailContent").d(spider.detailContent(Arrays.asList("/!iyQa")));
//            Logger.t("detailContent").d(spider.detailContent(Arrays.asList("/voddetail/81270.html")));//玩偶哥哥
//            Logger.t("detailContent").d(spider.detailContent(Arrays.asList("cm3q9ogxc3bg8bdpv8mgyq8k6")));//肉不要钱
//            Logger.t("detailContent").d(spider.detailContent(Arrays.asList("http://www.xpiaohua.com/column/juqing/20241119/66623.html")));//新飘花
            Logger.t("detailContent").d(spider.detailContent(Arrays.asList("https://www.yppan.com/archives/50624")));//新飘花

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void playerContent() {
        try {
            Logger.t("playerContent").d(spider.playerContent("", "382044/1/78", new ArrayList<>()));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void searchContent() {
        try {
            Logger.t("searchContent").d(spider.searchContent("仙逆", false));
//            Logger.t("searchContent").d(spider.searchContent("最后", false));
//            Logger.t("searchContent").d(spider.searchContent("漫威", false, "1"));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}