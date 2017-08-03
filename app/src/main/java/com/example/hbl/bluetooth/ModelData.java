package com.example.hbl.bluetooth;

import com.example.hbl.bluetooth.network.bean.BaseResponse;

/**
 * Created by hbl on 2017/6/4.
 */

public class ModelData extends BaseResponse {
    private boolean isCheck;
    private String up;
    private String down;
    private String time;
    private String date;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getUp() {
        return up;
    }

    public void setUp(String up) {
        this.up = up;
    }

    public String getDown() {
        return down;
    }

    public void setDown(String down) {
        this.down = down;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
