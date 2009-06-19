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

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.RemoteViews;

public class BatteryLevelWidget extends AppWidgetProvider {
	static final ComponentName THIS_APPWIDGET = new ComponentName(BatteryLevelWidget.class
			.getPackage().getName(), BatteryLevelWidget.class.getCanonicalName());
	private static BatteryLevelWidget sInstance;

	static synchronized BatteryLevelWidget getInstance() {
		if (sInstance == null) {
			sInstance = new BatteryLevelWidget();
		}
		return sInstance;
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Intent serviceIntent = new Intent(IBatteryLevelService.class.getCanonicalName());
		context.startService(serviceIntent);
	}

	public void notifyChanges(BatteryLevelService service) {
		RemoteViews views = new RemoteViews(service.getPackageName(), R.layout.appwidget);
		views.setTextViewText(R.id.level, String.format("%d%%", service.getLevel()));
		int level = service.getLevel();
		int status = service.getStatus();
		switch (status) {
		case BatteryManager.BATTERY_STATUS_UNKNOWN:
			views.setImageViewResource(R.id.status, R.drawable.stat_sys_battery_unknown);
			break;
		case BatteryManager.BATTERY_STATUS_CHARGING:
			views.setImageViewResource(R.id.status, R.drawable.stat_sys_battery_charge);
			break;
		case BatteryManager.BATTERY_STATUS_DISCHARGING:
		case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
		case BatteryManager.BATTERY_STATUS_FULL:
			views.setImageViewResource(R.id.status, R.drawable.stat_sys_battery);
			views.setInt(R.id.status, "setImageLevel", level);
			break;
		default:
			views.setImageViewResource(R.id.status, R.drawable.stat_sys_battery_unknown);
			break;
		}
		AppWidgetManager gm = AppWidgetManager.getInstance(service);
		gm.updateAppWidget(THIS_APPWIDGET, views);
	}
}
