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

import java.io.File;
import java.util.Random;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class TumblrService extends Service {
	private final static String API_URL = "http://www.tumblr.com/api/write";
	private static final String FIELD_CAPTION = "caption";
	private static final String FIELD_EMAIL = "email";
	private static final String FIELD_PASSWORD = "password";
	private static final String FIELD_PRIVATE = "private";
	private static final String FIELD_TYPE = "type";
	public static final String KEY_CAPTION = "caption";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_FILENAME = "filename";
	public static final String KEY_IS_PRIVATE = "is_private";
	public static final String KEY_PASSWORD = "password";
	private final static String TAG = TumblrService.class.getName();
	private String GENERATOR;
	private String APP_TITLE;
	private static final int RESULT_NOTIFICATION_ID = 3;
	private final ITumblrService.Stub binder = new ITumblrService.Stub() {
		@Override
		public void uploadPhoto(final String email, final String password, final String caption,
				final boolean isPrivate, final String file) throws RemoteException {
			TumblrService.this.uploadPhoto(email, password, caption, isPrivate, file);
		}
	};

	private NotificationManager notificationManager;

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		APP_TITLE = getString(R.string.app_name);
		GENERATOR = APP_TITLE + "/Android";
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Bundle extras = intent.getExtras();
		if (Intent.ACTION_SEND.equals(intent.getAction()) && (extras != null)) {
			if (extras.containsKey(KEY_EMAIL) && extras.containsKey(KEY_PASSWORD)
					&& extras.containsKey(KEY_CAPTION) && extras.containsKey(KEY_IS_PRIVATE)
					&& extras.containsKey(KEY_FILENAME)) {
				String email = extras.getString(KEY_EMAIL);
				String password = extras.getString(KEY_PASSWORD);
				String caption = extras.getString(KEY_CAPTION);
				boolean isPrivate = extras.getBoolean(KEY_IS_PRIVATE);
				String filename = extras.getString(KEY_FILENAME);
				uploadPhoto(email, password, caption, isPrivate, filename);
			}
		}
		stopSelf(startId);
	}

	private void clearNotification(int id) {
		notificationManager.cancel(id);
	}

	private void showResultNotification(String message) {
		long when = System.currentTimeMillis();
		Notification notification = new Notification(R.drawable.icon, APP_TITLE, when);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		Intent notificationIntent = new Intent(this, TumblrService.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(getApplicationContext(), APP_TITLE, message,
				contentIntent);
		notificationManager.notify(RESULT_NOTIFICATION_ID, notification);
	}

	private void showUploadingNotification(int id) {
		long when = System.currentTimeMillis();
		Notification notification = new Notification(R.drawable.icon, APP_TITLE, when);
		Intent notificationIntent = new Intent(this, TumblrService.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(getApplicationContext(), APP_TITLE, "Uploading ...",
				contentIntent);
		notificationManager.notify(id, notification);
	}

	private void uploadPhoto(String email, String password, String caption, boolean isPrivate,
			String filename) {
		try {
			File file = new File(filename);
			final HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
			final PostMethod multipartPost = new PostMethod(API_URL);
			Part[] parts = { new StringPart(FIELD_EMAIL, email),
					new StringPart(FIELD_PASSWORD, password), new StringPart(FIELD_TYPE, "photo"),
					new StringPart("generator", GENERATOR), new StringPart(FIELD_CAPTION, caption),
					new StringPart(FIELD_PRIVATE, isPrivate ? "1" : "0"),
					new FilePart("data", file) };
			multipartPost.setRequestEntity(new MultipartRequestEntity(parts, multipartPost
					.getParams()));
			httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);
			int id = new Random().nextInt();
			showUploadingNotification(id);
			int status = httpClient.executeMethod(multipartPost);
			clearNotification(id);
			if (status == 201) {
				showResultNotification("Successful Uploaded");
			} else {
				showResultNotification("Error occured");
			}
		} catch (Throwable e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}
}
