package com.example.hbl.bluetooth.newblue;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.hbl.bluetooth.App;

import java.util.Iterator;
import java.util.List;

/**
 * ??????????
 *
 * @author wangdandan
 */
public class BluetoothController {
    private String deviceAddress;
    private String deviceName;

    private BluetoothAdapter bleAdapter;
    private Handler serviceHandler;// ????????

    static BluetoothGatt bleGatt;// ????
    static BluetoothGattCharacteristic bleGattCharacteristic;

    /**
     * ????????
     */
    private static BluetoothController instance = null;

    private BluetoothController() {
    }

    public static BluetoothController getInstance() {
        if (instance == null)
            instance = new BluetoothController();
        return instance;
    }

    /**
     * ??????????
     *
     * @return
     */
    public boolean initBLE() {

        if (!App.app.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) App.app
                .getSystemService(Context.BLUETOOTH_SERVICE);
        bleAdapter = bluetoothManager.getAdapter();

        if (bleAdapter == null)
            return false;
        else
            return true;
    }

    /**
     * @return
     */
    public void setServiceHandler(Handler handler) {

        serviceHandler = handler;
    }

    /**
     *
     */
    BluetoothAdapter.LeScanCallback bleScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int arg1, byte[] arg2) {

            String name = device.getName();
            if (name == null)
                return;
            if (BluetoothController.this.serviceHandler != null
                    && !name.isEmpty()) {
                Message msg = new Message();
                msg.what = ConstantUtils.WM_UPDATE_BLE_LIST;
                msg.obj = device;
                BluetoothController.this.serviceHandler.sendMessage(msg);
            }
        }
    };

    /**
     *
     */
    public void startScanBLE() {
        bleAdapter.startLeScan(bleScanCallback);
        if (serviceHandler != null)
            serviceHandler.sendEmptyMessageDelayed(
                    ConstantUtils.WM_STOP_SCAN_BLE, 5000);
    }

    /**
     *
     */
    public void stopScanBLE() {
        bleAdapter.stopLeScan(bleScanCallback);
    }

    /**
     * @return
     */
    public boolean isBleOpen() {
        return bleAdapter.isEnabled();
    }

    /**
     * @param device
     */
    public void connect(EntityDevice device) {
        deviceAddress = device.getAddress();
        deviceName = device.getName();
        BluetoothDevice localBluetoothDevice = bleAdapter
                .getRemoteDevice(device.getAddress());
        if (bleGatt != null) {
            bleGatt.disconnect();
            bleGatt.close();
            bleGatt = null;
        }
        bleGatt = localBluetoothDevice.connectGatt(App.app, false,
                bleGattCallback);
    }

    public BluetoothGattCallback bleGattCallback = new BluetoothGattCallback() {

        /**
         *
         */
        public void onCharacteristicChanged(
                BluetoothGatt paramAnonymousBluetoothGatt,
                BluetoothGattCharacteristic paramAnonymousBluetoothGattCharacteristic) {

            byte[] arrayOfByte = paramAnonymousBluetoothGattCharacteristic
                    .getValue();
            if (BluetoothController.this.serviceHandler != null) {
                Message msg = new Message();
                msg.what = ConstantUtils.WM_RECEIVE_MSG_FROM_BLE;
                //
                msg.obj = ConvertUtils.getInstance().bytesToHexString(
                        arrayOfByte);
                BluetoothController.this.serviceHandler.sendMessage(msg);
            }

            Log.i("TEST",
                    ConvertUtils.getInstance().bytesToHexString(arrayOfByte));
        }

        public void onCharacteristicRead(
                BluetoothGatt paramAnonymousBluetoothGatt,
                BluetoothGattCharacteristic paramAnonymousBluetoothGattCharacteristic,
                int paramAnonymousInt) {
        }

        public void onCharacteristicWrite(
                BluetoothGatt paramAnonymousBluetoothGatt,
                BluetoothGattCharacteristic paramAnonymousBluetoothGattCharacteristic,
                int paramAnonymousInt) {
        }

        /**
         *
         */
        public void onConnectionStateChange(
                BluetoothGatt paramAnonymousBluetoothGatt, int oldStatus,
                int newStatus) {
            if (newStatus == 2) {
                Message msg = new Message();
                msg.what = ConstantUtils.WM_BLE_CONNECTED_STATE_CHANGE;
                Bundle bundle = new Bundle();
                bundle.putString("address", deviceAddress);
                bundle.putString("name", deviceName);
                msg.obj = bundle;
                serviceHandler.sendMessage(msg);
                paramAnonymousBluetoothGatt.discoverServices();
                return;
            }
            if (newStatus == 0) {
                serviceHandler.sendEmptyMessage(ConstantUtils.WM_STOP_CONNECT);
                return;
            }
            paramAnonymousBluetoothGatt.disconnect();
            paramAnonymousBluetoothGatt.close();
            return;
        }

        public void onDescriptorRead(BluetoothGatt paramAnonymousBluetoothGatt,
                                     BluetoothGattDescriptor paramAnonymousBluetoothGattDescriptor,
                                     int paramAnonymousInt) {
        }

        public void onDescriptorWrite(
                BluetoothGatt paramAnonymousBluetoothGatt,
                BluetoothGattDescriptor paramAnonymousBluetoothGattDescriptor,
                int paramAnonymousInt) {
        }

        public void onReadRemoteRssi(BluetoothGatt paramAnonymousBluetoothGatt,
                                     int paramAnonymousInt1, int paramAnonymousInt2) {
        }

        public void onReliableWriteCompleted(
                BluetoothGatt paramAnonymousBluetoothGatt, int paramAnonymousInt) {
        }

        public void onServicesDiscovered(
                BluetoothGatt paramAnonymousBluetoothGatt, int paramAnonymousInt) {
            BluetoothController.this.findService(paramAnonymousBluetoothGatt.getServices());
        }

    };

    /**
     * @param byteArray
     * @return
     */
    public boolean write(byte byteArray[]) {
        if (bleGattCharacteristic == null)
            return false;
        if (bleGatt == null)
            return false;
        bleGattCharacteristic.setValue(byteArray);
        return bleGatt.writeCharacteristic(bleGattCharacteristic);
    }

    /**
     * @param
     * @return
     */
    public boolean write(String str) {
        if (bleGattCharacteristic == null)
            return false;
        if (bleGatt == null)
            return false;
        bleGattCharacteristic.setValue(str);
        return bleGatt.writeCharacteristic(bleGattCharacteristic);
    }

    /**
     * @param paramList
     */
    public void findService(List<BluetoothGattService> paramList) {

        Iterator localIterator1 = paramList.iterator();
        while (localIterator1.hasNext()) {
            BluetoothGattService localBluetoothGattService = (BluetoothGattService) localIterator1
                    .next();
            if (localBluetoothGattService.getUuid().toString()
                    .equalsIgnoreCase(ConstantUtils.UUID_SERVER)) {
                List localList = localBluetoothGattService.getCharacteristics();
                Iterator localIterator2 = localList.iterator();
                while (localIterator2.hasNext()) {
                    BluetoothGattCharacteristic localBluetoothGattCharacteristic = (BluetoothGattCharacteristic) localIterator2
                            .next();
                    if (localBluetoothGattCharacteristic.getUuid().toString()
                            .equalsIgnoreCase(ConstantUtils.UUID_NOTIFY)) {
                        bleGattCharacteristic = localBluetoothGattCharacteristic;
                        break;
                    }
                }
                break;
            }

        }

        bleGatt.setCharacteristicNotification(bleGattCharacteristic, true);
    }


}
