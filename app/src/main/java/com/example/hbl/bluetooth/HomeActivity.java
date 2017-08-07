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
import com.example.hbl.bluetooth.network.RetrofitUtil;
import com.example.hbl.bluetooth.network.ToastUtil;
import com.example.hbl.bluetooth.newblue.BluetoothLeSecondeService;
import com.example.hbl.bluetooth.newblue.BluetoothLeService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private String address1, address2;
    private Button connect, connect2;
    private Handler handler = new Handler();
    private boolean DONE = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        initHost();
        ininData();
        if (!TextUtils.isEmpty(address1)) {
            Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        }
        if (!TextUtils.isEmpty(address2)) {
            Intent gattServiceIntent = new Intent(this, BluetoothLeSecondeService.class);
            bindService(gattServiceIntent, mServiceSecondConnection, BIND_AUTO_CREATE);
        }

    }

    public BluetoothGattCharacteristic RWNCharacteristic;
    public BluetoothGattCharacteristic RWNSECharacteristic;
    public BluetoothLeService mBluetoothLeService;
    public BluetoothLeSecondeService mBluetoothLeSecondService;
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
            mBluetoothLeService.connect(address1);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            DONE = true;
        }
    };
    private final ServiceConnection mServiceSecondConnection = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeSecondService = ((BluetoothLeSecondeService.LocalBinder) service).getService();
            if (!mBluetoothLeSecondService.initialize()) {
                BLog.e("Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up
            // initialization.

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeSecondService = null;
            DONE2 = true;
        }
    };

    private void ininData() {
        ArrayList<String> address = getIntent().getStringArrayListExtra("address");
        address1 = address.get(0);
        if (address.size() == 1) {
            address1 = address.get(0);
            App.ISDOUBLE = false;
        } else if (address.size() == 2) {
            address1 = address.get(0);
            address2 = address.get(1);
            App.ISDOUBLE = true;
        }
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                connect.setText(getResources().getString(R.string.connected));
                mConnected=2;
                if (mBluetoothLeSecondService != null) {
                    mBluetoothLeSecondService.connect(address2);
                }
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                connect.setText(getResources().getString(R.string.disconnected));
                DONE = true;
                canDo = true;
                mConnected=0;
            } else if (BluetoothLeService.ACTION_GATT_CONNECTEING.equals(action)) {
                connect.setText("连接中");
                mConnected=1;
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the
                // user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            } else if (BluetoothLeSecondeService.ACTION_GATT_CONNECTED.equals(action)) {
                connect2.setText(getResources().getString(R.string.connected));
                mConnected2=2;
            } else if (BluetoothLeSecondeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                connect2.setText(getResources().getString(R.string.disconnected));
                canDo2 = true;
                DONE2 = true;
                mConnected2=0;
            } else if (BluetoothLeSecondeService.ACTION_GATT_CONNECTEING.equals(action)) {
                connect2.setText("连接中");
                mConnected2=1;
            } else if (BluetoothLeSecondeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the
                // user interface.
                displayTGattServices(mBluetoothLeSecondService.getSupportedGattServices());
            } else if (BluetoothLeSecondeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayTData(intent.getStringExtra(BluetoothLeSecondeService.EXTRA_DATA));
            }
        }

    };
    private boolean canDo = true;

    private void displayData(String stringExtra) {
        String resString = ProcessData.StringToByte(stringExtra);
        DONE = true;
        BLog.e("display1: " + resString + " - > " + orderList.toString());
        if (resString.contains("AC")) {
            failOrderList.offer(preOrder);
            write();
        } else {
            if (orderList.size() > 0) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                write();
            } else {
                canDo = true;
            }
        }
    }

    boolean DONE2 = true;

    private void displayTData(String stringExtra) {
        String resString = ProcessData.StringToByte(stringExtra);
        DONE2 = true;
        BLog.e("display2: " + resString + " - > " + orderList2.toString());
        if (resString.contains("AC")) {
            failOrderList2.offer(preOrder2);
            write2();
        } else {
            if (orderList2.size() > 0) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                write2();
            } else {
                canDo2 = true;
            }
        }
    }

    private void displayTGattServices(List<BluetoothGattService> supportedGattServices) {
        for (BluetoothGattService service : supportedGattServices) {
            if (service.getUuid().toString().equals(SampleGattAttributes.BLUE_HOT_MEASUREMENT)) {
                for (BluetoothGattCharacteristic gattCharacteristic : service.getCharacteristics()) {
                    if (gattCharacteristic.getUuid().toString().equals(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG)) {
                        RWNSECharacteristic = gattCharacteristic;
                    }
                }
            }
        }
        if (RWNSECharacteristic == null) {
            ToastUtil.show("error");
        } else {
            mBluetoothLeSecondService.setCharacteristicNotification(RWNSECharacteristic, true);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    addOrder2(Order.WRITE_OPEN);
                    addOrder2(Order.WRITE_HEAT + "00");
//                    addOrder2(Order.WRITE_LIGHT + "03");
                }
            }, 500);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    addOrder2(Order.WRITE_LIGHT + "00");
                }
            }, 2500);
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
                    addOrder(Order.WRITE_HEAT + "00");
