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
package com.bakhtiyor.android.scottris;

import java.util.List;

import tetrisbean.ScoreEvent;
import tetrisbean.ScoreListener;
import tetrisbean.TetrisPiece;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import com.bakhtiyor.android.gestures.FancyGestureDetector;

public class ScottrisActivity extends Activity {
	private ScottrisView scottrisView;
	private GestureDetector gestureDetector;
	private FancyGestureDetector fancyGestureDetector;
	private SensorEventListener sensorListener;
	private SensorManager sensorManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		scottrisView = new ScottrisView(this);
		setContentView(scottrisView);
		scottrisView.getGame().startGame();
		scottrisView.setFocusable(true);
		scottrisView.getGame().addScoreListener(new ScoreListener() {
			public void scoreChange(final ScoreEvent event) {
				runOnUiThread(new Runnable() {
					public void run() {
						setTitle(String.format(getString(R.string.app_title),
								getString(R.string.app_name), event.getScore()));
					}
				});
			}
		});
		gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public void onLongPress(MotionEvent e) {
				scottrisView.getGame().move(TetrisPiece.FALL);
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				scottrisView.getGame().move(TetrisPiece.ROTATE);
				return true;
			}
		});
		fancyGestureDetector = new FancyGestureDetector(
				new FancyGestureDetector.OnGestureListener() {
					public void onGesture(String gesture, float path) {
						if (gesture.equals("left")) {
							int moveCount = (int) (path / 70);
							for (int i = 0; i < moveCount; i++) {
								scottrisView.getGame().move(TetrisPiece.LEFT);
							}
						} else if (gesture.equals("right")) {
							int moveCount = (int) (path / 70);
							for (int i = 0; i < moveCount; i++) {
								scottrisView.getGame().move(TetrisPiece.RIGHT);
							}
						} else if (gesture.equals("down")) {
							scottrisView.getGame().move(TetrisPiece.DOWN);
						} else if (gesture.equals("rotate")) {
							scottrisView.getGame().move(TetrisPiece.ROTATE);
						}
					}
				});
		fancyGestureDetector.addGesture("left", new int[] { 4 });
		fancyGestureDetector.addGesture("right", new int[] { 1 });
		fancyGestureDetector.addGesture("down", new int[] { 2 });
		fancyGestureDetector.addGesture("rotate", new int[] { 6 });

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorListener = new SensorEventListener() {
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}

			public void onSensorChanged(SensorEvent event) {
				final float value = event.values[SensorManager.DATA_Z];
				if (value > 30) {
					scottrisView.getGame().move(TetrisPiece.LEFT);
				} else if (value < -30) {
					scottrisView.getGame().move(TetrisPiece.RIGHT);
				}
			}
		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(getApplication()).inflate(R.menu.optionsmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_new_game:
			scottrisView.getGame().startGame();
			return true;
		case R.id.menu_help:
			Toast.makeText(this, R.string.help, Toast.LENGTH_LONG).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menu_new_game).setVisible(!scottrisView.getGame().isPlaying());
		if (scottrisView.getGame().isPlaying()) {
			scottrisView.getGame().setPaused(true);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		if (scottrisView.getGame().isPaused()) {
			scottrisView.getGame().setPaused(false);
		}
		super.onOptionsMenuClosed(menu);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event) || fancyGestureDetector.onTouchEvent(event);
	}

	@Override
	protected void onPause() {
		if (sensorManager != null) {
			sensorManager.unregisterListener(sensorListener);
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (sensorManager != null) {
			List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
			if (sensors != null && !sensors.isEmpty()) {
				sensorManager.registerListener(sensorListener, sensors.get(0),
						SensorManager.SENSOR_DELAY_NORMAL);
			}
		}
	}

}