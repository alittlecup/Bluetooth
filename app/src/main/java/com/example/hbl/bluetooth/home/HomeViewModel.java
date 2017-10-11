package com.example.hbl.bluetooth.home;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.view.View;

import com.example.hbl.bluetooth.Order;
import com.example.hbl.bluetooth.interfaces.HeatControl;

/**
 * Created by hbl on 2017/10/10.
 */

public class HomeViewModel extends ViewModel implements HeatControl{
    private final MutableLiveData<String> mUpText = new MutableLiveData<>();
    private final MutableLiveData<String> mDownText = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mUptextState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mDowntextState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mUptextVisible = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mDowntextVisible = new MutableLiveData<>();

    private final MutableLiveData<Boolean> mUpImgVisible = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mDownImgVisible = new MutableLiveData<>();

    private final MutableLiveData<Integer> mUpImg = new MutableLiveData<>();
    private final MutableLiveData<Integer> mDownImg = new MutableLiveData<>();

    private final MutableLiveData<View.OnClickListener> mUpTextClick = new MutableLiveData<>();
    private final MutableLiveData<View.OnClickListener> mDownTextClick = new MutableLiveData<>();
    private final MutableLiveData<String> mOrderUp = new MutableLiveData<>();
    private final MutableLiveData<String> mOrderDown = new MutableLiveData<>();

    private final MutableLiveData<Integer> mUpProgress = new MutableLiveData<>();
    private final MutableLiveData<Integer> mDownProgress = new MutableLiveData<>();
    private final MutableLiveData<Integer> mTimeProgress = new MutableLiveData<>();

    private final MutableLiveData<Boolean> mIsTeeEnable=new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsPainEnable=new MutableLiveData<>();


    public MutableLiveData<Boolean> getmIsTeeEnable() {
        return mIsTeeEnable;
    }

    public MutableLiveData<Boolean> getmIsPainEnable() {
        return mIsPainEnable;
    }

    public MutableLiveData<Integer> getmUpProgress() {
        return mUpProgress;
    }

    public MutableLiveData<Integer> getmDownProgress() {
        return mDownProgress;
    }

    public MutableLiveData<Integer> getmTimeProgress() {
        return mTimeProgress;
    }

    public MutableLiveData<String> getmOrderUp() {
        return mOrderUp;
    }

    public MutableLiveData<String> getmOrderDown() {
        return mOrderDown;
    }

    public void sendOrderUp(String order) {
        mOrderUp.setValue(order);
    }

    public void sendOrderDown(String order) {
        mOrderDown.setValue(order);
    }

    public MutableLiveData<View.OnClickListener> getmUpTextClick() {
        return mUpTextClick;
    }

    public MutableLiveData<View.OnClickListener> getmDownTextClick() {
        return mDownTextClick;
    }

    public MutableLiveData<String> getmUpText() {
        return mUpText;
    }

    public MutableLiveData<String> getmDownText() {
        return mDownText;
    }

    public MutableLiveData<Boolean> getmUptextState() {
        return mUptextState;
    }

    public MutableLiveData<Boolean> getmDowntextState() {
        return mDowntextState;
    }

    public MutableLiveData<Boolean> getmUptextVisible() {
        return mUptextVisible;
    }

    public MutableLiveData<Boolean> getmDowntextVisible() {
        return mDowntextVisible;
    }

    public MutableLiveData<Boolean> getmUpImgVisible() {
        return mUpImgVisible;
    }

    public MutableLiveData<Boolean> getmDownImgVisible() {
        return mDownImgVisible;
    }

    public MutableLiveData<Integer> getmUpImg() {
        return mUpImg;
    }

    public MutableLiveData<Integer> getmDownImg() {
        return mDownImg;
    }



    @Override
    public void changeHeat(Integer integer) {
        mUpProgress.setValue(integer);
        mDownProgress.setValue(integer);
    }

    @Override
    public void stop() {
        sendOrderUp(Order.WRITE_CLOSE);
        sendOrderDown(Order.WRITE_CLOSE);
    }

}
