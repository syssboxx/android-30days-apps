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
package com.bakhtiyor.android.passgen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class PasswordGeneratorActivity extends Activity {
	private static final String KEY_PHONETIC = "phonetic";
	protected static final String TAG = PasswordGeneratorActivity.class.getSimpleName();
	private EditText passwordEdit;
	private TextView phoneticText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		final PasswordGenerator passwordGenerator = new PasswordGenerator();
		final CheckBox includeLowerCase = (CheckBox) findViewById(R.id.include_lowercase);
		final CheckBox includeUpperCase = (CheckBox) findViewById(R.id.include_uppercase);
		final CheckBox includeNumbers = (CheckBox) findViewById(R.id.include_numbers);
		final CheckBox includePunctuations = (CheckBox) findViewById(R.id.include_punctuations);
		final CheckBox showPhonetic = (CheckBox) findViewById(R.id.show_phonetic);
		final EditText passwordLength = (EditText) findViewById(R.id.length);
		passwordEdit = (EditText) findViewById(R.id.password);
		phoneticText = (TextView) findViewById(R.id.phonetic);
		Button button = (Button) findViewById(R.id.generate);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int length = 0;
				try {
					length = Integer.valueOf(passwordLength.getText().toString());
				} catch (Throwable t) {
					Log.i(TAG, t.getMessage(), t);
				}
				if (length <= 0
				        || !(includeLowerCase.isChecked() || includeUpperCase.isChecked()
				                || includeNumbers.isChecked() || includePunctuations.isChecked()))
					return;
				String password = passwordGenerator.generatePassword(length, includeLowerCase
				        .isChecked(), includeUpperCase.isChecked(), includeNumbers.isChecked(),
				        includePunctuations.isChecked());
				passwordEdit.setText(password);
				if (showPhonetic.isChecked()) {
					phoneticText.setText(passwordGenerator.getPhonetic(password));
				} else {
					phoneticText.setText("");
				}
			}
		});
		if (savedInstanceState != null && savedInstanceState.containsKey(KEY_PHONETIC)) {
			phoneticText.setText(savedInstanceState.getString(KEY_PHONETIC));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(getApplication()).inflate(R.menu.mainmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_send:
			Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
			sendIntent.setType("plain/text");
			sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
			        getString(R.string.subject_send_action));
			String message = "";
			if (phoneticText.getText().toString().length() > 0) {
				message = String.format(getString(R.string.template_send_phonetic), passwordEdit
				        .getText().toString(), phoneticText.getText().toString());
			} else {
				message = String.format(getString(R.string.template_send), passwordEdit.getText()
				        .toString());
			}
			sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
			startActivity(Intent.createChooser(sendIntent, getString(R.string.title_send_action)));
			return true;
		case R.id.menu_clear:
			passwordEdit.setText("");
			phoneticText.setText("");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (passwordEdit.getText().toString().length() > 0) {
			menu.findItem(R.id.menu_send).setVisible(true);
			menu.findItem(R.id.menu_clear).setVisible(true);
		} else {
			menu.findItem(R.id.menu_send).setVisible(false);
			menu.findItem(R.id.menu_clear).setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (phoneticText.getText().length() > 0) {
			outState.putString(KEY_PHONETIC, phoneticText.getText().toString());
		}
	}

}