//                    addOrder(Order.WRITE_LIGHT + "03");
                }
            }, 500);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    addOrder(Order.WRITE_LIGHT + "00");
                }
            }, 2500);
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
                mBluetoothLeService.connect(address1);
            }
        });
        //TODO 测试实验代码
        connect2 = (Button) findViewById(R.id.connect2);
        connect2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothLeSecondService.connect(address2);
            }
        });
        getModeData();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    private int mConnected = 0;
    private int mConnected2 = 0;

    @Override
    protected void onResume() {
        super.onResume();
        tabhost.setCurrentTab(currentIndex);
        if (mBluetoothLeService != null && mConnected == 0) {
            final boolean result = mBluetoothLeService.connect(address1);
            System.out.println("Connect request result=" + result);
        }
        if (mBluetoothLeSecondService != null&& mConnected2==0) {
            final boolean result = mBluetoothLeSecondService.connect(address2);
            System.out.println("Connect request result=" + result);
        }
        ToastUtil.show("OK");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
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
        intentFilter.addAction(BluetoothLeSecondeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeSecondeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeSecondeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeSecondeService.ACTION_DATA_AVAILABLE);

        return intentFilter;
    }

    private String preOrder;
    private String preOrder2;

    private void write() {
        if (RWNCharacteristic == null) {
            ToastUtil.show("蓝牙连接异常,正在重新连接");
            mBluetoothLeService.connect(address1);
            canDo = true;
            return;
        }
        if (DONE) {
            if (failOrderList.size() > 0) {
                preOrder = failOrderList.poll();
            } else if (orderList.size() > 0) {
                preOrder = orderList.poll();

            }
            if (TextUtils.isEmpty(preOrder)) {
                canDo = true;
                return;
            }
            BLog.e(preOrder);
            canDo = false;
            BluetoothGattCharacteristic writeGattCharacteristic = RWNCharacteristic;
            writeGattCharacteristic.setValue(ProcessData.StrToHexbyte(ProcessData.StringToNul(preOrder)));
            mBluetoothLeService.writeCharacteristic(writeGattCharacteristic);
            DONE = false;
        }

    }

    private boolean canDo2 = true;

    private void write2() {
        if (RWNSECharacteristic == null) {
            ToastUtil.show("蓝牙连接异常,正在重新连接");
            mBluetoothLeSecondService.connect(address2);
            canDo2 = true;
            return;
        }
        if (DONE2) {
            if (failOrderList2.size() > 0) {
                preOrder2 = failOrderList2.poll();
            } else if (orderList2.size() > 0) {
                preOrder2 = orderList2.poll();

            }
            if (TextUtils.isEmpty(preOrder2)) {
                canDo2 = true;
                return;
            }
            canDo2 = false;
            BLog.e(preOrder2);
            BluetoothGattCharacteristic writeGattCharacteristic = RWNSECharacteristic;
            writeGattCharacteristic.setValue(ProcessData.StrToHexbyte(ProcessData.StringToNul(preOrder2)));
            mBluetoothLeSecondService.writeCharacteristic(writeGattCharacteristic);
            DONE2 = false;
        }

    }

    private void getModeData() {
        RetrofitUtil.getService()
                .getMode(App.tel)
                .enqueue(new Callback<List<ModelData>>() {
                    @Override
                    public void onResponse(Call<List<ModelData>> call, Response<List<ModelData>> response) {
                        if(response.body()!=null&&response.body().size()>=0){
                            for(ModelData data:response.body()){
                                App.addData(data);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ModelData>> call, Throwable t) {
                        ToastUtil.show("网络请求异常，请重试");
                    }
                });
    }

    private Queue<String> orderList = new LinkedList<>();
    private Queue<String> failOrderList = new LinkedList<>();

    private Queue<String> orderList2 = new LinkedList<>();
    private Queue<String> failOrderList2 = new LinkedList<>();

    public void addOrder(String order) {
        if (!orderList.contains(order)) {
            orderList.offer(order);
        }
        BLog.e("order1: " + orderList.toString());
        if (orderList.size() >= 1 && canDo) {
            write();
        }
    }

    public void addOrder2(String order) {
        if (TextUtils.isEmpty(address2)) return;
        if (!orderList2.contains(order)) {
            orderList2.offer(order);
        }
        BLog.e("order2: " + orderList2.toString());
        if (orderList2.size() >= 1 && canDo2) {
            write2();
        }
    }
}
