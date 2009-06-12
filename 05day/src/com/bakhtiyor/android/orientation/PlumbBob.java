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
package com.bakhtiyor.android.orientation;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

public class PlumbBob extends Activity {
	private SensorEventListener sensorListener;
	private PlumbBobView plumbBobView;
	private SensorManager sensorManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		plumbBobView = new PlumbBobView(this);
		setContentView(plumbBobView);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorListener = new SensorEventListener() {
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}

			public void onSensorChanged(SensorEvent event) {
				float value = event.values[SensorManager.DATA_Z];
				plumbBobView.setDegree(value);
			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
		List<Sensor> sensors = sensorManager
				.getSensorList(Sensor.TYPE_ORIENTATION);
		if (false || (sensors != null && !sensors.isEmpty())) {
			sensorManager.registerListener(sensorListener, sensors.get(0),
					SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	@Override
	protected void onPause() {
		sensorManager.unregisterListener(sensorListener);
		super.onPause();
	}

	private class PlumbBobView extends View {
		private final Drawable drawable;
		private final Paint paint;
		private final int drawableWidth;
		private final int drawableHeight;
		private Float degree;
		private int centerX;

		public PlumbBobView(Context context) {
			super(context);
			drawable = context.getResources().getDrawable(R.drawable.plumb_bob);
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(Color.TRANSPARENT);
			paint.setStyle(Paint.Style.FILL);
			drawableWidth = drawable.getIntrinsicWidth();
			drawableHeight = drawable.getIntrinsicHeight();
		}

		void setDegree(Float degree) {
			Float oldValue = this.degree;
			this.degree = degree;
			if (oldValue != degree) {
				invalidate();
			}
		}

		@Override
		protected void onSizeChanged(int width, int height, int oldw, int oldh) {
			super.onSizeChanged(width, height, oldw, oldh);
			centerX = width / 2;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			canvas.translate(centerX - drawableWidth / 2, 0);
			if (degree != null) {
				canvas.rotate(degree);
			}
			drawable.setBounds(0, 0, drawableWidth, drawableHeight);
			drawable.draw(canvas);
		}

	}
}