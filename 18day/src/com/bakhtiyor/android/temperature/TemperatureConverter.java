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
package com.bakhtiyor.android.temperature;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class TemperatureConverter extends Activity {
	protected static final String TAG = TemperatureConverter.class.getSimpleName();
	private EditText celsius;
	private EditText fahrenheit;
	private TextView textView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		celsius = (EditText) findViewById(R.id.celsius);
		fahrenheit = (EditText) findViewById(R.id.fahrenheit);
		textView = (TextView) findViewById(R.id.text);
		View.OnKeyListener onKeyListener = new View.OnKeyListener() {
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if ((KeyEvent.KEYCODE_0 <= keyCode && keyCode <= KeyEvent.KEYCODE_9)
				        || keyCode == KeyEvent.KEYCODE_PERIOD || keyCode == KeyEvent.KEYCODE_DEL) {
					if (view instanceof EditText) {
						EditText editText = (EditText) view;
						Double degree = 0.0;
						try {
							degree = Double.valueOf(editText.getText().toString());
						} catch (Throwable t) {
							Log.e(TAG, t.getMessage());
							return false;
						}
						if (editText.equals(celsius)) {
							fahrenheit.setText(String.format("%.2f", celsiusToFahrenheit(degree)));
							textView.setText(String.format(
							        getString(R.string.celsius_fahrenheit_format),
							        degree, celsiusToFahrenheit(degree)));
						} else if (editText.equals(fahrenheit)) {
							celsius.setText(String.format("%.2f", fahrenheitToCelsius(degree)));
							textView.setText(String.format(
							        getString(R.string.fahrenheit_celsius_format), degree,
							        fahrenheitToCelsius(degree)));
						}
					}
				}
				return false;
			}
		};
		celsius.setOnKeyListener(onKeyListener);
		fahrenheit.setOnKeyListener(onKeyListener);
	}

	private double celsiusToFahrenheit(double degree) {
		return degree * (9.0 / 5) + 32;
	}

	private double fahrenheitToCelsius(double degree) {
		return (degree - 32) * (5.0 / 9);
	}
}