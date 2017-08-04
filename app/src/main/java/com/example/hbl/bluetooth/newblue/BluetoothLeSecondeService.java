package com.example.hbl.bluetooth.newblue;


import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.hbl.bluetooth.bluetooth_old.SampleGattAttributes;
import com.example.hbl.bluetooth.network.BLog;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class BluetoothLeSecondeService extends Service {
    public static final String ACTION_DATA_AVAILABLE = "com.example.bluetooth.leseconde.ACTION_DATA_AVAILABLE";
    public static final String ACTION_GATT_CONNECTED = "com.example.bluetooth.leseconde.ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_CONNECTEING = "com.example.bluetooth.leseconde.ACTION_GATT_CONNECTEING";
    public static final String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.leseconde.ACTION_GATT_DISCONNECTED";
    public static final String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.leseconde.ACTION_GATT_SERVICES_DISCOVERED";
    public static final String EXTRA_DATA = "com.example.bluetooth.leseconde.EXTRA_DATA";
    private static final int STATE_CONNECTED = 2;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_DISCONNECTED = 0;
    private static boolean ServicesHaveFine = false;
    private static final String TAG = BluetoothLeSecondeService.class.getSimpleName();
    public static final UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
    private static String mBluetoothDeviceAddress;
    private static BluetoothGatt mBluetoothGatt;
    private Timer checkTimer = null;
    private int checkTimercount = 0;
    private int checkTimercountre = -1;
    private BluetoothDevice device = null;
    private final IBinder mBinder = new LocalBinder();
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private int mConnectionState = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    checkTimerEnable(false);
                    checkTimerEnable(true);
                    return;
                case 2:
                    if (BluetoothLeSecondeService.mBluetoothGatt.getServices().size() > 0) {
                        checkTimercountre = -1;
                        broadcastUpdate(BluetoothLeSecondeService.ACTION_GATT_SERVICES_DISCOVERED);
                        BluetoothLeSecondeService.ServicesHaveFine = true;
                        checkTimerEnable(false);
                        return;
                    }
                    Log.e("TAG", "Unable to find the Services");
                    close();
                    connectNewDevice(BluetoothLeSecondeService.mBluetoothDeviceAddress);
                    return;
                case 3:
                    mConnectionState = 0;
                    BluetoothLeSecondeService.ServicesHaveFine = false;
                    Log.i(BluetoothLeSecondeService.TAG, "Disconnected from GATT server.");
                    broadcastUpdate(BluetoothLeSecondeService.ACTION_GATT_DISCONNECTED);
                    if (checkTimer != null && checkTimercount < 20) {
                        close();
                        connectNewDevice(BluetoothLeSecondeService.mBluetoothDeviceAddress);
                        return;
                    }
                    return;
                case 4:
                    if (checkTimer != null) {
                        checkTimerEnable(false);
                        return;
                    }
                    return;
                case 5:
                    broadcastUpdate(BluetoothLeSecondeService.ACTION_GATT_CONNECTED);
                    mConnectionState = 2;
                    checkTimercountre = checkTimercount + 50;
                    Log.i(BluetoothLeSecondeService.TAG, "Connected to GATT server.");
                    if (BluetoothLeSecondeService.mBluetoothGatt.discoverServices()) {
                        Log.i(BluetoothLeSecondeService.TAG, "mBluetoothGatt discoverServices true");
                        return;
                    } else {
                        Log.i(BluetoothLeSecondeService.TAG, "mBluetoothGatt discoverServices false");
                        return;
                    }
                default:
                    return;
            }
        }
    };
    private mGattCallback mbGattCallback = null;

    public class LocalBinder extends Binder {
        public BluetoothLeSecondeService getService() {
            return BluetoothLeSecondeService.this;
        }
    }

    private class mGattCallback extends BluetoothGattCallback {
        private mGattCallback() {
        }

        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == 2) {
                if (mConnectionState != 2) {
                    Message.obtain(mHandler, 5).sendToTarget();
                }
                Log.i(BluetoothLeSecondeService.TAG, "BluetoothLeService onConnectionStateChange STATE_CONNECTED");
            } else if (newState == 0) {
                Message.obtain(mHandler, 3).sendToTarget();
                Log.i(BluetoothLeSecondeService.TAG, "BluetoothLeService onConnectionStateChange STATE_DISCONNECTED");
            }
        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i(BluetoothLeSecondeService.TAG, "BluetoothLeService onServicesDiscovered status is " + status);
            if (status == 0) {
                if (mbGattCallback != null) {
                    Message.obtain(mHandler, 2).sendToTarget();
                    Log.i(BluetoothLeSecondeService.TAG, "BluetoothLeService onServicesDiscovered status is true");
                }
            } else if (status == 129) {
                mBluetoothAdapter.disable();
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothAdapter.enable();
            } else {
                Log.w(BluetoothLeSecondeService.TAG, "onServicesDiscovered received: " + status);
            }
        }

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            BLog.e("onCharacteristicRead" + characteristic.getValue().toString());
            if (status == 0) {
                broadcastUpdate(BluetoothLeSecondeService.ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            BLog.e("onCharacteristicChanged" + characteristic.getValue().toString());
            broadcastUpdate(BluetoothLeSecondeService.ACTION_DATA_AVAILABLE, characteristic);
        }

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        }
    }

    private void checkTimerEnable(boolean en) {
        if (en) {
            if (checkTimer == null) {
                checkTimer = new Timer();
                checkTimer.schedule(new TimerTask() {
                    public void run() {
                        checkTimercount = checkTimercount + 1;
                        if (checkTimercountre == checkTimercount) {
                            Message.obtain(mHandler, 2).sendToTarget();
                        }
                        if (checkTimercount == 200) {
                            checkTimercount = 0;
                            Message.obtain(mHandler, 4).sendToTarget();
                        }
                    }
                }, 10, 100);
            }
        } else if (checkTimer != null) {
            checkTimer.cancel();
            checkTimer = null;
            checkTimercountre = -1;
            checkTimercount = 0;
        }
    }

    private void broadcastUpdate(String action) {
        sendBroadcast(new Intent(action));
    }

    private void broadcastUpdate(String action, BluetoothGattCharacteristic characteristic) {
        Intent intent = new Intent(action);
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int format;
            if ((characteristic.getProperties() & 1) != 0) {
                format = 18;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = 17;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            int heartRate = characteristic.getIntValue(format, 1).intValue();
            Log.d(TAG, String.format("Received heart rate: %d", new Object[]{Integer.valueOf(heartRate)}));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
            byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                StringBuilder stringBuilder = new StringBuilder(data.length);
                int length = data.length;
                for (int i = 0; i < length; i++) {
                    stringBuilder.append(String.format("%02X ", new Object[]{Byte.valueOf(data[i])}));
                }
                intent.putExtra(EXTRA_DATA, new StringBuilder(String.valueOf(new String(data))).append("\n").append(stringBuilder.toString()).toString());
            }
        }
        sendBroadcast(intent);
    }

    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    public boolean initialize() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter != null) {
            return true;
        }
        Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
        return false;
    }

    public boolean connect(String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        } else if (mBluetoothDeviceAddress == null || !address.equals(mBluetoothDeviceAddress) || mBluetoothGatt == null) {
            disconnect();
            close();
            connectNewDevice(address);
            return true;
        } else {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            mBluetoothGatt.disconnect();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!mBluetoothGatt.connect()) {
                return false;
            }
            mConnectionState = 1;
            Message.obtain(mHandler, 1).sendToTarget();
            broadcastUpdate(ACTION_GATT_CONNECTEING);
            return true;
        }
    }

    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
        mConnectionState = 0;
        ServicesHaveFine = false;
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean connectNewDevice(String address) {
        if (device == null) {
            device = mBluetoothAdapter.getRemoteDevice(address);
        }
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        if (mbGattCallback == null) {
            mbGattCallback = new mGattCallback();
        }
        mBluetoothGatt = device.connectGatt(this, false, mbGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        broadcastUpdate(ACTION_GATT_CONNECTEING);
        Message.obtain(mHandler, 1).sendToTarget();
        mConnectionState = 1;
        return true;
    }

    public void close() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mbGattCallback = null;
            mBluetoothGatt = null;
            device = null;
        }
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
        } else {
            mBluetoothGatt.readCharacteristic(characteristic);
        }
    }

    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
        } else {
            mBluetoothGatt.writeCharacteristic(characteristic);
        }
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        int proper = characteristic.getProperties();
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(SampleGattAttributes.DESCRIPTOR_CHARACTERISTIC_CONFIG));
        if ((proper & 16) != 0) {
            mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
        if ((proper & 32) != 0) {
            mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) {
            return null;
        }
        return mBluetoothGatt.getServices();
    }

    public void restartdevice() {
        mBluetoothAdapter.disable();
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mBluetoothAdapter.enable();
    }
}