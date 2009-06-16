package com.bakhtiyor.android.countmein;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class CountMeInWidget extends AppWidgetProvider {
	static final ComponentName THIS_APPWIDGET = new ComponentName(CountMeInWidget.class
	        .getPackage().getName(), CountMeInWidget.class.getCanonicalName());
	private static CountMeInWidget sInstance;

	static synchronized CountMeInWidget getInstance() {
		if (sInstance == null) {
			sInstance = new CountMeInWidget();
		}
		return sInstance;
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Intent intent = new Intent(CountMeInService.UPDATE);
		// PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
		// intent, 0);
		context.sendBroadcast(intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	void notifyChange(CountMeInService service) {
		RemoteViews views = new RemoteViews(service.getPackageName(), R.layout.appwidget);
		views.setTextViewText(R.id.widget_counter, String.valueOf(String.format("%d", service
		        .getValueImpl())));
		linkControls(service, views);
		AppWidgetManager gm = AppWidgetManager.getInstance(service);
		gm.updateAppWidget(THIS_APPWIDGET, views);
	}

	private void linkControls(Context context, RemoteViews views) {
		Intent decIntent = new Intent(CountMeInService.DECREMENT);
		PendingIntent decPendingIntent = PendingIntent.getBroadcast(context, 0, decIntent, 0);
		views.setOnClickPendingIntent(R.id.widget_dec, decPendingIntent);

		Intent incIntent = new Intent(CountMeInService.INCREMENT);
		PendingIntent incPendingIntent = PendingIntent.getBroadcast(context, 0, incIntent, 0);
		views.setOnClickPendingIntent(R.id.widget_inc, incPendingIntent);
	}
}
