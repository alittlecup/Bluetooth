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

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for
 * demonstration purposes.<br>
 * uuid:00001800-0000-1000-8000-00805f9b34fb
 * uuid:00001801-0000-1000-8000-00805f9b34fb
 * uuid:0000180a-0000-1000-8000-00805f9b34fb
 * uuid:0000fff0-0000-1000-8000-00805f9b34fb
 * 
 */

public class SampleGattAttributes {
	private static HashMap<String, String> attributes = new HashMap();
	public static String BLUE_HOT_MEASUREMENT = "0000ffe0-0000-1000-8000-00805f9b34fb";
	public static String CLIENT_CHARACTERISTIC_CONFIG = "0000ffe2-0000-1000-8000-00805f9b34fb";
	public static String DESCRIPTOR_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
	public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";

	static {
		// Sample Services.
		attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
		attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
		// Sample Characteristics.
		attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");

		attributes.put("00001802-0000-1000-8000-00805f9b34fb", "Immediate Alert Service");
		attributes.put("00001800-0000-1000-8000-00805f9b34fb", "Generic Access");
		attributes.put("00001801-0000-1000-8000-00805f9b34fb", "Generic Attribute");
		attributes.put("00001803-0000-1000-8000-00805f9b34fb", "Link Loss");
		attributes.put("00001804-0000-1000-8000-00805f9b34fb", "Tx Power");
		attributes.put("0000180f-0000-1000-8000-00805f9b34fb", "Battery Service");
		attributes.put("0000ffe0-0000-1000-8000-00805f9b34fb", "Button Event");
		attributes.put("00002a06-0000-1000-8000-00805f9b34fb", "Alert Level");
		attributes.put("00002a00-0000-1000-8000-00805f9b34fb", "Device Name");
		attributes.put("00002a01-0000-1000-8000-00805f9b34fb", "Appearance");
		attributes.put("00002a02-0000-1000-8000-00805f9b34fb", "Peripheral Privacy Flag");
		attributes.put("00002a03-0000-1000-8000-00805f9b34fb", "Reconnection Address");
		attributes.put("00002a04-0000-1000-8000-00805f9b34fb", "Peripheral Preferred Connection Parameters");
		attributes.put("00002a05-0000-1000-8000-00805f9b34fb", "Service Changed");
		attributes.put("00002a23-0000-1000-8000-00805f9b34fb", "System ID");
		attributes.put("00002a24-0000-1000-8000-00805f9b34fb", "Model Number String");
		attributes.put("00002a25-0000-1000-8000-00805f9b34fb", "Serial Number String");
		attributes.put("00002a26-0000-1000-8000-00805f9b34fb", "Firmware Revision String");
		attributes.put("00002a27-0000-1000-8000-00805f9b34fb", "Hardware Revision String");
		attributes.put("00002a28-0000-1000-8000-00805f9b34fb", "Software Revision String");
		attributes.put("00002a2a-0000-1000-8000-00805f9b34fb", "IEEE 11073-20601 Regulatory Certification Data List");
		attributes.put("00002a50-0000-1000-8000-00805f9b34fb", "PnP ID");
		attributes.put("00002a07-0000-1000-8000-00805f9b34fb", "Tx Power Level");
		attributes.put("00002a19-0000-1000-8000-00805f9b34fb", "Battery Level");
		attributes.put("0000ffe1-0000-1000-8000-00805f9b34fb", "Button Event");

		// custom
		attributes.put("0000fff0-0000-1000-8000-00805f9b34fb", "Blood pressure");
		//ETC
		attributes.put("0000fee9-0000-1000-8000-00805f9b34fb", "ETC读写服务");
		attributes.put("d44bc439-abfd-45a2-b575-925416129600", "写入");
		attributes.put("d44bc439-abfd-45a2-b575-925416129601","通知");
	}

	public static String lookup(String uuid, String defaultName) {
		String name = attributes.get(uuid);
		return name == null ? defaultName : name;
	}
}
