package com.example.hbl.bluetooth.home;


import android.support.v4.app.Fragment;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.hbl.bluetooth.BaseFragment;
import com.example.hbl.bluetooth.R;
import com.example.hbl.bluetooth.util.SPKey;
import com.example.hbl.bluetooth.util.SharedPreferenceUtil;
import com.example.hbl.bluetooth.view.MineItemView;

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
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    public SettingFragment() {
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
