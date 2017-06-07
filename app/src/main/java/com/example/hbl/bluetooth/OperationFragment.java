package com.example.hbl.bluetooth;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.xw.repo.BubbleSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class OperationFragment extends Fragment {


    @BindView(R.id.imTee)
    ImageButton imTee;
    @BindView(R.id.sbTee)
    BubbleSeekBar sbTee;
    @BindView(R.id.rlTee)
    RelativeLayout rlTee;
    @BindView(R.id.imPans)
    ImageButton imPans;
    @BindView(R.id.sbPans)
    BubbleSeekBar sbPans;
    @BindView(R.id.rlPants)
    RelativeLayout rlPants;
    @BindView(R.id.sbTime)
    BubbleSeekBar sbTime;
    @BindView(R.id.btnStart)
    Button btnStart;
    @BindView(R.id.btnSave)
    Button btnSave;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    Unbinder unbinder;

    public OperationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_operation, container, false);
        unbinder = ButterKnife.bind(this, view);
        rlPants.setEnabled(false);
        rlTee.setEnabled(false);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.imTee, R.id.imPans, R.id.btnStart, R.id.btnSave})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imTee:
                rlTee.setEnabled(!rlTee.isEnabled());
                sbTee.setEnabled(rlTee.isEnabled());
                break;
            case R.id.imPans:
                rlPants.setEnabled(!rlPants.isEnabled());
                sbPans.setEnabled(rlPants.isEnabled());
                break;
            case R.id.btnStart:
                break;
            case R.id.btnSave:
                break;
        }
    }
}



