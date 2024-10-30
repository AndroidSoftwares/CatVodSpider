package com.github.catvod.debug;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import com.github.catvod.R;
import com.github.catvod.crawler.Spider;
import com.github.catvod.spider.Init;
import com.github.catvod.spider.PTT;
import com.github.catvod.spider.PanSearch;
import com.github.catvod.spider.PanSou;
import com.github.catvod.spider.UpYun;
import com.github.catvod.spider.YiSo;
import com.github.catvod.spider.Zhaozy;
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
//            spider = new PanSearch();
//            spider = new UpYun();
            spider = new PanSou();
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
            Logger.t("categoryContent").d(spider.categoryContent("3", "2", true, extend));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void detailContent() {
        try {
//            Logger.t("detailContent").d(spider.detailContent(Arrays.asList("434686")));
//            Logger.t("detailContent").d(spider.detailContent(Arrays.asList("g1iFr6KNHku91")));//
            Logger.t("detailContent").d(spider.detailContent(Arrays.asList("https://www.alipan.com/s/bnZqa9iBN7V")));

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
//            Logger.t("searchContent").d(spider.searchContent("我的人间烟火", false));
            Logger.t("searchContent").d(spider.searchContent("你好", false, "2"));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}