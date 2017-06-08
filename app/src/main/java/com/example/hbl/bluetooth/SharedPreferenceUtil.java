package com.example.hbl.bluetooth;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferenceUtil {
    private static Context mContext;
    private static SharedPreferences sp;


    private SharedPreferenceUtil() {
    }

    public static void init(Context context, String fileName) {
        mContext = context;
        sp = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    /**
     * @param key
     * @param defaultObject
     * @return
     */
    public static <T> T getValue(String key, T defaultObject) {
        String decrypt = sp.getString(key, String.valueOf(defaultObject));
        System.out.println("decrypt: " + decrypt);
        if (defaultObject instanceof String) {
            return (T) decrypt;
        } else if (defaultObject instanceof Integer) {
            return (T) Integer.valueOf(decrypt);
        } else if (defaultObject instanceof Boolean) {
            return (T) Boolean.valueOf(decrypt);
        } else if (defaultObject instanceof Float) {
            return (T) Float.valueOf(decrypt);
        } else if (defaultObject instanceof Long) {
            return (T) Long.valueOf(decrypt);
        }

        return null;
    }

    /**
     * @param key
     * @param object
     */
    public static void putValue(String key, Object object) {
        SharedPreferences.Editor editor = sp.edit();
        String encrypt = object.toString();
        editor.putString(key, encrypt);
        editor.apply();
    }
}
