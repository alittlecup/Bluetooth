package com.example.hbl.bluetooth.network;

import com.example.hbl.bluetooth.network.bean.BaseResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hbl on 2017/7/18.
 */

public abstract class DefaultCallback<T extends BaseResponse> implements Callback<T> {
    protected static final int SUCCESS=0;
    private static final int ERROR=-1;
    protected static final int FAILURE=1;
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.raw().code() == 200 && response.body() != null) {//200是服务器有合理响应
            T body = response.body();
            if (0==body.status) {
                onFinish(SUCCESS,body);
            }else{
                onFinish(FAILURE,body);
            }
        } else {//失败响应
            onFailure(call, new RuntimeException("response error,detail = " + response.raw().toString()));
        }
    }

    public abstract void onFinish(int status, T body);

    @Override
    public void onFailure(Call<T> call, Throwable t) {//网络问题会走该回调
        onFinish(ERROR,null);
        ToastUtil.show("网络访问异常，请重试");
    }
}
