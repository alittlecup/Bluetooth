package com.example.hbl.bluetooth.network;

import com.example.hbl.bluetooth.ModelData;
import com.example.hbl.bluetooth.ResultData;
import com.example.hbl.bluetooth.network.bean.BaseResponse;
import com.example.hbl.bluetooth.network.bean.CodeResponse;
import com.example.hbl.bluetooth.network.bean.UserInfo;

import java.util.List;
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
    Call<ResultData> setMode(@Field("tel") String tel, @Field("up")String up, @Field("down")String down, @Field("time")String time);

    @FormUrlEncoded
    @POST("getMode")
    Call<List<ModelData>> getMode(@Field("tel")String tel);

    @FormUrlEncoded
    @GET("getnews")
    Call<BaseResponse> getNews();


}
