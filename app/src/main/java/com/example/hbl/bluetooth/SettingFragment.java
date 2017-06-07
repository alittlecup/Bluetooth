package com.example.hbl.bluetooth;


import android.support.v4.app.Fragment;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.Unbinder;


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
    Unbinder unbinder;
    String TAG="SettingFragment";
    public SettingFragment() {
        // Required empty public constructor
    }



    @Override
    int getLayoutId() {
        return R.layout.fragment_setting;
    }


}
