package com.example.hbl.bluetooth;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.example.hbl.bluetooth.network.DefaultCallback;
import com.example.hbl.bluetooth.network.RetrofitUtil;
import com.example.hbl.bluetooth.network.ToastUtil;
import com.xw.repo.BubbleSeekBar;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class OperationFragment extends BaseFragment {


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
    @BindView(R.id.btnreadtime)
    Button btnreadtime;
    @BindView(R.id.scrollView)
    ObservableScrollView scrollView;
    @BindView(R.id.edit)
    EditText editText;
    Unbinder unbinder;
    HomeActivity activity;
    @BindView(R.id.message1)
    MyTextView tv1;
    @BindView(R.id.message2)
    MyTextView tv2;
    private int mTeeGrade = 0;
    private int mPanGrade = 0;
    private int mTimeGrade = 0;
    private CountDownTimer timer;

    public OperationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rlPants.setEnabled(App.ISPAINENABLE);
        rlTee.setEnabled(App.ISTEEENABLE);
        sbTee.setEnabled(App.ISTEEENABLE);
        sbPans.setEnabled(App.ISPAINENABLE);
        imTee.setClickable(App.ISTEEENABLE);
        imPans.setClickable(App.ISPAINENABLE);
        activity = (HomeActivity) getActivity();
        sbTee.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {
                activity.addOrder(Order.WRITE_HEAT + toHex(progress));

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
                activity.addOrder2(Order.WRITE_HEAT + toHex(progress));
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
                activity.addOrder(Order.WRITE_TIME + timeToHex(progress*60));
                activity.addOrder2(Order.WRITE_TIME + timeToHex(progress*60));
                resetTimer(progress*60);
            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {

            }
        });
        scrollView.addOnScrollChangeListener(new ObservableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(ScrollView view, int x, int y, int oldx, int oldy) {
                sbPans.correctOffsetWhenContainerOnScrolling();
                sbTime.correctOffsetWhenContainerOnScrolling();
                sbTee.correctOffsetWhenContainerOnScrolling();
            }
        });
    }

    @Override
    int getLayoutId() {
        return R.layout.fragment_operation;
    }

    private void resetTimer(int progress) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new CountDownTimer(progress*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                currentMills=  millisUntilFinished;
                sbTime.setProgress((int)millisUntilFinished/1000/60);
            }

            @Override
            public void onFinish() {
                currentMills=0;
                sbTime.setProgress(0);
                btnStart.setText("开");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                activity.addOrder(Order.WRITE_CLOSE);
                activity.addOrder2(Order.WRITE_CLOSE);

            }
        };
        timer.start();
    }
    private long currentMills=0;
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
        SharedPreferenceUtil.putValue(SPKey.TIME,System.currentTimeMillis()+currentMills);
    }

    @Override
    public void onResume() {
        super.onResume();
        Long value = SharedPreferenceUtil.getValue(SPKey.TIME, 0L);
        long l = System.currentTimeMillis();
        if(l<value){
            sbTime.setProgress((value-l)/1000/60);
        }
    }

    @OnClick({R.id.imTee, R.id.imPans, R.id.btnStart, R.id.btnreadtime})
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
                sendOrder(btnStart.getText().toString().contains("开"));
//                saveData();
                break;
            case R.id.btnreadtime:
//                activity.addOrder(Order.READ_TIME);
//                activity.addOrder2(Order.READ_TIME);
                break;
        }
    }

    private void saveData() {
        final ModelData data = new ModelData();
        data.setUp(String.valueOf(sbTee.getProgress()));
        data.setDown(String.valueOf(sbPans.getProgress()));
        data.setTime(String.valueOf(sbTime.getProgress()));
        RetrofitUtil.getService()
                .setMode(App.tel, data.getUp(), data.getDown(), data.getTime())
                .enqueue(new DefaultCallback<ResultData>() {
                    @Override
                    public void onFinish(int status, ResultData body) {
                        if (status == DefaultCallback.SUCCESS||body.status==1) {
                            ToastUtil.show("保存成功");
                            App.addData(data);
                        } else {
                            ToastUtil.show("保存失败");
                        }
                    }
                });
    }

    private void sendOrder(boolean isOpen) {
        if(isOpen){
            btnStart.setText("关");
        }else {
            btnStart.setText("开");
        }
        activity.addOrder(isOpen?Order.WRITE_OPEN:Order.WRITE_CLOSE);
        activity.addOrder2(isOpen?Order.WRITE_OPEN:Order.WRITE_CLOSE);
    }

    private String toHex(int i) {
        if (i < 16) {
            return "0" + Integer.toHexString(i);
        } else {
            return Integer.toHexString(i);
        }
    }

    private String timeToHex(int secondes) {
        if (secondes <= 15) {
            return "000" + Integer.toHexString(secondes);
        } else if (secondes <= 255) {
            return "00" + Integer.toHexString(secondes);
        } else if (secondes <= 4095) {
            return "0" + Integer.toHexString(secondes);
        } else {
            return Integer.toHexString(secondes);
        }
    }
    public  MyTextView getTextView(){
        return tv1;
    }
    public  MyTextView getText2View(){
        return tv2;
    }
}



