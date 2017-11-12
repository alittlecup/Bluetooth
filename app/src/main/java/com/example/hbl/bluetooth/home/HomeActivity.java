package com.example.hbl.bluetooth.home;

import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.hbl.bluetooth.App;
import com.example.hbl.bluetooth.BaseActivity;
import com.example.hbl.bluetooth.LocationManager;
import com.example.hbl.bluetooth.ModelData;
import com.example.hbl.bluetooth.MsgFragment;
import com.example.hbl.bluetooth.Order;
import com.example.hbl.bluetooth.ProcessData;
import com.example.hbl.bluetooth.R;
import com.example.hbl.bluetooth.WeatherControl;
import com.example.hbl.bluetooth.bluetooth_old.SampleGattAttributes;
import com.example.hbl.bluetooth.network.BLog;
import com.example.hbl.bluetooth.network.RetrofitUtil;
import com.example.hbl.bluetooth.network.ToastUtil;
import com.example.hbl.bluetooth.network.WeatherCallback;
import com.example.hbl.bluetooth.network.bean.CityWeather;
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

public class HomeActivity extends BaseActivity {

    @BindView(R.id.bottom_navigation_bar)
    BottomNavigationBar bottomNavigationBar;


    private String address1, address2;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 3) {
                if (!TextUtils.isEmpty(address1)) {
                    mHomeViewModel.getmUpTextClick().setValue(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (v instanceof TextView) {
                                if (((TextView) v).getText().toString().contains("断开")) {
                                    threadhandler.sendEmptyMessage(1);
                                }
                            }
                        }
                    });
                } else {
                    mHomeViewModel.getmUpText().setValue("当前不可用");
                    mHomeViewModel.getmUptextState().setValue(false);
                }
                if (!TextUtils.isEmpty(address2)) {
                    mHomeViewModel.getmDownTextClick().setValue(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (v instanceof TextView) {
                                if (((TextView) v).getText().toString().contains("断开")) {
                                    threadhandler.sendEmptyMessage(2);
                                }
                            }
                        }
                    });
                } else {
                    mHomeViewModel.getmDownText().setValue("当前不可用");
                    mHomeViewModel.getmDowntextState().setValue(false);

                }
            } else if (msg.what == 1) {
                mHomeViewModel.sendOrderUp(Order.READ_ENERGY);
                handler.sendEmptyMessageDelayed(1, 60 * 1000);
            } else if (msg.what == 2) {
                mHomeViewModel.sendOrderDown(Order.READ_ENERGY);
                handler.sendEmptyMessageDelayed(2, 60 * 1000);
            }
        }
    };
    private boolean DONE = true;
    private HomeViewModel mHomeViewModel;
    private WeatherControl control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        initHost();
        initViewModel();
        LocationManager.getInstance(getApplication()).addObserver(location -> {
            System.out.println();
            RetrofitUtil.getService(RetrofitUtil.weatherClient)
                    .getweather(location.getLatitude() + ":" + location.getLongitude())
                    .enqueue(new WeatherCallback() {
                        @Override
                        public void onSucess(CityWeather cityWeather) {
                            String temperature = cityWeather.results.get(0).now.temperature;
                            control.changeHeat(Integer.valueOf(temperature));
                        }

                        @Override
                        public void onFailure(String msg) {
                        }
                    });

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getBooleanValue(mHomeViewModel.getmAutoHeat().getValue())) {
            LocationManager.getInstance(getApplication()).startLocation();
        }
    }
    private boolean getBooleanValue(Boolean value){
        return value==null?false:value;
    }
    public void changeTemperture(String temperature) {
        control.changeHeat(Integer.valueOf(temperature));
    }

    private void initViewModel() {
        mHomeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mHomeViewModel.getmOrderUp().observe(this, str -> addOrder(str));
        mHomeViewModel.getmOrderDown().observe(this, str -> addOrder2(str));
        control = WeatherControl.getInstance(mHomeViewModel);
        mHomeViewModel.getmAutoHeat().observe(this,b->{
            if(b){
                LocationManager.getInstance(getApplication()).startLocation();
            }
        });
        mHomeViewModel.getmIsPainEnable().setValue(false);
        mHomeViewModel.getmIsTeeEnable().setValue(false);
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
            if (TextUtils.isEmpty(address1)) {
                mBluetoothLeSecondService.connect(address2);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeSecondService = null;
            DONE2 = true;
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ininData(intent);
    }

    private void ininData(Intent intent) {
        String hotup = intent.getStringExtra("hotup");
        String hotdw = intent.getStringExtra("hotdw");

        if (!TextUtils.isEmpty(hotup)) {
            address1 = hotup;
        }
        if (!TextUtils.isEmpty(hotdw)) {
            address2 = hotdw;
        }
        if (!TextUtils.isEmpty(address1)) {
            mHomeViewModel.getmIsTeeEnable().setValue(true);
            Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        } else {
            mHomeViewModel.getmIsTeeEnable().setValue(false);
        }
        if (!TextUtils.isEmpty(address2)) {
            mHomeViewModel.getmIsPainEnable().setValue(true);
            Intent gattServiceIntent = new Intent(this, BluetoothLeSecondeService.class);
            bindService(gattServiceIntent, mServiceSecondConnection, BIND_AUTO_CREATE);
        } else {
            mHomeViewModel.getmIsPainEnable().setValue(false);
        }
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mHomeViewModel.getmUpText().setValue(getResources().getString(R.string.connected));
                mHomeViewModel.getmUptextState().setValue(true);
                mConnected = 2;
                if (mBluetoothLeSecondService != null && mConnected2 == 0) {
                    mBluetoothLeSecondService.connect(address2);
                }
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mHomeViewModel.getmUpText().setValue(getResources().getString(R.string.disconnected));
                mHomeViewModel.getmUptextState().setValue(false);
                mHomeViewModel.getmUptextVisible().setValue(true);
                mHomeViewModel.getmUpImgVisible().setValue(false);
                DONE = true;
                canDo = true;
                mConnected = 0;
                handler.removeMessages(1);
            } else if (BluetoothLeService.ACTION_GATT_CONNECTEING.equals(action)) {
                mHomeViewModel.getmUpText().setValue("正在连接...");
                mHomeViewModel.getmUptextState().setValue(false);
                mConnected = 1;
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            } else if (BluetoothLeSecondeService.ACTION_GATT_CONNECTED.equals(action)) {
                mHomeViewModel.getmDownText().setValue(getResources().getString(R.string.connected));
                mHomeViewModel.getmDowntextState().setValue(true);
                mConnected2 = 2;
            } else if (BluetoothLeSecondeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mHomeViewModel.getmDownText().setValue(getResources().getString(R.string.disconnected));
                mHomeViewModel.getmDowntextState().setValue(false);
                mHomeViewModel.getmDownImgVisible().setValue(false);
                mHomeViewModel.getmDowntextVisible().setValue(true);
                canDo2 = true;
                DONE2 = true;
                mConnected2 = 0;
                handler.removeMessages(2);
            } else if (BluetoothLeSecondeService.ACTION_GATT_CONNECTEING.equals(action)) {
                mHomeViewModel.getmDownText().setValue("正在连接...");
                mHomeViewModel.getmDowntextState().setValue(false);
                mConnected2 = 1;
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
            if (resString.contains("AA")) {
                char c = resString.charAt(resString.length() - 1);
                Integer integer = Integer.valueOf(c + "");
                mHomeViewModel.getmUptextVisible().setValue(false);
                mHomeViewModel.getmUpImgVisible().setValue(true);
                switch (integer) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        mHomeViewModel.getmUpImg().setValue(R.drawable.opear_energy_low);
                        break;
                    default:
                        mHomeViewModel.getmUpImg().setValue(R.drawable.opear_energy);
                }
            }
            //关机的控制，首先发送关机指令，之后在回调中判断状态
            //如果有两个设备，则需要判断当前的关机状态
            close();
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

    boolean hasOtherClose = false;
    boolean DONE2 = true;

    private void displayTData(String stringExtra) {
        String resString = ProcessData.StringToByte(stringExtra);
        DONE2 = true;
        BLog.e("display2: " + resString + " - > " + orderList2.toString());
        if (resString.contains("AC")) {
            failOrderList2.offer(preOrder2);
            write2();
        } else {
            if (resString.contains("AA")) {
                char c = resString.charAt(resString.length() - 1);
                Integer integer = Integer.valueOf(c + "");

                mHomeViewModel.getmDowntextVisible().setValue(false);
                mHomeViewModel.getmDownImgVisible().setValue(true);
                switch (integer) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        mHomeViewModel.getmDownImg().setValue(R.drawable.opear_energy_low);
                        break;
                    default:
                        mHomeViewModel.getmDownImg().setValue(R.drawable.opear_energy);
                }
            }
            close();
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

    private void close() {
        if (BackClose) {
            boolean mTee = getBooleanValue(mHomeViewModel.getmIsTeeEnable().getValue()) ;
            boolean mPan =getBooleanValue(mHomeViewModel.getmIsPainEnable().getValue());
            if (mTee && mPan) {
                if (hasOtherClose) {
                    dismissDialog();
                    finish();
                } else {
                    hasOtherClose = true;
                }
            } else {
                dismissDialog();
                finish();
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

        }
    }

    private List<Fragment> fragmentList = new ArrayList<>();

    private void getFragments() {

        OperationFragment operationFragment = new OperationFragment();
        ModelFragment modelFragment = new ModelFragment();

        MsgFragment msgFragment = new MsgFragment();

        SettingFragment settingFragment = new SettingFragment();
        WebViewFragment webViewFragment=new WebViewFragment();
        fragmentList.add(operationFragment);

//        fragmentList.add(modelFragment);
        fragmentList.add(msgFragment);
        fragmentList.add(webViewFragment);
        fragmentList.add(settingFragment);

    }

    private void initHost() {
        getFragments();
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.opeartor_on, "操作").setInactiveIconResource(R.drawable.operator_un))
                .addItem(new BottomNavigationItem(R.drawable.module_on, "模式").setInactiveIconResource(R.drawable.module_un))
                .addItem(new BottomNavigationItem(R.drawable.shop, "商城").setInactiveIconResource(R.drawable.shop_un))
                .addItem(new BottomNavigationItem(R.drawable.mine_on, "我的").setInactiveIconResource(R.drawable.mine_un))
                .setFirstSelectedPosition(0)
                .initialise();
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int i) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                if (fragmentList.get(i).isAdded()) {
                    fragmentTransaction.show(fragmentList.get(i));
                } else {
                    fragmentTransaction.add(R.id.tabs, fragmentList.get(i));
                }
                fragmentTransaction.commitAllowingStateLoss();
            }

            @Override
            public void onTabUnselected(int i) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.hide(fragmentList.get(i));
                fragmentTransaction.commitAllowingStateLoss();
            }

            @Override
            public void onTabReselected(int i) {
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.tabs, fragmentList.get(0)).commitAllowingStateLoss();
        getModeData();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        handler.sendEmptyMessage(3);
    }

    public static HandlerThread thread;

    static {
        thread = new HandlerThread("Pool");
        thread.start();

    }

    private Handler threadhandler = new Handler(thread.getLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                mBluetoothLeService.connect(address1);
            } else if (msg.what == 2) {
                mBluetoothLeSecondService.connect(address2);
            }
        }
    };
    private int mConnected = 0;
    private int mConnected2 = 0;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
        if (!TextUtils.isEmpty(address1)) {
            unbindService(mServiceConnection);
            mBluetoothLeService = null;
        }
        if (!TextUtils.isEmpty(address2)) {
            unbindService(mServiceSecondConnection);
            mBluetoothLeSecondService = null;
        }

    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTEING);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeSecondeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeSecondeService.ACTION_GATT_CONNECTEING);
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
            if (mBluetoothLeService == null) return;
            mBluetoothLeService.writeCharacteristic(writeGattCharacteristic);
            DONE = false;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!DONE) {
                        DONE = true;
                        canDo = true;
                        orderList.clear();
                        failOrderList.clear();
                    }
                }
            }, 2000);
        }

    }

    private boolean canDo2 = true;

    private void write2() {
        if (RWNSECharacteristic == null) {
            ToastUtil.show("蓝牙连接异常,正在重新连接");
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
            if (mBluetoothLeSecondService == null) return;
            mBluetoothLeSecondService.writeCharacteristic(writeGattCharacteristic);
            DONE2 = false;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!DONE2) {
                        DONE2 = true;
                        canDo2 = true;
                        orderList2.clear();
                        failOrderList2.clear();
                    }
                }
            }, 3000);

        }

    }

    private void getModeData() {
        RetrofitUtil.getService()
                .getMode(App.tel)
                .enqueue(new Callback<List<ModelData>>() {
                    @Override
                    public void onResponse(Call<List<ModelData>> call, Response<List<ModelData>> response) {
                        if (response.body() != null && response.body().size() >= 0) {
                            for (ModelData data : response.body()) {
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

    private void addOrder(String order) {
        if (TextUtils.isEmpty(address1)) return;
        if (!orderList.contains(order)) {
            orderList.offer(order);
        }
        BLog.e("order1: " + orderList.toString());
        if (orderList.size() >= 1 && canDo) {
            write();
        }
    }

    private void addOrder2(String order) {
        if (TextUtils.isEmpty(address2)) return;
        if (!orderList2.contains(order)) {
            orderList2.offer(order);
        }
        BLog.e("order2: " + orderList2.toString());
        if (orderList2.size() >= 1 && canDo2) {
            write2();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage("确认退出吗？").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BackClose = true;
                        showDialog("");
                        setBelClose();
                        if (TextUtils.isEmpty(address1) && TextUtils.isEmpty(address2)) {
                            dismissDialog();
                            finish();
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private boolean BackClose = false;


    public void connect(int i) {
        threadhandler.sendEmptyMessage(i);
    }

    private void setBelClose() {
        mHomeViewModel.sendOrderUp(Order.WRITE_CLOSE);
        mHomeViewModel.sendOrderDown(Order.WRITE_CLOSE);
    }


}
