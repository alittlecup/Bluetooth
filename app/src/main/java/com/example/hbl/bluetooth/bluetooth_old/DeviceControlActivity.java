/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.hbl.bluetooth.bluetooth_old;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.example.hbl.bluetooth.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * For a given BLE device, this Activity provides the user interface to connect,
 * display data, and display GATT services and characteristics supported by the
 * device. The Activity communicates with {@code BluetoothLeService}, which in
 * turn interacts with the Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity {
	private final static String TAG = DeviceControlActivity.class
			.getSimpleName();

	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	private TextView mConnectionState;
	private TextView mDataField;
	private String mDeviceName;
	private String mDeviceAddress;
	private ExpandableListView mGattServicesList;
	private BluetoothLeService mBluetoothLeService;
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	private boolean mConnected = false;
	private BluetoothGattCharacteristic mNotifyCharacteristic;

	private final String LIST_NAME = "NAME";
	private final String LIST_UUID = "UUID";

	ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
	ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();

	// Code to manage Service lifecycle.
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
					.getService();
			if (!mBluetoothLeService.initialize()) {
				Log.i(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			// Automatically connects to the device upon successful start-up
			// initialization.
			mBluetoothLeService.connect(mDeviceAddress);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};

	// Handles various events fired by the Service.
	// ACTION_GATT_CONNECTED: connected to a GATT server.
	// ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
	// ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
	// ACTION_DATA_AVAILABLE: received data from the device. This can be a
	// result of read
	// or notification operations.
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				mConnected = true;
				updateConnectionState(R.string.connected);
				invalidateOptionsMenu();
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				mConnected = false;
				updateConnectionState(R.string.disconnected);
				invalidateOptionsMenu();
				clearUI();
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				// Show all the supported services and characteristics on the
				// user interface.
				displayGattServices(mBluetoothLeService.getSupportedGattServices());
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
			}
		}
	};

	// If a given GATT characteristic is selected, check for supported features.
	// This sample
	// demonstrates 'Read' and 'Notify' features. See
	// http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for
	// the complete
	// list of supported characteristic features.
	private final ExpandableListView.OnChildClickListener servicesListClickListner = new ExpandableListView.OnChildClickListener() {
		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			if (mGattCharacteristics != null) {
				Log.i("linqiang", "onChildClick: 点击事件");
			final BluetoothGattCharacteristic characteristic = mGattCharacteristics
						.get(groupPosition).get(childPosition);
					final int charaProp = characteristic.getProperties();
				if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
					// If there is an active notification on a characteristic,
					// clear
					// it first so it doesn't update the data field on the user
					// interface.
					if (mNotifyCharacteristic != null) {
						mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);
						mNotifyCharacteristic = null;
					}
					mBluetoothLeService.readCharacteristic(characteristic);
				}
				if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
					mNotifyCharacteristic = characteristic;
					Log.i("linqiang", "通知UUID："+characteristic.getUuid());
					mBluetoothLeService.setCharacteristicNotification(characteristic, true);
				}

				return true;
			}
			return false;
		}
	};

	private void clearUI() {
		mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
		mDataField.setText(R.string.no_data);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gatt_services_characteristics);

		final Intent intent = getIntent();
		mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
		mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

		// Sets up UI references.
		((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
		mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
		mGattServicesList.setOnChildClickListener(servicesListClickListner);
		mConnectionState = (TextView) findViewById(R.id.connection_state);
		mDataField = (TextView) findViewById(R.id.data_value);

		getActionBar().setTitle(mDeviceName);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		if (mBluetoothLeService != null) {
			final boolean result = mBluetoothLeService.connect(mDeviceAddress);
			Log.i(TAG, "Connect request result=" + result);
		}
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.gatt_services, menu);
		if (mConnected) {
			menu.findItem(R.id.menu_connect).setVisible(false);
			menu.findItem(R.id.menu_disconnect).setVisible(true);
		} else {
			menu.findItem(R.id.menu_connect).setVisible(true);
			menu.findItem(R.id.menu_disconnect).setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_connect:
			mBluetoothLeService.connect(mDeviceAddress);
			return true;
		case R.id.menu_disconnect:
			mBluetoothLeService.disconnect();
			return true;
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateConnectionState(final int resourceId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mConnectionState.setText(resourceId);
			}
		});
	}

	private void displayData(String data) {
		if (data != null) {
			mDataField.setText(data);
			Log.i("linqiang", "displayData:读取到的数据 "+data);
		}
	}

	// Demonstrates how to iterate through the supported GATT
	// Services/Characteristics.
	// In this sample, we populate the data structure that is bound to the
	// ExpandableListView
	// on the UI.
	ArrayList<GattServicesInfo> serviceList = new ArrayList<GattServicesInfo>();

	private void displayGattServices(List<BluetoothGattService> gattServices) {
		if (gattServices == null)
			return;
		String uuid = null;
		String unknownServiceString = getResources().getString(R.string.unknown_service);
		String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
		mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

		// Loops through available GATT Services.
		GattServicesInfo serviceInfo = null;
		CharactInfo charactInfo = null;
		for (BluetoothGattService gattService : gattServices) {

			uuid = gattService.getUuid().toString();
			Log.i("log", "uuid:" + uuid);
			serviceInfo = new GattServicesInfo();
			serviceInfo.gattService = gattService;
			serviceInfo.serviceName = SampleGattAttributes.lookup(uuid,
					unknownServiceString);
			serviceInfo.uuid = uuid;
			serviceInfo.type = gattService.getType();
			serviceList.add(serviceInfo);

			List<BluetoothGattCharacteristic> gattCharacteristics = gattService
					.getCharacteristics();

			// Loops through available Characteristics.
			for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
				uuid = gattCharacteristic.getUuid().toString();
				charactInfo = new CharactInfo();
				charactInfo.gattCharacteristic = gattCharacteristic;
				charactInfo.charactName = SampleGattAttributes.lookup(uuid,
						unknownCharaString);
				charactInfo.uuid = uuid;
				charactInfo.propertie = gattCharacteristic.getProperties();
				Log.i("log", "uuid child:" + uuid);
				Log.i("log", "charaProp:" + gattCharacteristic.getProperties());
				serviceInfo.charactInfoList.add(charactInfo);
			}
		}

		mGattServicesList.setAdapter(new GattServicesAdapter());
	}

	class GattServicesInfo {
		public String serviceName;
		public String uuid;
		public int type;
		public ArrayList<CharactInfo> charactInfoList = new ArrayList<CharactInfo>();
		public BluetoothGattService gattService;
	}

	class CharactInfo {
		public String charactName;
		public String uuid;
		public int propertie;

		public BluetoothGattCharacteristic gattCharacteristic;
	}

	class GattServicesAdapter extends BaseExpandableListAdapter {

		public GattServicesAdapter() {
		}

		@Override
		public int getGroupCount() {
			return serviceList.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return serviceList.get(groupPosition).charactInfoList.size();
		}

		@Override
		public GattServicesInfo getGroup(int groupPosition) {
			return serviceList.get(groupPosition);
		}

		@Override
		public CharactInfo getChild(int groupPosition, int childPosition) {
			return serviceList.get(groupPosition).charactInfoList
					.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			GattServicesInfo serviceInfo = getGroup(groupPosition);
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.custom_listitem_device, null);
				viewHolder = new ViewHolder();
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			TextView tvDeviceName = viewHolder.obtainView(convertView, R.id.cutom_device_name);
			TextView tvUUID = viewHolder.obtainView(convertView, R.id.cutom_device_uuid);
			tvDeviceName.setText(serviceInfo.serviceName);
			tvUUID.setText(serviceInfo.uuid);
			return convertView;
		}

		@Override
		public View getChildView(final int groupPosition,
				final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			final CharactInfo charactInfo = getChild(groupPosition, childPosition);
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.child_listitem_device, null);
				viewHolder = new ViewHolder();
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			TextView tvDeviceName = viewHolder.obtainView(convertView, R.id.child_device_name);
			TextView tvUUID = viewHolder.obtainView(convertView, R.id.child_device_uuid);
			Button bteWrite = viewHolder.obtainView(convertView, R.id.character_write_button);
			Button btnRead = viewHolder.obtainView(convertView, R.id.character_read_button);
			Button btnNotify = viewHolder.obtainView(convertView, R.id.character_notify_button);
			Button btnIndicate = viewHolder.obtainView(convertView, R.id.character_indicate_button);
			Log.i("log", "charactInfo.propertie:" + charactInfo.propertie);

			//bteWrite.setVisibility(View.GONE);
			//btnRead.setVisibility(View.GONE);
		//	btnNotify.setVisibility(View.GONE);
		//	btnIndicate.setVisibility(View.GONE);
			bteWrite.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					charactInfo.gattCharacteristic.setValue("150005001200000002".getBytes());
					mBluetoothLeService.wirteCharacteristic(charactInfo.gattCharacteristic);
					Log.i("linqiang", "onClick: 写入开始");
				}
			});
			btnRead.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					byte[] valueByte = charactInfo.gattCharacteristic.getValue();
					// Log.v("log", "value:"+new String(data, charsetName));
					mBluetoothLeService.readCharacteristic(charactInfo.gattCharacteristic);
				}
			});
			btnNotify.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Log.i("linqiang", "通知UUID："+charactInfo.gattCharacteristic.getUuid());
					mBluetoothLeService.setCharacteristicNotification(charactInfo.gattCharacteristic, true);
				}
			});

			switch (charactInfo.propertie) {
			case (BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_READ):
				bteWrite.setVisibility(View.VISIBLE);
				btnRead.setVisibility(View.VISIBLE);
				break;
			case BluetoothGattCharacteristic.PROPERTY_WRITE:
				bteWrite.setVisibility(View.VISIBLE);
				break;
			case BluetoothGattCharacteristic.PROPERTY_READ:
				btnRead.setVisibility(View.VISIBLE);
				break;
			case BluetoothGattCharacteristic.PROPERTY_NOTIFY:
				btnNotify.setVisibility(View.VISIBLE);
				break;
			case BluetoothGattCharacteristic.PROPERTY_INDICATE:
				btnIndicate.setVisibility(View.VISIBLE);
				break;

			default:
				break;
			}

			tvDeviceName.setText(charactInfo.charactName);
			tvUUID.setText(charactInfo.uuid);
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return false;
		}

		class ViewHolder {
			private SparseArray<View> views = new SparseArray<View>();

			/**
			 * 指定resId和类型即可获取到相应的view
			 *
			 * @param convertView
			 * @param resId
			 * @param <T>
			 * @return
			 */
			<T extends View> T obtainView(View convertView, int resId) {
				View v = views.get(resId);
				if (null == v) {
					v = convertView.findViewById(resId);
					views.put(resId, v);
				}
				return (T) v;
			}

		}

	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}
}
