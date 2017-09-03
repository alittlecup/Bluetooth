package com.example.hbl.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hbl.bluetooth.home.HomeActivity;
import com.example.hbl.bluetooth.network.BLog;
import com.example.hbl.bluetooth.network.DefaultCallback;
import com.example.hbl.bluetooth.network.RetrofitUtil;
import com.example.hbl.bluetooth.network.ToastUtil;
import com.example.hbl.bluetooth.network.bean.CodeResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.editPhone)
    EditText editPhone;
    @BindView(R.id.editYz)
    EditText editYz;
    @BindView(R.id.btnYz)
    Button btnYz;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.viewLine)
    View viewLine;
    @BindView(R.id.llYz)
    LinearLayout llYz;
    @BindView(R.id.vieLine)
    View vieLine;
    @BindView(R.id.ivBluetooth)
    ImageView ivBluetooth;
    @BindView(R.id.btnSerach)
    Button btnSerach;
    @BindView(R.id.tvDevice)
    TextView tvDevice;
    @BindView(R.id.lllogin)
    LinearLayout lllogin;
    @BindView(R.id.tvName1)
    TextView tvName1;
    @BindView(R.id.tvName2)
    TextView tvName2;
    @BindView(R.id.btnMatch)
    Button btnMatch;
    @BindView(R.id.llserach)
    LinearLayout llserach;
    @BindView(R.id.ll_tv1)
    LinearLayout ll1;
    @BindView(R.id.ll_tv2)
    LinearLayout ll2;
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private Handler mHandler = new Handler();
    private static final long SCAN_PERIOD = 10000;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        btnSerach.setEnabled(false);
    }

    @OnClick({R.id.btnYz, R.id.btnLogin, R.id.btnSerach, R.id.btnMatch})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnYz:
                getCode();
                sendYZM();
                break;
            case R.id.btnSerach:
                scanLeDevice(true);
                break;
            case R.id.btnMatch:
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("hotup",map.get("hotup"));
                intent.putExtra("hotdw",map.get("hotdw"));
                startActivity(intent);
                finish();
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                break;
            case R.id.btnLogin:
                if (editYz.getText().toString().trim().equals(code)) {
                    if (timer != null) {
                        timer.cancel();
                    }
                    btnSerach.setEnabled(true);
                } else {
                    ToastUtil.show("验证码不正确");
                }
                break;
        }
    }

    private String code = "";

    private void getCode() {
        RetrofitUtil.getService()
                .getCode(editPhone.getText().toString().trim())
                .enqueue(new DefaultCallback<CodeResponse>() {
                    @Override
                    public void onFinish(int status, CodeResponse body) {
                        if (status == DefaultCallback.SUCCESS) {
                            code = body.code;
                            App.tel = body.tel;
                            editYz.setText(body.code);
                        }
                        timer.cancel();
                        btnYz.setText("获取验证码");
                        btnYz.setEnabled(true);
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
        }
        scanLeDevice(false);
    }

    private void sendYZM() {
        timer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int last = (int) (millisUntilFinished / 1000);
                if (btnYz.isEnabled()) {
                    btnYz.setEnabled(false);
                }
                btnYz.setText("验证码(" + last + ")");
            }

            @Override
            public void onFinish() {
                btnYz.setText("获取验证码");
                btnYz.setEnabled(true);
            }
        }.start();
    }

    private void scanLeDevice(boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    btnSerach.setEnabled(true);
                    btnMatch.setEnabled(true);
                }
            }, SCAN_PERIOD);
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            btnSerach.setEnabled(false);
            return;
        }
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        btnSerach.setEnabled(true);



    }

    private ArrayMap<String,String> map = new ArrayMap<>();
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                             byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BLog.e(device.getAddress() + "/" + device.getName());
                    if ("hotup".equals(device.getName())) {
                        if (map.get("hotup")==null) {
                            map.put("hotup",device.getAddress());
                        }
                    }
                    if("hotdw".equals(device.getName())){
                        if ((map.get("hotdw")==null)) {
                            map.put("hotdw",device.getAddress());
                        }
                    }
                    if(map.get("hotup")!=null){
                        ivBluetooth.setVisibility(View.GONE);
                        tvName1.setText("衣");
                        ll1.setVisibility(View.VISIBLE);
                    }
                    if(map.get("hotdw")!=null){
                        tvName2.setText("裤");
                        ivBluetooth.setVisibility(View.GONE);
                        ll2.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    };

}
