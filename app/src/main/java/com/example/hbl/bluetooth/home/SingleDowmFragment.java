package com.example.hbl.bluetooth.home;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.hbl.bluetooth.BaseFragment;
import com.example.hbl.bluetooth.Order;
import com.example.hbl.bluetooth.R;
import com.example.hbl.bluetooth.network.ToastUtil;
import com.example.hbl.bluetooth.search.SearchActivity;
import com.example.hbl.bluetooth.util.SPKey;
import com.example.hbl.bluetooth.util.SharedPreferenceUtil;
import com.example.hbl.bluetooth.view.CircleSeekBar;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by huangbaole on 2017/12/7.
 */

public class SingleDowmFragment extends BaseFragment {
    @BindView(R.id.imBack)
    ImageView imBack;
    @BindView(R.id.cricle_seek_bar)
    CircleSeekBar cricleSeekBar;
    @BindView(R.id.upText)
    ImageView upText;//蓝牙状态开关，左侧
    @BindView(R.id.upCheck)
    ImageView upCheck;//蓝牙开关
    @BindView(R.id.clock)
    TextView clock;
    @BindView(R.id.upImageEn)
    ImageView upImageEn;//点量开关
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.image_min)
    ImageView imageMin;
    @BindView(R.id.sbTime)
    SeekBar sbTime;
    Unbinder unbinder;
    private Handler handler=new Handler();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_single_op;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModelObserver();
    }

    @Override
    protected void initView() {
        cricleSeekBar.setChangeListener(new CircleSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChange(int duration, int progress) {

            }

            @Override
            public void onProgressChangeEnd(int duration, int progress) {
                mHomeViewModel.sendOrderDown(Order.WRITE_HEAT + toHex(progress));

            }
        });
        sbTime.setOnSeekBarChangeListener(new SingleDowmFragment.OnSeekChangeListenr() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mHomeViewModel.sendOrderUp(Order.WRITE_TIME + timeToHex(progress * 60));
                mHomeViewModel.sendOrderDown(Order.WRITE_TIME + timeToHex(progress * 60));
                resetTimer(progress * 60);
            }
        });
    }



    public abstract class OnSeekChangeListenr implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }
    }

    private CountDownTimer timer;

    public long currentMills;

    private void resetTimer(int progress) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new CountDownTimer(progress * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                currentMills = millisUntilFinished;
                if (isVisible()) {
                    sbTime.setProgress((int) millisUntilFinished / 1000 / 60);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            time.setText(((int) millisUntilFinished / 1000 / 60)+"");
                        }
                    });
                }
            }

            @Override
            public void onFinish() {
                currentMills = 0;
                if (isVisible()) {
                    sbTime.setProgress(0);
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHomeViewModel.sendOrderUp(Order.WRITE_CLOSE);
                mHomeViewModel.sendOrderDown(Order.WRITE_CLOSE);

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
            sbTime.setProgress((int) ((value - l) / 1000 / 60));
        }
    }

    HomeViewModel mHomeViewModel;

    private void initViewModelObserver() {
        mHomeViewModel = ViewModelProviders.of(getParentFragment().getActivity()).get(HomeViewModel.class);
//        mHomeViewModel.getmUpText().observe(this, str -> upText.setText(str));
//        mHomeViewModel.getmDownText().observe(this, str -> downText.setText(str));
        //蓝牙连接状态
//        mHomeViewModel.getmUptextState().observe(this, state -> setTextDrawable(upText, state));
//        mHomeViewModel.getmDowntextState().observe(this, state -> setTextDrawable(downText, state));
//        mHomeViewModel.getmUptextVisible().observe(this, visi -> upText.setVisibility(visi ? View.VISIBLE : View.GONE));
        mHomeViewModel.getmDownImg().observe(this, integer -> {
            if(integer==null)return;

            switch (integer){
                case R.drawable.power_low:
                    upImageEn.setImageResource(R.drawable.single_power_low);
                    break;
                case R.drawable.power_three:
                    upImageEn.setImageResource(R.drawable.single_power_seven);
                    break;
                case R.drawable.power_half:
                    upImageEn.setImageResource(R.drawable.single_power_five);
                    break;
                case R.drawable.power_max:
                    upImageEn.setImageResource(R.drawable.single_power_max);
                    break;
            }
        });
//        mHomeViewModel.getmUpImgVisible().observe(this, visible -> upImageEn.setVisibility(visible ? View.VISIBLE : View.GONE));
        mHomeViewModel.getmDownProgress().observe(this, integer -> {
            if(integer==null)return;

            if (cricleSeekBar.isEnabled()) {
                cricleSeekBar.setProgress(integer);
                mHomeViewModel.sendOrderDown(Order.WRITE_HEAT + toHex(integer));
            } else {
                ToastUtil.show("当前蓝牙设备不可用");
            }
        });

        mHomeViewModel.getmTimeProgress().observe(this, integer -> {
            if(integer==null)return;
            if (sbTime.isEnabled()) {
                sbTime.setProgress(integer);
                mHomeViewModel.sendOrderUp(Order.WRITE_TIME + timeToHex(integer * 60));
                mHomeViewModel.sendOrderDown(Order.WRITE_TIME + timeToHex(integer * 60));
            } else {
                ToastUtil.show("当前蓝牙设备不可用");
            }
        });
        mHomeViewModel.getmIsPainEnable().observe(this, b -> {
            if(b==null)return;
            upText.setImageResource(b?R.drawable.bletooth_on:R.drawable.bluetoon_off);
            cricleSeekBar.setEnabled(b);
        });


        mHomeViewModel.getmDownSwitch().observe(this, b -> {
            if(b==null)return;

            upCheck.setImageResource(b ? R.drawable.duble_bluetoooth_on : R.drawable.double_bluetootn_off);
        });

        mHomeViewModel.getmDownSwitch().setValue(false);
        mHomeViewModel.getmDownSwitch().setValue(false);


    }


    @OnClick({R.id.imBack, R.id.upText, R.id.upCheck})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imBack:
                getActivity().onBackPressed();
                break;
            case R.id.upText:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            case R.id.upCheck:
                mHomeViewModel.getmDownSwitch().setValue(!mHomeViewModel.getmDownSwitch().getValue());
                if (mHomeViewModel.getmDownSwitch().getValue()) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mHomeViewModel.sendOrderDown(Order.READ_ENERGY);
                        }
                    }, 30000);
                }
                mHomeViewModel.sendOrderDown(mHomeViewModel.getmDownSwitch().getValue() ? Order.WRITE_OPEN : Order.WRITE_CLOSE);
                break;
        }
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
}
