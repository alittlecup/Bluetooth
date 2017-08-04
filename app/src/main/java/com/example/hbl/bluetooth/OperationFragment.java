package com.example.hbl.bluetooth;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.example.hbl.bluetooth.network.DefaultCallback;
import com.example.hbl.bluetooth.network.RetrofitUtil;
import com.example.hbl.bluetooth.network.ToastUtil;
import com.example.hbl.bluetooth.network.bean.BaseResponse;
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
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.edit)
    EditText editText;
    Unbinder unbinder;
    HomeActivity activity;
    private int mTeeGrade = 0;
    private int mPanGrade = 0;
    private int mTimeGrade = 0;

    public OperationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_operation, container, false);
        unbinder = ButterKnife.bind(this, view);
        rlPants.setEnabled(false);
        rlTee.setEnabled(false);
        sbTee.setEnabled(false);
        sbPans.setEnabled(false);
        activity = (HomeActivity) getActivity();
        sbTee.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {
                mTeeGrade = progress;
            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {
            }
        });
        sbPans.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {
                mPanGrade = progress;
            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {

            }
        });
        sbTime.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {
                mTimeGrade = progress;
            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {

            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.imTee, R.id.imPans, R.id.btnStart})
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
                sendOrder();
//                saveData();
                break;
        }
    }

    private void saveData() {
        ModelData data = new ModelData();
        data.setUp(String.valueOf(mTeeGrade));
        data.setDown(String.valueOf(mPanGrade));
        data.setTime(String.valueOf(mTimeGrade));
        App.addData(data);
        RetrofitUtil.getService()
                .setMode(App.tel, data.getUp(), data.getDown(), data.getTime())
                .enqueue(new DefaultCallback<BaseResponse>() {
                    @Override
                    public void onFinish(int status, BaseResponse body) {
                        if (status == DefaultCallback.SUCCESS) {
                            ToastUtil.show("保存成功");
                        } else {
                            ToastUtil.show("保存失败");
                        }
                    }
                });
    }

    private void sendOrder() {
        activity.addOrder(Order.WRITE_HEAT + toHex(mTeeGrade));
//        activity.addOrder(Order.WRITE_TIME+Integer.toHexString(mTimeGrade));
        activity.addOrder2(Order.WRITE_HEAT + toHex(mPanGrade));
//        activity.addOrder2(Order.WRITE_TIME+Integer.toHexString(mTimeGrade));
    }

    private String toHex(int i) {
        if (i < 16) {
            return "0" + Integer.toHexString(i);
        } else {
            return Integer.toHexString(i);
        }
    }
}



