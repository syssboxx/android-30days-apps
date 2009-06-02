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
package com.bakhtiyor.android.flashlight;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;

public class Flashlight extends Activity implements
		ColorPickerDialog.OnColorChangedListener {
	private static final String BRIGHTNESS_PREFERENCE_KEY = "brightness";
	private static final String COLOR_PREFERENCE_KEY = "color";
	private static final int DEFAULT_BRIGHTNESS_VALUE = 100;
	private static final int MENU_ID_BRIGHTNESS = Menu.FIRST;
	private static final int MENU_ID_COLOR = Menu.FIRST + 1;
	private static final int MENU_ID_RESET = Menu.FIRST + 2;
	private View mainView;
	private View brightnessPanel;
	private SeekBar brightnessControl;
	private int brightness;

	public void colorChanged(int color) {
		PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(
				COLOR_PREFERENCE_KEY, color).commit();
		mainView.setBackgroundColor(color);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);
		mainView = findViewById(R.id.main_view);
		brightnessPanel = findViewById(R.id.panel);
		brightnessControl = (SeekBar) findViewById(R.id.seek);
		brightnessControl
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						setBrightness(progress);
					}

					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					public void onStopTrackingTouch(SeekBar seekBar) {
						hideBrightnessPanel();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu) && createOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ID_BRIGHTNESS:
			showBrightnessPanel();
			return true;
		case MENU_ID_COLOR:
			int color = PreferenceManager.getDefaultSharedPreferences(this)
					.getInt(COLOR_PREFERENCE_KEY, Color.WHITE);
			new ColorPickerDialog(this, this, color).show();
			return true;
		case MENU_ID_RESET:
			colorChanged(getResources().getColor(R.color.default_color));
			setBrightness(DEFAULT_BRIGHTNESS_VALUE);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {
		super.onStart();
		int color = PreferenceManager.getDefaultSharedPreferences(this).getInt(
				COLOR_PREFERENCE_KEY,
				getResources().getColor(R.color.default_color));
		mainView.setBackgroundColor(color);
		int brightness = PreferenceManager.getDefaultSharedPreferences(this)
				.getInt(BRIGHTNESS_PREFERENCE_KEY, DEFAULT_BRIGHTNESS_VALUE);
		setBrightness(brightness);
	}

	@Override
	protected void onStop() {
		super.onStop();
		PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(
				BRIGHTNESS_PREFERENCE_KEY, this.brightness).commit();
	}

	private boolean createOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_ID_BRIGHTNESS, Menu.NONE, R.string.brightness)
				.setIcon(android.R.drawable.ic_menu_sort_by_size);
		menu.add(Menu.NONE, MENU_ID_COLOR, Menu.NONE, R.string.color).setIcon(
				android.R.drawable.ic_menu_view);
		menu.add(Menu.NONE, MENU_ID_RESET, Menu.NONE, R.string.reset).setIcon(
				android.R.drawable.ic_menu_revert);
		return true;
	}

	private void hideBrightnessPanel() {
		Animation animation = AnimationUtils.loadAnimation(Flashlight.this,
				android.R.anim.fade_out);
		brightnessPanel.startAnimation(animation);
		brightnessPanel.setVisibility(View.GONE);
		PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(
				BRIGHTNESS_PREFERENCE_KEY, brightnessControl.getProgress())
				.commit();
	}

	private void setBrightness(int value) {
		if (value < 10) {
			value = 10;
		} else if (value > 100) {
			value = 100;
		}
		brightness = value;
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.screenBrightness = (float) value / 100;
		lp.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		getWindow().setAttributes(lp);
	}

	private void showBrightnessPanel() {
		Animation animation = AnimationUtils.loadAnimation(this,
				android.R.anim.fade_in);
		brightnessControl.setProgress(this.brightness);
		brightnessPanel.setVisibility(View.VISIBLE);
		brightnessPanel.startAnimation(animation);
	}
}