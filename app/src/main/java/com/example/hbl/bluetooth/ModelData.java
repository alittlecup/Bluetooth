package com.example.hbl.bluetooth;

/**
 * Created by hbl on 2017/6/4.
 */

public class ModelData {
    boolean isCheck;
    String string;

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return "ModelData{" +
                "isCheck=" + isCheck +
                ", string='" + string + '\'' +
                '}';
    }
}
