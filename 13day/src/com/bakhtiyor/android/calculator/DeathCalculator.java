package com.bakhtiyor.android.calculator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class DeathCalculator extends Activity {
	private final SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
	private RadioGroup genderView;
	private EditText ageView;
	private TextView predictionView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		predictionView = (TextView) findViewById(R.id.prediction);
		ageView = (EditText) findViewById(R.id.age);
		ageView.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((KeyEvent.KEYCODE_0 <= keyCode && keyCode <= KeyEvent.KEYCODE_9)
				        || keyCode == KeyEvent.KEYCODE_DEL) {
					calculate();
				}
				return false;
			}
		});
		genderView = (RadioGroup) findViewById(R.id.gender);
		OnCheckedChangeListener listener = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				calculate();
			}
		};
		genderView.setOnCheckedChangeListener(listener);
	}

	private void calculate() {
		int age = 0;
		try {
			age = Integer.valueOf(ageView.getText().toString());
		} catch (Exception e) {
			return;
		}
		if (genderView.getCheckedRadioButtonId() != R.id.male
		        && genderView.getCheckedRadioButtonId() != R.id.female)
			return;
		boolean isMale = genderView.getCheckedRadioButtonId() == R.id.male ? true : false;
		long lifeExpectancyInYears = isMale ? 77 : 81;
		lifeExpectancyInYears -= age;
		int secondsInAYear = (int) (365.242199 * 24 * 60 * 60);
		long lifeExpectancy = lifeExpectancyInYears * secondsInAYear;
		lifeExpectancy += new Random().nextInt(secondsInAYear);
		Date date = new Date(Calendar.getInstance().getTimeInMillis() + lifeExpectancy * 1000);
		String text = String.format(getString(R.string.prediction_text), sdf.format(date),
		        lifeExpectancy, getString(R.string.proverb));
		predictionView.setText(text);
	}

}