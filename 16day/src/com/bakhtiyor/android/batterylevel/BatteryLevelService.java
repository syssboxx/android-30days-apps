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
package com.bakhtiyor.android.batterylevel;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class BatteryLevelService extends Service {
	protected static final String TAG = BatteryLevelService.class.getSimpleName();
	private final IBatteryLevelService.Stub binder = new IBatteryLevelService.Stub() {
		public int getHealth() throws RemoteException {
			return BatteryLevelService.this.getHealth();
		}

		public int getLevel() throws RemoteException {
			return BatteryLevelService.this.getLevel();
		}

		public int getStatus() throws RemoteException {
			return BatteryLevelService.this.getStatus();
		}

	};

	private final BroadcastReceiver batteryBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
				Log.i(TAG, "Intent.ACTION_BATTERY_CHANGED");
				int rawlevel = intent.getIntExtra("level", -1);
				int scale = intent.getIntExtra("scale", -1);
				status = intent.getIntExtra("status", -1);
				health = intent.getIntExtra("health", -1);
				level = -1;
				if (rawlevel >= 0 && scale > 0) {
					level = (rawlevel * 100) / scale;
				}
				notifyChanges();
			}
		}
	};

	private int health;
	private int level;
	private int status;

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		notifyChanges();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	int getStatus() {
		return status;
	}

	int getLevel() {
		return level;
	}

	int getHealth() {
		return health;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryBroadcastReceiver, intentFilter);
	}

	private void notifyChanges() {
		BatteryLevelWidget.getInstance().notifyChanges(this);
	}
}
