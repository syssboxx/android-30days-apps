package com.bakhtiyor.android.twitter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Settings extends PreferenceActivity {
	public static String USERNAME = "username";
	public static String PASSWORD = "password";

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
	}

	static void show(final Context context) {
		final Intent intent = new Intent(context, Settings.class);
		context.startActivity(intent);
	}
}
