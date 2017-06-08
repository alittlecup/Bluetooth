package com.example.hbl.bluetooth;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by hbl on 2017/6/4.
 */
class BaseActivity extends AppCompatActivity{
    ProgressDialog progressDialog;

    public void showDialog(String message){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
        }
        progressDialog.setMessage(message);
        progressDialog.show();
    }
    public void dismissDialog(){
        if(progressDialog!=null&&progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}
