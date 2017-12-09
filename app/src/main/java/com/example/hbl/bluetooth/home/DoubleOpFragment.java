package com.example.hbl.bluetooth.home;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by huangbaole on 2017/12/2.
 */

public class DoubleOpFragment extends BaseFragment {


    @BindView(R.id.top_cloth)
    ImageView topCloth;
    @BindView(R.id.upText)
    ImageView upText;
    @BindView(R.id.upImageEn)
    ImageView upImageEn;
    @BindView(R.id.upCheck)
    ImageView upCheck;
    @BindView(R.id.sbTee)
    SeekBar sbTee;
    @BindView(R.id.down_cloth)
    ImageView downCloth;
    @BindView(R.id.downText)
    ImageView downText;
    @BindView(R.id.downImageEn)
    ImageView downImageEn;
    @BindView(R.id.downCheck)
    ImageView downCheck;
    @BindView(R.id.sbPans)
    SeekBar sbPans;
    @BindView(R.id.clock)
    TextView clock;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.image_min)
    ImageView imageMin;
    @BindView(R.id.sbTime)
    SeekBar sbTime;
    private long currentMills;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_double_op;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewModelObserver();
    }

    @Override
    protected void initView() {
        sbTee.setOnSeekBarChangeListener(new OnSeekChangeListenr() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHomeViewModel.sendOrderUp(Order.WRITE_HEAT + toHex(seekBar.getProgress()));
            }
        });
        sbPans.setOnSeekBarChangeListener(new OnSeekChangeListenr() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHomeViewModel.sendOrderDown(Order.WRITE_HEAT + toHex(seekBar.getProgress()));
            }
        });
        sbTime.setOnSeekBarChangeListener(new OnSeekChangeListenr() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mHomeViewModel.sendOrderUp(Order.WRITE_TIME + timeToHex(progress * 60));
                mHomeViewModel.sendOrderDown(Order.WRITE_TIME + timeToHex(progress * 60));
                resetTimer(progress * 60);
            }
        });
    }


    private CountDownTimer timer;

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
                            time.setText(((int) millisUntilFinished / 1000 / 60) + "");
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

    public abstract class OnSeekChangeListenr implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }
    }

    Handler handler = new Handler();

    @OnClick({R.id.downCheck, R.id.upCheck, R.id.imBack, R.id.upText, R.id.downText})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.downCheck:
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
            case R.id.upCheck:
                mHomeViewModel.getmUpSwitch().setValue(!mHomeViewModel.getmUpSwitch().getValue());
                if (mHomeViewModel.getmUpSwitch().getValue()) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mHomeViewModel.sendOrderUp(Order.READ_ENERGY);
                        }
                    }, 30000);
                }
                mHomeViewModel.sendOrderUp(mHomeViewModel.getmUpSwitch().getValue() ? Order.WRITE_OPEN : Order.WRITE_CLOSE);
                break;
            case R.id.imBack:
                getActivity().onBackPressed();
                break;
            case R.id.upText:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            case R.id.downText:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;

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
//        mHomeViewModel.getmDowntextVisible().observe(this, visi -> downText.setVisibility(visi ? View.VISIBLE : View.GONE));
        mHomeViewModel.getmUpImg().observe(this, integer -> {
            if (integer == null) return;

            upImageEn.setImageResource(integer);
        });
        mHomeViewModel.getmDownImg().observe(this, integer -> {
            if(integer==null)return;

            downImageEn.setImageResource(integer);
        });
//        mHomeViewModel.getmUpImgVisible().observe(this, visible -> upImageEn.setVisibility(visible ? View.VISIBLE : View.GONE));
//        mHomeViewModel.getmDownImgVisible().observe(this, visible -> downImageEn.setVisibility(visible ? View.VISIBLE : View.GONE));
        mHomeViewModel.getmUpProgress().observe(this, integer -> {
            if (integer == null) return;
            if (sbTee.isEnabled()) {
                sbTee.setProgress(integer);
                mHomeViewModel.sendOrderUp(Order.WRITE_HEAT + toHex(integer));
            } else {
                ToastUtil.show("当前蓝牙设备不可用");
            }
        });
        mHomeViewModel.getmDownProgress().observe(this, integer -> {
            if (integer == null) return;
            if (sbPans.isEnabled()) {
                sbPans.setProgress(integer);
                mHomeViewModel.sendOrderDown(Order.WRITE_HEAT + toHex(integer));
            } else {
                ToastUtil.show("当前蓝牙设备不可用");
            }
        });
        mHomeViewModel.getmTimeProgress().observe(this, integer -> {
            if (integer == null) return;

            if (sbTime.isEnabled()) {
                sbTime.setProgress(integer);
                mHomeViewModel.sendOrderUp(Order.WRITE_TIME + timeToHex(integer * 60));
                mHomeViewModel.sendOrderDown(Order.WRITE_TIME + timeToHex(integer * 60));
            } else {
                ToastUtil.show("当前蓝牙设备不可用");
            }
        });
        mHomeViewModel.getmIsTeeEnable().observe(this, b -> {
            if (b == null) return;
            upText.setImageResource(b ? R.drawable.bletooth_on : R.drawable.bluetoon_off);
            sbTee.setEnabled(b);
            sbTime.setEnabled(b || (mHomeViewModel.getmIsPainEnable().getValue() == null ? false : mHomeViewModel.getmIsPainEnable().getValue()));
        });
        mHomeViewModel.getmIsPainEnable().observe(this, b -> {
            if (b == null) return;
            downText.setImageResource(b ? R.drawable.bletooth_on : R.drawable.bluetoon_off);
            sbPans.setEnabled(b);
            sbTime.setEnabled(b || (mHomeViewModel.getmIsTeeEnable().getValue() == null ? false : mHomeViewModel.getmIsTeeEnable().getValue()));
        });

        mHomeViewModel.getmUpSwitch().observe(this, b -> {
            if (b == null) return;
            upCheck.setImageResource(b ? R.drawable.duble_bluetoooth_on : R.drawable.double_bluetootn_off);
        });
        mHomeViewModel.getmDownSwitch().observe(this, b -> {
            if (b == null) return;
            downCheck.setImageResource(b ? R.drawable.duble_bluetoooth_on : R.drawable.double_bluetootn_off);
        });
        mHomeViewModel.getmUpSwitch().setValue(false);
        mHomeViewModel.getmDownSwitch().setValue(false);


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
