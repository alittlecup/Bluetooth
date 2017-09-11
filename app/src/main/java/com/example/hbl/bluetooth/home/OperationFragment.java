package com.example.hbl.bluetooth.home;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.hbl.bluetooth.App;
import com.example.hbl.bluetooth.BaseFragment;
import com.example.hbl.bluetooth.ModelData;
import com.example.hbl.bluetooth.Order;
import com.example.hbl.bluetooth.R;
import com.example.hbl.bluetooth.ResultData;
import com.example.hbl.bluetooth.network.DefaultCallback;
import com.example.hbl.bluetooth.network.RetrofitUtil;
import com.example.hbl.bluetooth.network.ToastUtil;
import com.example.hbl.bluetooth.util.SPKey;
import com.example.hbl.bluetooth.util.SharedPreferenceUtil;
import com.example.hbl.bluetooth.view.ObservableScrollView;
import com.xw.repo.BubbleSeekBar;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class OperationFragment extends BaseFragment {


    @BindView(R.id.imageUp)
    ImageView imageUp;
    @BindView(R.id.upText)
    TextView upText;
    @BindView(R.id.upImageEn)
    ImageView upImageEn;
    @BindView(R.id.upCheck)
    ImageView upCheck;
    @BindView(R.id.sbTee)
    BubbleSeekBar sbTee;
    @BindView(R.id.imageDown)
    ImageView imageDown;
    @BindView(R.id.downText)
    TextView downText;
    @BindView(R.id.downImageEn)
    ImageView downImageEn;
    @BindView(R.id.downCheck)
    ImageView downCheck;
    @BindView(R.id.sbPans)
    BubbleSeekBar sbPans;
    @BindView(R.id.sbTime)
    BubbleSeekBar sbTime;
    @BindView(R.id.scrollView)
    ObservableScrollView scrollView;
    private CountDownTimer timer;
    private HomeActivity activity;
    private long currentMills = 0;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        upText.setEnabled(App.ISPAINENABLE);
        downText.setEnabled(App.ISTEEENABLE);
        sbTee.setEnabled(App.ISTEEENABLE);
        sbPans.setEnabled(App.ISPAINENABLE);
        imageUp.setClickable(App.ISTEEENABLE);
        imageDown.setClickable(App.ISPAINENABLE);
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
                activity.addOrder(Order.WRITE_TIME + timeToHex(progress * 60));
                activity.addOrder2(Order.WRITE_TIME + timeToHex(progress * 60));
                resetTimer(progress * 60);
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
    protected int getLayoutId() {
        return R.layout.operator_fragment;
    }

    private void resetTimer(int progress) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new CountDownTimer(progress * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                currentMills = millisUntilFinished;
                sbTime.setProgress((int) millisUntilFinished / 1000 / 60);
            }

            @Override
            public void onFinish() {
                currentMills = 0;
                sbTime.setProgress(0);
                downCheck.setPressed(true);
                upCheck.setPressed(true);
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


    @Override
    public void onResume() {
        super.onResume();
        Long value = SharedPreferenceUtil.getValue(SPKey.TIME, 0L);
        long l = System.currentTimeMillis();
        if (l < value) {
            sbTime.setProgress((value - l) / 1000 / 60);
        }
    }

    private Handler handler = new Handler();
    public static boolean isDownOpened;
    public static boolean isUpOpened;

    @OnClick({R.id.downCheck, R.id.upCheck})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.downCheck:
                activity.addOrder2(!isDownOpened? Order.WRITE_OPEN : Order.WRITE_CLOSE);
                if (!isDownOpened) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            activity.addOrder2(Order.READ_ENERGY);
                        }
                    }, 30000);
                }
                downCheck.setImageResource(!isDownOpened?R.drawable.opear_ble_open:R.drawable.opear_ble_close);
                isDownOpened = !isDownOpened;
                break;
            case R.id.upCheck:
                activity.addOrder(!isUpOpened? Order.WRITE_OPEN : Order.WRITE_CLOSE);
                if (!isUpOpened) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            activity.addOrder(Order.READ_ENERGY);
                        }
                    }, 30000);
                }
                upCheck.setImageResource(!isUpOpened?R.drawable.opear_ble_open:R.drawable.opear_ble_close);
                isUpOpened = !isUpOpened;
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
                        if (status == DefaultCallback.SUCCESS || body.status == 1) {
                            ToastUtil.show("保存成功");
                            App.addData(data);
                        } else {
                            ToastUtil.show("保存失败");
                        }
                    }
                });
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

    public TextView getTextView() {
        return upText;
    }

    public TextView getText2View() {
        return downText;
    }


    public ImageView getUpImage() {
        return upImageEn;
    }

    public ImageView getDownImage() {
        return downImageEn;
    }

    public ImageView getUpCheck() {
        return upCheck;
    }

    public ImageView getDownCheck() {
        return downCheck;
    }

}



