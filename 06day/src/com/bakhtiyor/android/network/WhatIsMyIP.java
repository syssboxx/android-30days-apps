/*
 * Copyright (c) 2009 Bakhtiyor Khodjayev (http://www.bakhtiyor.com/)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.bakhtiyor.android.network;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class WhatIsMyIP extends Activity {
	private static final String TAG = WhatIsMyIP.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		new Thread(new Runnable() {
			public void run() {
				initUI();
			}
		}).run();
	}

	private List<String> getIpAddresses() {
		List<String> ips = new ArrayList<String>();
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
			        .hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> e = intf.getInetAddresses(); e.hasMoreElements();) {
					InetAddress inetAddress = e.nextElement();
					if (!inetAddress.isLoopbackAddress()) 
						ips.add(inetAddress.getHostAddress().toString());
				}
			}
		} catch (SocketException ex) {
			Log.e(TAG, ex.toString(), ex);
		}
		return !ips.isEmpty() ? ips : Collections.<String> emptyList();
	}

	private String getSocketIPAdress() {
		Socket conn = null;
		String result = null;
		try {
			try {
				conn = new Socket("www.google.com", 80);
				result = conn.getLocalAddress().toString();
			} finally {
				if (conn != null && !conn.isClosed()) 
					conn.close();
			}
		} catch (Throwable t) {
			Log.i(TAG, t.getMessage(), t);
		}
		return result;
	}

	private void initUI() {
		List<String> ips = getIpAddresses();
		final String ipAddress = !ips.isEmpty() ? join(ips, ", ") : getSocketIPAdress();
		runOnUiThread(new Runnable() {
			public void run() {
				updateTextView(ipAddress);
			}
		});
	}

	private String join(Collection<?> s, String delimiter) {
		StringBuffer buffer = new StringBuffer();
		Iterator<?> iter = s.iterator();
		while (iter.hasNext()) {
			buffer.append(iter.next());
			if (iter.hasNext()) {
				buffer.append(delimiter);
			}
		}
		return buffer.toString();
	}

	private void updateTextView(String ipAddress) {
		TextView textView = (TextView) findViewById(R.id.ip_address);
		if (ipAddress != null) {
			textView.setText(getString(R.string.ip_address) + ipAddress);
		} else {
			textView.setText(getString(R.string.ip_address) + getString(R.string.not_available));
		}
	}

}