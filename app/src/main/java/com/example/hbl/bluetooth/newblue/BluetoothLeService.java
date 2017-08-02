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

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class BluetoothLeService extends Service {
    public static final String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public static final String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_CONNECTEING = "com.example.bluetooth.le.ACTION_GATT_CONNECTEING";
    public static final String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public static final String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public static final String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
    private static final int STATE_CONNECTED = 2;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_DISCONNECTED = 0;
    private static boolean ServicesHaveFine = false;
    private static final String TAG = BluetoothLeService.class.getSimpleName();
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
                    BluetoothLeService.this.checkTimerEnable(false);
                    BluetoothLeService.this.checkTimerEnable(true);
                    return;
                case 2:
                    if (BluetoothLeService.mBluetoothGatt.getServices().size() > 0) {
                        BluetoothLeService.this.checkTimercountre = -1;
                        BluetoothLeService.this.broadcastUpdate(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
                        BluetoothLeService.ServicesHaveFine = true;
                        BluetoothLeService.this.checkTimerEnable(false);
                        return;
                    }
                    Log.e("TAG", "Unable to find the Services");
                    BluetoothLeService.this.close();
                    BluetoothLeService.this.connectNewDevice(BluetoothLeService.mBluetoothDeviceAddress);
                    return;
                case 3:
                    BluetoothLeService.this.mConnectionState = 0;
                    BluetoothLeService.ServicesHaveFine = false;
                    Log.i(BluetoothLeService.TAG, "Disconnected from GATT server.");
                    BluetoothLeService.this.broadcastUpdate(BluetoothLeService.ACTION_GATT_DISCONNECTED);
                    if (BluetoothLeService.this.checkTimer != null && BluetoothLeService.this.checkTimercount < 20) {
                        BluetoothLeService.this.close();
                        BluetoothLeService.this.connectNewDevice(BluetoothLeService.mBluetoothDeviceAddress);
                        return;
                    }
                    return;
                case 4:
                    if (BluetoothLeService.this.checkTimer != null) {
                        BluetoothLeService.this.checkTimerEnable(false);
                        return;
                    }
                    return;
                case 5:
                    BluetoothLeService.this.broadcastUpdate(BluetoothLeService.ACTION_GATT_CONNECTED);
                    BluetoothLeService.this.mConnectionState = 2;
                    BluetoothLeService.this.checkTimercountre = BluetoothLeService.this.checkTimercount + 50;
                    Log.i(BluetoothLeService.TAG, "Connected to GATT server.");
                    if (BluetoothLeService.mBluetoothGatt.discoverServices()) {
                        Log.i(BluetoothLeService.TAG, "mBluetoothGatt discoverServices true");
                        return;
                    } else {
                        Log.i(BluetoothLeService.TAG, "mBluetoothGatt discoverServices false");
                        return;
                    }
                default:
                    return;
            }
        }
    };
    private mGattCallback mbGattCallback = null;

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    private class mGattCallback extends BluetoothGattCallback {
        private mGattCallback() {
        }

        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == 2) {
                if (BluetoothLeService.this.mConnectionState != 2) {
                    Message.obtain(BluetoothLeService.this.mHandler, 5).sendToTarget();
                }
                Log.i(BluetoothLeService.TAG, "BluetoothLeService onConnectionStateChange STATE_CONNECTED");
            } else if (newState == 0) {
                Message.obtain(BluetoothLeService.this.mHandler, 3).sendToTarget();
                Log.i(BluetoothLeService.TAG, "BluetoothLeService onConnectionStateChange STATE_DISCONNECTED");
            }
        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i(BluetoothLeService.TAG, "BluetoothLeService onServicesDiscovered status is " + status);
            if (status == 0) {
                if (BluetoothLeService.this.mbGattCallback != null) {
                    Message.obtain(BluetoothLeService.this.mHandler, 2).sendToTarget();
                    Log.i(BluetoothLeService.TAG, "BluetoothLeService onServicesDiscovered status is true");
                }
            } else if (status == 129) {
                BluetoothLeService.this.mBluetoothAdapter.disable();
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                BluetoothLeService.this.mBluetoothAdapter.enable();
            } else {
                Log.w(BluetoothLeService.TAG, "onServicesDiscovered received: " + status);
            }
        }

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == 0) {
                BluetoothLeService.this.broadcastUpdate(BluetoothLeService.ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            BluetoothLeService.this.broadcastUpdate(BluetoothLeService.ACTION_DATA_AVAILABLE, characteristic);
        }

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        }
    }

    private void checkTimerEnable(boolean en) {
        if (en) {
            if (this.checkTimer == null) {
                this.checkTimer = new Timer();
                this.checkTimer.schedule(new TimerTask() {
                    public void run() {
                        BluetoothLeService bluetoothLeService = BluetoothLeService.this;
                        bluetoothLeService.checkTimercount = bluetoothLeService.checkTimercount + 1;
                        if (BluetoothLeService.this.checkTimercountre == BluetoothLeService.this.checkTimercount) {
                            Message.obtain(BluetoothLeService.this.mHandler, 2).sendToTarget();
                        }
                        if (BluetoothLeService.this.checkTimercount == 200) {
                            BluetoothLeService.this.checkTimercount = 0;
                            Message.obtain(BluetoothLeService.this.mHandler, 4).sendToTarget();
                        }
                    }
                }, 10, 100);
            }
        } else if (this.checkTimer != null) {
            this.checkTimer.cancel();
            this.checkTimer = null;
            this.checkTimercountre = -1;
            this.checkTimercount = 0;
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
        return this.mBinder;
    }

    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    public boolean initialize() {
        if (this.mBluetoothManager == null) {
            this.mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
            if (this.mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
        if (this.mBluetoothAdapter != null) {
            return true;
        }
        Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
        return false;
    }

    public boolean connect(String address) {
        if (this.mBluetoothAdapter == null || address == null) {
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
            this.mConnectionState = 1;
            Message.obtain(this.mHandler, 1).sendToTarget();
            broadcastUpdate(ACTION_GATT_CONNECTEING);
            return true;
        }
    }

    public void disconnect() {
        if (this.mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
        this.mConnectionState = 0;
        ServicesHaveFine = false;
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean connectNewDevice(String address) {
        if (this.device == null) {
            this.device = this.mBluetoothAdapter.getRemoteDevice(address);
        }
        if (this.device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        if (this.mbGattCallback == null) {
            this.mbGattCallback = new mGattCallback();
        }
        mBluetoothGatt = this.device.connectGatt(this, false, this.mbGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        broadcastUpdate(ACTION_GATT_CONNECTEING);
        Message.obtain(this.mHandler, 1).sendToTarget();
        this.mConnectionState = 1;
        return true;
    }

    public void close() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            this.mbGattCallback = null;
            mBluetoothGatt = null;
            this.device = null;
        }
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (this.mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
        } else {
            mBluetoothGatt.readCharacteristic(characteristic);
        }
    }

    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (this.mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
        } else {
            mBluetoothGatt.writeCharacteristic(characteristic);
        }
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (this.mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        int proper = characteristic.getProperties();
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
        if ((proper & 16) != 0) {
            mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
        if ((proper & 32) != 0) {
            mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
            descriptor = characteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
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
        this.mBluetoothAdapter.disable();
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.mBluetoothAdapter.enable();
    }
}