//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.hbl.bluetooth.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.hbl.bluetooth.App;


public class ToastUtil {

    public static void show(final Context context, final String msg) {
        if (isMainThread()) {
            showOnMainThread(context, msg);
        } else {
            new Handler().post(new Runnable() {
                public void run() {
                    showOnMainThread(context, msg);
                }
            });
        }
    }

    public static void show(final String msg) {
        if (isMainThread()) {
            showOnMainThread(App.app, msg);
        } else {
            new Handler().post(new Runnable() {
                public void run() {
                    showOnMainThread(App.app, msg);
                }
            });
        }
    }


    private static void showOnMainThread(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    private static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

}
