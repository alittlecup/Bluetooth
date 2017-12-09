package com.example.hbl.bluetooth.home;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.hbl.bluetooth.BaseFragment;
import com.example.hbl.bluetooth.R;
import com.example.hbl.bluetooth.login.LoginActivity;
import com.example.hbl.bluetooth.util.SPKey;
import com.example.hbl.bluetooth.util.SharedPreferenceUtil;
import com.example.hbl.bluetooth.view.MineItemView;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import butterknife.BindView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends BaseFragment {


    @BindView(R.id.llName)
    MineItemView editName;
    @BindView(R.id.radioMan)
    RadioButton radioMan;
    @BindView(R.id.radioWomen)
    RadioButton radioWomen;
    @BindView(R.id.llBrithday)
    MineItemView tvDate;
    @BindView(R.id.llHigh)
    MineItemView editHeight;
    @BindView(R.id.llWeight)
    MineItemView editWeight;
    @BindView(R.id.llMadicine)
    MineItemView tvShebei;
    @BindView(R.id.llBehaiver)
    MineItemView editBing;
    @BindView(R.id.llOptions)
    MineItemView tvAbut;
    @BindView(R.id.checkboxHeatEnable)
    CheckBox checkboxHeatEnable;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.loginout)
    Button loginout;
    public SettingFragment() {
    }
    HomeViewModel mHomeViewModel;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHomeViewModel= ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        mHomeViewModel.getmIsPainEnable().observe(this,b->{
            checkboxHeatEnable.setEnabled(b&&(mHomeViewModel.getmIsTeeEnable().getValue()==null?false:mHomeViewModel.getmIsTeeEnable().getValue()));
        });
        mHomeViewModel.getmIsTeeEnable().observe(this,b->{
            checkboxHeatEnable.setEnabled(b&&(mHomeViewModel.getmIsPainEnable().getValue()==null?false:mHomeViewModel.getmIsPainEnable().getValue()));
        });
        checkboxHeatEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    checkboxHeatEnable.setText("开启");
                    mHomeViewModel.getmAutoHeat().setValue(true);
                }else{
                    checkboxHeatEnable.setText("关闭");
                    mHomeViewModel.getmAutoHeat().setValue(false);
                }
            }
        });
        loginout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EMClient.getInstance().logout(true, new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onError(int code, String message) {
                        // TODO Auto-generated method stub

                    }
                });
            }
        });
    }

    @Override
    protected void initView() {

    }

    @Override
    protected  int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    public void onResume() {
        super.onResume();
        editName.setEditText(SharedPreferenceUtil.getValue(SPKey.NAME,""));
        tvDate.setEditText(SharedPreferenceUtil.getValue(SPKey.BIRTHDAY,""));
        editHeight.setEditText(SharedPreferenceUtil.getValue(SPKey.HEIGHT,""));
        editWeight.setEditText(SharedPreferenceUtil.getValue(SPKey.WEIGHT,""));
        tvShebei.setEditText(SharedPreferenceUtil.getValue(SPKey.DEVICE,""));
        editBing.setEditText(SharedPreferenceUtil.getValue(SPKey.DISEASE,""));
        tvAbut.setEditText(SharedPreferenceUtil.getValue(SPKey.ABOUT,""));
        radioMan.setChecked(SharedPreferenceUtil.getValue(SPKey.GENDER,false));
        radioWomen.setChecked(!radioMan.isChecked());
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferenceUtil.putValue(SPKey.NAME,editName.getEditText());
        SharedPreferenceUtil.putValue(SPKey.BIRTHDAY,tvDate.getEditText());
        SharedPreferenceUtil.putValue(SPKey.HEIGHT,editHeight.getEditText());
        SharedPreferenceUtil.putValue(SPKey.WEIGHT,editWeight.getEditText());
        SharedPreferenceUtil.putValue(SPKey.DEVICE,tvShebei.getEditText());
        SharedPreferenceUtil.putValue(SPKey.DISEASE,editBing.getEditText());
        SharedPreferenceUtil.putValue(SPKey.ABOUT,tvAbut.getEditText());
        SharedPreferenceUtil.putValue(SPKey.GENDER,radioMan.isChecked());
    }
}
