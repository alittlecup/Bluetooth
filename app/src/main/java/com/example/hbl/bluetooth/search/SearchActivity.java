package com.example.hbl.bluetooth.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;

import com.example.hbl.bluetooth.BaseActivity;
import com.example.hbl.bluetooth.R;
import com.example.hbl.bluetooth.login.SearchFragment;

/**
 * Created by hbl on 2017/9/13.
 */

public class SearchActivity extends BaseActivity  {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.activity_container,new SearchFragment()).commitAllowingStateLoss();
    }


}
