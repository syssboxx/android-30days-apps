package com.bakhtiyor.android.countmein;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceCounter implements ICounter {
	private static final String KEY_COUNTER = "counter";
	private final SharedPreferences sharedPreferences;

	public PreferenceCounter(Context context) {
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	@Override
	public synchronized int decrementAndGet() {
		int value = get();
		set(--value);
		return value;
	}

	@Override
	public int get() {
		return sharedPreferences.getInt(KEY_COUNTER, 0);
	}

	@Override
	public synchronized int incrementAndGet() {
		int value = get();
		set(++value);
		return value;
	}

	@Override
	public void set(int value) {
		sharedPreferences.edit().putInt(KEY_COUNTER, value).commit();
	}
}
