/*
 * Copyright (c) 2009 Bakhtiyor Khodjayev (http://www.bakhtiyor.com/)
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
 * 
 */
package com.bakhtiyor.android.countdown;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class MinutesToMidnight extends Activity {
	private TextView countdown;
	private Timer timer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		countdown = (TextView) findViewById(R.id.countdown);
		TextView base = (TextView) findViewById(R.id.base);
		Typeface font = Typeface.createFromAsset(this.getAssets(),
				"fonts/digital-7 (mono).ttf");
		base.setTypeface(font);
		countdown.setTypeface(font);
	}

	@Override
	protected void onStart() {
		super.onStart();
		timer = new Timer("minutes-to-midnight");
		Calendar calendar = Calendar.getInstance();
		final Runnable updateTask = new Runnable() {
			public void run() {
				countdown.setText(getCountdownString());
			}
		};
		int msec = 999 - calendar.get(Calendar.MILLISECOND);
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(updateTask);
			}
		}, msec, 1000);
	}

	@Override
	protected void onStop() {
		super.onStop();
		timer.cancel();
		timer.purge();
		timer = null;
	}

	private String getCountdownString() {
		Calendar calendar = Calendar.getInstance();
		int hour = 23 - calendar.get(Calendar.HOUR_OF_DAY);
		int minute = 59 - calendar.get(Calendar.MINUTE);
		int second = 59 - calendar.get(Calendar.SECOND);
		return String.format("%02d:%02d:%02d", hour, minute, second);
	}
}