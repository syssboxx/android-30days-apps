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
package com.bakhtiyor.android.tumblr;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class UploadActivity extends Activity {
	private String filename;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload);

		if (!unpackIntent(getIntent())) {

		}

		final EditText emailView = (EditText) findViewById(R.id.email);
		final EditText passwordView = (EditText) findViewById(R.id.password);
		final EditText captionView = (EditText) findViewById(R.id.caption);
		final CheckBox isPrivateView = (CheckBox) findViewById(R.id.is_private);
		final CheckBox saveAccount = (CheckBox) findViewById(R.id.save_account);
		Button uploadButton = (Button) findViewById(R.id.upload_button);
		uploadButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String email = emailView.getText().toString();
				String password = passwordView.getText().toString();
				String caption = captionView.getText().toString();
				boolean isPrivate = isPrivateView.isChecked();
				if (saveAccount.isChecked()) {
					saveAccountInfo(email, password);
				} else {
					saveAccountInfo("", "");
				}
				Intent intent = new Intent(ITumblrService.class.getCanonicalName());
				intent.setAction(Intent.ACTION_SEND);
				intent.putExtra(TumblrService.KEY_EMAIL, email);
				intent.putExtra(TumblrService.KEY_PASSWORD, password);
				intent.putExtra(TumblrService.KEY_CAPTION, caption);
				intent.putExtra(TumblrService.KEY_IS_PRIVATE, isPrivate);
				intent.putExtra(TumblrService.KEY_FILENAME, filename);
				startService(intent);
				finish();
			}
		});
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String savedEmail = sharedPreferences.getString(TumblrService.KEY_EMAIL, "");
		String savedPassword = sharedPreferences.getString(TumblrService.KEY_PASSWORD, "");
		emailView.setText(savedEmail);
		passwordView.setText(savedPassword);
	}

	protected void saveAccountInfo(String email, String password) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		sharedPreferences.edit().putString(TumblrService.KEY_EMAIL, email).commit();
		sharedPreferences.edit().putString(TumblrService.KEY_PASSWORD, password).commit();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private boolean unpackIntent(Intent intent) {
		Bundle extras = intent.getExtras();

		if (!Intent.ACTION_SEND.equals(intent.getAction()) || (extras == null))
			return false;

		if (extras.containsKey(Intent.EXTRA_STREAM)) {
			Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
			if (uri != null && intent.getType().startsWith("image/")) {
				filename = getMediaFilename(uri);
			}
		}
		return false;
	}

	private String getMediaFilename(Uri uri) {
		String result = null;
		Cursor cursor = MediaStore.Images.Media.query(getContentResolver(), uri,
				new String[] { MediaStore.Images.ImageColumns.DATA }, "1=1", "_id");
		if (cursor.getCount() != 0) {
			cursor.moveToFirst();
			result = cursor.getString(0);
		}
		cursor.close();
		return result;
	}

}
