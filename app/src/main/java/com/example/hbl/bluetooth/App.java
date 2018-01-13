package com.example.hbl.bluetooth;

import android.app.Application;
import android.support.annotation.IntegerRes;
import android.support.multidex.MultiDex;

import com.example.hbl.bluetooth.util.SharedPreferenceUtil;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;

public class App extends Application {

    public static App app;
    public static String tel;
    public static boolean AutoHeat;
    public static ArrayList<ModelData> getDatas() {
        return datas;
    }

    public static void addData(ModelData data) {
        datas.add(data);
    }

    private static ArrayList<ModelData> datas = new ArrayList<>();

    public static String singleMatch;
    public static int singleDrawbleRes;
    public static String singleName;
    @Override
    public void onCreate() {
        app = this;
        super.onCreate();
        MultiDex.install(this);
        CrashReport.initCrashReport(getApplicationContext(), "d6f9fc5961", true);
        SharedPreferenceUtil.init(this, "Bluetooth");
        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(false);

        EMClient.getInstance().init(this, options);
        EMClient.getInstance().setDebugMode(true);

        LocationManager instance = LocationManager.getInstance(this);
        instance.init();
    }

}
