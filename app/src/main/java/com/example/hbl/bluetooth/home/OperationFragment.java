package com.example.hbl.bluetooth.home;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.util.TypedValue;
import android.view.Gravity;
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
import com.example.hbl.bluetooth.search.SearchActivity;
import com.example.hbl.bluetooth.util.SPKey;
import com.example.hbl.bluetooth.util.SharedPreferenceUtil;
import com.example.hbl.bluetooth.view.ObservableScrollView;
import com.example.hbl.bluetooth.view.UltraViewPagerAdapter;
import com.tmall.ultraviewpager.UltraViewPager;
import com.tmall.ultraviewpager.transformer.UltraScaleTransformer;
import com.xw.repo.BubbleSeekBar;

import java.util.ArrayList;
import java.util.List;

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
    @BindView(R.id.ivBleFind)
    ImageView ivBleFind;
    @BindView(R.id.sbPans)
    BubbleSeekBar sbPans;
    @BindView(R.id.sbTime)
    BubbleSeekBar sbTime;
    @BindView(R.id.scrollView)
    ObservableScrollView scrollView;
    @BindView(R.id.ultra_viewpager)
    UltraViewPager ultraViewPager;
    private CountDownTimer timer;
    private HomeActivity activity;
    private long currentMills = 0;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewModelObserver();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (HomeActivity) getActivity();
        sbTee.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {
                mHomeViewModel.sendOrderUp(Order.WRITE_HEAT + toHex(progress));

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
                mHomeViewModel.sendOrderDown(Order.WRITE_HEAT + toHex(progress));
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
                mHomeViewModel.sendOrderUp(Order.WRITE_TIME + timeToHex(progress * 60));
                mHomeViewModel.sendOrderDown(Order.WRITE_TIME + timeToHex(progress * 60));
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
        initViewPager();


    }

    HomeViewModel mHomeViewModel;

    private void initViewModelObserver() {
        mHomeViewModel = ViewModelProviders.of(activity).get(HomeViewModel.class);
        mHomeViewModel.getmUpText().observe(this, str -> upText.setText(str));
        mHomeViewModel.getmDownText().observe(this, str -> downText.setText(str));
        mHomeViewModel.getmUptextState().observe(this, state -> setTextDrawable(upText, state));
        mHomeViewModel.getmDowntextState().observe(this, state -> setTextDrawable(downText, state));
        mHomeViewModel.getmUptextVisible().observe(this, visi -> upText.setVisibility(visi ? View.VISIBLE : View.GONE));
        mHomeViewModel.getmDowntextVisible().observe(this, visi -> downText.setVisibility(visi ? View.VISIBLE : View.GONE));
        mHomeViewModel.getmUpImg().observe(this, integer -> upImageEn.setImageResource(integer));
        mHomeViewModel.getmDownImg().observe(this, integer -> downImageEn.setImageResource(integer));
        mHomeViewModel.getmUpImgVisible().observe(this, visible -> upImageEn.setVisibility(visible ? View.VISIBLE : View.GONE));
        mHomeViewModel.getmDownImgVisible().observe(this, visible -> downImageEn.setVisibility(visible ? View.VISIBLE : View.GONE));
        mHomeViewModel.getmUpProgress().observe(this, integer -> {
            if (sbTee.isEnabled()) {
                sbTee.setProgress(integer);
                mHomeViewModel.sendOrderUp(Order.WRITE_HEAT + toHex(integer));
            } else {
                ToastUtil.show("当前蓝牙设备不可用");
            }
        });
        mHomeViewModel.getmDownProgress().observe(this, integer -> {
            if (sbPans.isEnabled()) {
                sbPans.setProgress(integer);
                mHomeViewModel.sendOrderDown(Order.WRITE_HEAT + toHex(integer));
            } else {
                ToastUtil.show("当前蓝牙设备不可用");
            }
        });
        mHomeViewModel.getmTimeProgress().observe(this, integer -> {
            if (sbTime.isEnabled()) {
                sbTime.setProgress(integer);
                mHomeViewModel.sendOrderUp(Order.WRITE_TIME + timeToHex(integer * 60));
                mHomeViewModel.sendOrderDown(Order.WRITE_TIME + timeToHex(integer * 60));
            } else {
                ToastUtil.show("当前蓝牙设备不可用");
            }
        });
        mHomeViewModel.getmIsTeeEnable().observe(this,b->{
            upText.setEnabled(b);
            sbTee.setEnabled(b);
            imageUp.setEnabled(b);
            sbTime.setEnabled(b||(mHomeViewModel.getmIsPainEnable().getValue()==null?false:mHomeViewModel.getmIsPainEnable().getValue()));
        });
        mHomeViewModel.getmIsPainEnable().observe(this,b->{
            downText.setEnabled(b);
            sbPans.setEnabled(b);
            imageDown.setEnabled(b);
            sbTime.setEnabled(b||(mHomeViewModel.getmIsTeeEnable().getValue()==null?false:mHomeViewModel.getmIsTeeEnable().getValue()));
        });

        mHomeViewModel.getmUpSwitch().observe(this,b->{
            upCheck.setImageResource(b ? R.drawable.opear_ble_open : R.drawable.opear_ble_close);
        });
        mHomeViewModel.getmDownSwitch().observe(this,b->{
            downCheck.setImageResource(b ? R.drawable.opear_ble_open : R.drawable.opear_ble_close);
        });
        mHomeViewModel.getmUpSwitch().setValue(false);
        mHomeViewModel.getmDownSwitch().setValue(false);


    }

    private void setTextDrawable(TextView tv, boolean opean) {
        Drawable drawable = getResources().getDrawable(opean ? R.drawable.opear_ble : R.drawable.opear_ble_dis);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tv.setCompoundDrawables(drawable, null, null, null);
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
                if (isVisible()) {
                    sbTime.setProgress((int) millisUntilFinished / 1000 / 60);
                }
            }

            @Override
            public void onFinish() {
                currentMills = 0;
                if (isVisible()) {
                    sbTime.setProgress(0);
                    downCheck.setPressed(true);
                    upCheck.setPressed(true);
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
            sbTime.setProgress((value - l) / 1000 / 60);
        }
    }

    private Handler handler = new Handler();

    @OnClick({R.id.downCheck, R.id.upCheck, R.id.ivBleFind, R.id.upText, R.id.downText})
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
                mHomeViewModel.sendOrderDown(mHomeViewModel.getmDownSwitch().getValue()? Order.WRITE_OPEN : Order.WRITE_CLOSE);
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
            case R.id.ivBleFind:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            case R.id.upText:
                if (upText.getText().toString().contains("断开")) {
                    activity.connect(1);
                }
                break;
            case R.id.downText:
                if (downText.getText().toString().contains("断开")) {
                    activity.connect(2);
                }
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


    private void initViewPager() {
        List<Integer> draws = new ArrayList<>();
        draws.add(R.drawable.banner);
        draws.add(R.drawable.banner2);
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
//UltraPagerAdapter 绑定子view到UltraViewPager
        PagerAdapter adapter = new UltraViewPagerAdapter(draws);
        ultraViewPager.setAdapter(adapter);
        //内置indicator初始化
        ultraViewPager.initIndicator();
        //设置indicator样式
        ultraViewPager.getIndicator()
                .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                .setFocusColor(getResources().getColor(R.color.getcode))
                .setNormalColor(Color.WHITE)
                .setRadius((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
//设置indicator对齐方式
        ultraViewPager.getIndicator().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        ultraViewPager.getIndicator().setMargin(0, 0, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
//构造indicator,绑定到UltraViewPager
        ultraViewPager.getIndicator().build();

//设定页面循环播放
        ultraViewPager.setInfiniteLoop(true);
        ultraViewPager.setPageTransformer(false, new UltraScaleTransformer());
//设定页面自动切换  间隔2秒
        ultraViewPager.setAutoScroll(5000);
    }
}



