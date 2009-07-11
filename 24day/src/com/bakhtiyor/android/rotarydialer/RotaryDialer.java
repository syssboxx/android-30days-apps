package com.bakhtiyor.android.rotarydialer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class RotaryDialer extends Activity {
	private EditText digits;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		RotaryDialerView rotaryDialerView = (RotaryDialerView) findViewById(R.id.rotary_dialer);
		digits = (EditText) findViewById(R.id.digits);
		rotaryDialerView.addDialListener(new RotaryDialerView.DialListener() {
			public void onDial(int number) {
				digits.append(String.valueOf(number));
			}
		});
		ImageButton backspace = (ImageButton) findViewById(R.id.backspace);
		backspace.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (digits.getText().toString().length() > 0) {
					digits.getText().delete(digits.getText().length() - 1,
							digits.getText().length());
				}
			}
		});
		backspace.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				if (digits.getText().toString().length() > 0) {
					digits.getText().clear();
					return true;
				}
				return false;
			}
		});
		digits.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				makeCall();
			}
		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_CALL) {
			makeCall();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void makeCall() {
		if (digits.getText().length() > 0) {
			String toDial = "tel:" + digits.getText().toString();
			startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(toDial)));
		}
	}

}