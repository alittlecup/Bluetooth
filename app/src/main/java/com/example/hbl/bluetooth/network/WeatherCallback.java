package com.example.hbl.bluetooth.network;

import com.example.hbl.bluetooth.network.bean.CityWeather;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hbl on 2017/10/11.T
 */

public abstract class WeatherCallback implements Callback<ResponseBody> {


    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        String string = null;
        try {
            string = response.body().string();
            CityWeather cityWeather = new Gson().fromJson(string, CityWeather.class);
            onSucess(cityWeather);
        } catch (IOException e) {
            e.printStackTrace();
        }catch (JsonSyntaxException e){
            WeatherError weatherError = new Gson().fromJson(string, WeatherError.class);
            onFailure(weatherError.status);
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        onFailure(t.getMessage());
    }
    public abstract  void onSucess(CityWeather cityWeather);
    public abstract void onFailure(String msg);
    class WeatherError{
        public String status="";
        public String status_code="";
    }
}
