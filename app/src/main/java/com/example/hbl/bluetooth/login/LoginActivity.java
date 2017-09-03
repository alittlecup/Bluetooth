package com.example.hbl.bluetooth.login;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.hbl.bluetooth.BaseActivity;
import com.example.hbl.bluetooth.R;
import com.example.hbl.bluetooth.interfaces.onFragmentClick;
import com.example.hbl.bluetooth.util.ActivityUtilsImpl;
import com.example.hbl.bluetooth.util.CollectionUtilsImpl;

/**
 * Created by hbl on 2017/9/2.
 */

public class LoginActivity extends BaseActivity implements onFragmentClick {

    ActivityUtilsImpl activityUtils = new ActivityUtilsImpl(new CollectionUtilsImpl());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activityUtils.addFragmentWithTagToActivity(getSupportFragmentManager(), LoginFragment.newInstance(this), R.id.activity_container, LoginFragment.TAG);
    }

    @Override
    public void onFragmentClick(int action) {
        switch (action) {
            case Action.Action_Login:
                activityUtils.addFragmentWithTagToActivity(getSupportFragmentManager(), SearchFragment.newInstance(this), R.id.activity_container, LoginFragment.TAG);
                break;
            case Action.Action_MachBack:
                activityUtils.removeFragmentWithTagFromActivity(getSupportFragmentManager(),SearchFragment.TAG);
                break;
        }
    }
}
