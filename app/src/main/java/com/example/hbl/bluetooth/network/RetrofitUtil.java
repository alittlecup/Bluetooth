package com.example.hbl.bluetooth.network;


import com.example.hbl.bluetooth.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hbl on 2017/7/18.
 */

public class RetrofitUtil {
    private static OkHttpClient okHttpClient;
    private static Retrofit.Builder builder;
        private static String debugHost = "http://food.xwzce.com/app/index/";
//    private static String debugHost = "http://10.133.36.21:8080/online-sign/";
    private static String releaseHost;

    static {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(new AddParamsInterceptor())
                .addInterceptor(new LoggingIntercepter())//打印日志
                .build();
        builder = new Retrofit.Builder()
                .baseUrl(BuildConfig.DEBUG ? debugHost : releaseHost)
                .addConverterFactory(GsonConverterFactory.create());
    }

    public static NetService getService() {
        return getService(null);
    }

    public static NetService getService(OkHttpClient client) {
        return builder.client(client == null ? okHttpClient : client)
                .build()
                .create(NetService.class);
    }
}
