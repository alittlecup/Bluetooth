package com.example.hbl.bluetooth;

import android.app.Application;
import android.arch.lifecycle.Observer;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.hbl.bluetooth.network.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hbl on 2017/10/11.
 */

public class LocationManager {
    private Application application;

    private LocationManager(Application application) {
        this.application = application;
    }

    private static LocationManager mInstance;

    public static LocationManager getInstance(Application application) {
        if (null == mInstance) {
            synchronized (LocationManager.class) {
                if (null == mInstance) {
                    mInstance = new LocationManager(application);
                }
            }
        }
        return mInstance;
    }

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    for (Observer<AMapLocation> observer : observers) {
                        observer.onChanged(aMapLocation);
                    }
                    mLocationClient.stopLocation();
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    ToastUtil.show("定位失败");
                }
            }
        }
    };
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    public void init() {
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setNeedAddress(true);
        mLocationOption.setMockEnable(false);
        mLocationOption.setHttpTimeOut(20000);
        mLocationOption.setOnceLocationLatest(true);

        mLocationClient = new AMapLocationClient(application);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.setLocationListener(mLocationListener);
    }

    private List<Observer<AMapLocation>> observers = new ArrayList<>();

    public void startLocation() {
        if (mLocationClient == null) {
            throw new RuntimeException("the mLocationClient must init first");
        } else {
            mLocationClient.startLocation();

        }
    }

    public void addObserver(Observer<AMapLocation> observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    public void removeObserver(Observer<AMapLocation> observer){
        if (observers.contains(observer)) {
            observers.remove(observer);
        }
    }
}
