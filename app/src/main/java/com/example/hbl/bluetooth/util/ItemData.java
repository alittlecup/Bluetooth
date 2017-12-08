package com.example.hbl.bluetooth.util;

/**
 * Created by huangbaole on 2017/12/2.
 */

public final class ItemData {
    String text;
    int res;

    public ItemData(String text, int res) {
        this.text = text;
        this.res = res;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }
}
