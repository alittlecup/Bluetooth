package com.example.hbl.bluetooth.network;

import android.util.Log;

import com.example.hbl.bluetooth.BuildConfig;


public class BLog {
    private static final String TAG = "BLUE";

    public static void e(String msg) {
        if (BuildConfig.DEBUG) {
            System.out.println(TAG+":  "+msg);
        }
    }

    public static void i(String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, msg);
        }
    }

    public static void d(String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, msg);
        }
    }
}
