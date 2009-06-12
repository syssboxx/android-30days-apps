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
package com.bakhtiyor.android.game;

import java.util.List;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

public class Mosquito extends Activity {
	private MosquitoView mosquitoView;
	private SensorManager sensorManager;
	private SensorEventListener sensorListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		        WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mosquitoView = new MosquitoView(this);
		setContentView(mosquitoView);
		setScreenFlags();
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorListener = new SensorEventListener() {
			private float lastValueY = 0;
			private long skipsCount = 0;

			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}

			public void onSensorChanged(SensorEvent event) {
				float valueY = event.values[SensorManager.DATA_Y];
				if (lastValueY - valueY > 1 && skipsCount <= 0) {
					mosquitoView.slap();
					skipsCount = 7;
				}
				if (skipsCount > 0) {
					skipsCount--;
				}
				lastValueY = valueY;
			}
		};
	}

	private void setScreenFlags() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		getWindow().setAttributes(lp);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (sensorManager != null) {
			List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
			if ((sensors != null && !sensors.isEmpty())) {
				sensorManager.registerListener(sensorListener, sensors.get(0),
						SensorManager.SENSOR_DELAY_NORMAL);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (sensorManager != null) {
			sensorManager.unregisterListener(sensorListener);
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			mosquitoView.slap();
		}
		return super.onKeyUp(keyCode, event);
	}

}