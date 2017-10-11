package com.example.hbl.bluetooth;

import com.example.hbl.bluetooth.interfaces.HeatControl;

/**
 * Created by hbl on 2017/10/11.
 */

public final class WeatherControl {
    private HeatControl heatControl;
    private WeatherControl(HeatControl control){
        this.heatControl=control;
    }
    private static WeatherControl mInstance;
    public static WeatherControl getInstance(HeatControl control){
        if(null==mInstance){
            synchronized (WeatherControl.class){
                if(null==mInstance){
                    mInstance=new WeatherControl(control);
                }
            }
        }
        return mInstance;
    }
    public void changeHeat(Integer integer){
       if(integer>=10){
           heatControl.stop();
       }else if(integer>=5&&integer<10){
           heatControl.changeHeat(30);
       }else if(integer>=2&&integer<5){
           heatControl.changeHeat(40);
       }else if(integer>=0&&integer<2){
           heatControl.changeHeat(50);
       }else if(integer>=-2&&integer<0){
           heatControl.changeHeat(60);
       }else if(integer>=-5&&integer<-2){
           heatControl.changeHeat(70);
       }else if(integer>=-8&&integer<-5){
           heatControl.changeHeat(80);
       }else if(integer>=-10&&integer<-8){
           heatControl.changeHeat(90);
       }else {
           heatControl.changeHeat(100);
       }

    }
}
