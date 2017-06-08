package com.example.hbl.bluetooth;


import android.support.v4.app.Fragment;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import butterknife.BindView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends BaseFragment {


    @BindView(R.id.editName)
    EditText editName;
    @BindView(R.id.radioMan)
    RadioButton radioMan;
    @BindView(R.id.radioWomen)
    RadioButton radioWomen;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.editHeight)
    EditText editHeight;
    @BindView(R.id.editWeight)
    EditText editWeight;
    @BindView(R.id.tvShebei)
    TextView tvShebei;
    @BindView(R.id.editBing)
    EditText editBing;
    @BindView(R.id.tvAbut)
    EditText tvAbut;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    public SettingFragment() {
    }



    @Override
    int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    public void onResume() {
        super.onResume();
        editName.setText(SharedPreferenceUtil.getValue(SPKey.NAME,""));
        tvDate.setText(SharedPreferenceUtil.getValue(SPKey.BIRTHDAY,""));
        editHeight.setText(SharedPreferenceUtil.getValue(SPKey.HEIGHT,""));
        editWeight.setText(SharedPreferenceUtil.getValue(SPKey.WEIGHT,""));
        tvShebei.setText(SharedPreferenceUtil.getValue(SPKey.DEVICE,""));
        editBing.setText(SharedPreferenceUtil.getValue(SPKey.DISEASE,""));
        tvAbut.setText(SharedPreferenceUtil.getValue(SPKey.ABOUT,""));
        radioMan.setChecked(SharedPreferenceUtil.getValue(SPKey.GENDER,false));
        radioWomen.setChecked(!radioMan.isEnabled());
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferenceUtil.putValue(SPKey.NAME,editName.getText().toString().trim());
        SharedPreferenceUtil.putValue(SPKey.BIRTHDAY,tvDate.getText().toString().trim());
        SharedPreferenceUtil.putValue(SPKey.HEIGHT,editHeight.getText().toString().trim());
        SharedPreferenceUtil.putValue(SPKey.WEIGHT,editWeight.getText().toString().trim());
        SharedPreferenceUtil.putValue(SPKey.DEVICE,tvShebei.getText().toString().trim());
        SharedPreferenceUtil.putValue(SPKey.DISEASE,editBing.getText().toString().trim());
        SharedPreferenceUtil.putValue(SPKey.ABOUT,tvAbut.getText().toString().trim());
        SharedPreferenceUtil.putValue(SPKey.GENDER,radioMan.isChecked());
    }
}
