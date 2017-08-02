package com.example.hbl.bluetooth.network;

import com.example.hbl.bluetooth.network.bean.BaseResponse;
import com.example.hbl.bluetooth.network.bean.CodeResponse;
import com.example.hbl.bluetooth.network.bean.UserInfo;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by hbl on 2017/7/18.
 */

public interface NetService {


    @FormUrlEncoded
    @POST("getcode")
    Call<CodeResponse> getCode(@Field("tel") String tel);

    @FormUrlEncoded
    @POST("setinfo")
    Call<UserInfo> updateUserInfo(@FieldMap() Map appNo);

    @FormUrlEncoded
    @POST("setmode")
    Call<BaseResponse> setMode(@Field("mobile") String mobile);

    @FormUrlEncoded
    @POST("getMode")
    Call<BaseResponse> getMode(@Field("tel")String tel);

    @FormUrlEncoded
    @GET("getnews")
    Call<BaseResponse> getNews();


}
