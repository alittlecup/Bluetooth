package com.example.hbl.bluetooth;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;

public class App extends Application {

    public static App app;
    public static String tel;
    public static boolean ISTEEENABLE=false;
    public static boolean ISPAINENABLE=false;

    public static ArrayList<ModelData> getDatas() {
        return datas;
    }

    public static void addData(ModelData data) {
        datas.add(data);
    }

    private static ArrayList<ModelData> datas = new ArrayList<>();

    @Override
    public void onCreate() {
        app = this;
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "d6f9fc5961", true);
        SharedPreferenceUtil.init(this, "Bluetooth");
    }

}
