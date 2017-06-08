package com.example.hbl.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private Handler mHandler = new Handler();
    private boolean mScanning;
    private static final long SCAN_PERIOD = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        checkBluetooth();
        changeUI(false);
        findViewById(R.id.enter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enter();
            }
        });
    }

    @OnClick({R.id.btnYz, R.id.btnLogin, R.id.btnSerach})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnYz:
                break;
            case R.id.btnSerach:
                scanLeDevice(true);
                break;
            case R.id.btnLogin:
                showDialog("验证中");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismissDialog();
                        changeUI(true);
                    }
                }, 1000);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT
                && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void changeUI(boolean showSerach) {
        btnSerach.setVisibility(showSerach ? View.VISIBLE : View.GONE);
        ivBluetooth.setVisibility(showSerach ? View.VISIBLE : View.GONE);
        editPhone.setVisibility(showSerach ? View.GONE : View.VISIBLE);
        llYz.setVisibility(showSerach ? View.GONE : View.VISIBLE);
        btnLogin.setVisibility(showSerach ? View.GONE : View.VISIBLE);
        vieLine.setVisibility(showSerach ? View.GONE : View.VISIBLE);
        viewLine.setVisibility(showSerach ? View.GONE : View.VISIBLE);

    }

    private void enter() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void checkBluetooth() {
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT)
                    .show();
            finish();
        }

        // Initializes a Bluetooth adapter. For API level 18 and above, get a
        // reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported,
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanLeDevice(false);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        btnSerach.setEnabled(!enable);
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                             byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("TAG", "run: " + device.getAddress() + '/' + device.getName());
                    tvDevice.setText("蓝牙地址： "+device.getAddress()+" 蓝牙名称： "+device.getName());
                }
            });
        }
    };

}
