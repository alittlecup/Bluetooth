package com.example.hbl.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.hbl.bluetooth.bluetooth_old.SampleGattAttributes;
import com.example.hbl.bluetooth.network.BLog;
import com.example.hbl.bluetooth.network.DefaultCallback;
import com.example.hbl.bluetooth.network.RetrofitUtil;
import com.example.hbl.bluetooth.network.ToastUtil;
import com.example.hbl.bluetooth.newblue.BluetoothLeService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.realtabcontent)
    FrameLayout realtabcontent;
    @BindView(android.R.id.tabcontent)
    FrameLayout tabcontent;
    @BindView(android.R.id.tabhost)
    FragmentTabHost tabhost;
    private Class<?>[] fragmentArr = {OperationFragment.class, ModelFragment.class, SettingFragment.class};

    private int[] tabImageResArr = {
            R.drawable.selector_op
            , R.drawable.selector_model
            , R.drawable.selector_setting};

    private int[] tabTextResArr = {R.string.op, R.string.model, R.string.setting};
    private ArrayList<String> tabTagList;
    private int currentIndex;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private String address;
    private Button connect;
    private Handler handler = new Handler();
    private boolean DONE = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        initHost();
        ininData();
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    public BluetoothGattCharacteristic RWNCharacteristic;
    public BluetoothLeService mBluetoothLeService;
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                BLog.e("Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            mBluetoothLeService.connect(address);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private void ininData() {
        address = getIntent().getStringExtra("address");

    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                connect.setText(getResources().getString(R.string.connected));
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                connect.setText(getResources().getString(R.string.disconnected));
            } else if (BluetoothLeService.ACTION_GATT_CONNECTEING.equals(action)) {
                connect.setText("连接中");
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the
                // user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }

    };

    private void displayData(String stringExtra) {
        String resString = ProcessData.StringToByte(stringExtra);
        DONE = true;
        ToastUtil.show(resString);
        if (resString.contains("AC")) {
            failOrderList.offer(preOrder);
            write();
        } else {
            if (orderList.size() > 0) {
                write();
            }
        }
    }

    private void displayGattServices(List<BluetoothGattService> supportedGattServices) {
        for (BluetoothGattService service : supportedGattServices) {
            if (service.getUuid().toString().equals(SampleGattAttributes.BLUE_HOT_MEASUREMENT)) {
                for (BluetoothGattCharacteristic gattCharacteristic : service.getCharacteristics()) {
                    if (gattCharacteristic.getUuid().toString().equals(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG)) {
                        RWNCharacteristic = gattCharacteristic;
                    }
                }
            }
        }
        if (RWNCharacteristic == null) {
            ToastUtil.show("error");
        } else {
            mBluetoothLeService.setCharacteristicNotification(RWNCharacteristic, true);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    addOrder(Order.WRITE_OPEN);
                    addOrder(Order.WRITE_HEAT + "10");
                    addOrder(Order.WRITE_LIGHT + "03");
                }
            }, 500);
        }
    }

    private void initHost() {
        tabhost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        tabTagList = new ArrayList<>();

        for (int i = 0; i < fragmentArr.length; i++) {
            tabTagList.add(getResources().getString(tabTextResArr[i]));
            TabHost.TabSpec tabSpec = tabhost.newTabSpec(tabTagList.get(i));
            tabSpec.setIndicator(getTabItemView(i));
            tabhost.addTab(tabSpec, fragmentArr[i], null);
        }
        //隐藏默认分割线
        tabhost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);

        //同步currentIndex
        tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                currentIndex = tabTagList.indexOf(tabId);
            }
        });

        //TODO 测试实验代码
        connect = (Button) findViewById(R.id.connect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothLeService.connect(address);
            }
        });
//        getModeData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tabhost.setCurrentTab(currentIndex);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(address);
            System.out.println("Connect request result=" + result);
        }
        ToastUtil.show("OK");
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;

    }

    private View getTabItemView(int i) {
        View view = View.inflate(this, R.layout.tabspec_home, null);
        ImageView ivTab = (ImageView) view.findViewById(R.id.ivTab);
        ivTab.setImageResource(tabImageResArr[i]);
        TextView tvTab = (TextView) view.findViewById(R.id.tvTab);
        tvTab.setText(tabTextResArr[i]);
        return view;
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private String preOrder;

    private void write() {
        if (RWNCharacteristic == null) {
            ToastUtil.show("蓝牙连接异常");
            return;
        }
        if (DONE) {
            if (failOrderList.size() > 0) {
                preOrder = failOrderList.poll();
            } else if (orderList.size() > 0) {
                preOrder = orderList.poll();

            }
            if (TextUtils.isEmpty(preOrder)) {
                return;
            }
            BLog.e(preOrder);
            BluetoothGattCharacteristic writeGattCharacteristic = RWNCharacteristic;
            writeGattCharacteristic.setValue(ProcessData.StrToHexbyte(ProcessData.StringToNul(preOrder)));
            mBluetoothLeService.writeCharacteristic(writeGattCharacteristic);
            DONE = false;
        }

    }

    private void getModeData() {
        RetrofitUtil.getService()
                .getMode(App.tel)
                .enqueue(new DefaultCallback<ModelData>() {
                    @Override
                    public void onFinish(int status, ModelData body) {
                        if (status == DefaultCallback.SUCCESS) {
                            App.addData(body);
                        }
                    }
                });
    }

    private Queue<String> orderList = new LinkedList<>();
    private Queue<String> failOrderList = new LinkedList<>();

    public void addOrder(String order) {
        orderList.offer(order);
        BLog.e(orderList.toString());
        if (orderList.size() == 1) {
            write();
        }
    }
}
