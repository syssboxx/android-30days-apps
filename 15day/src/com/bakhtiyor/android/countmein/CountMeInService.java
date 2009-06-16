package com.bakhtiyor.android.countmein;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class CountMeInService extends Service {
	public static final String INCREMENT = CountMeInService.class.getCanonicalName() + ".increment";
	public static final String DECREMENT = CountMeInService.class.getCanonicalName() + ".decrement";
	public static final String UPDATE = CountMeInService.class.getCanonicalName() + ".update";
	private static final String TAG = CountMeInService.class.getSimpleName();

	private final ICountMeInService.Stub binder = new ICountMeInService.Stub() {
		public void dec() throws RemoteException {
			decImpl();
		}

		public int getValue() throws RemoteException {
			return getValueImpl();
		}

		public void inc() throws RemoteException {
			incImpl();
		}

		public void setValue(int value) throws RemoteException {
			setValueImpl(value);
		}
	};

	class InBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.i(TAG, "BroadcastReceiver.onRecive - action: " + action);
			if (action.equals(INCREMENT)) {
				incImpl();
			} else if (action.equals(DECREMENT)) {
				decImpl();
			} else if (action.equals(UPDATE)) {
				notifyChange(getValueImpl());
			}
		}

	}

	private final BroadcastReceiver intentReceiver = new InBroadcastReceiver();
	private static ICounter counter;

	@Override
	public void onCreate() {
		super.onCreate();
		counter = new PreferenceCounter(this);
		IntentFilter commandFilter = new IntentFilter();
		commandFilter.addAction(INCREMENT);
		commandFilter.addAction(DECREMENT);
		commandFilter.addAction(UPDATE);
		registerReceiver(intentReceiver, commandFilter);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	void decImpl() {
		Log.i(TAG, "decrement");
		notifyChange(counter.decrementAndGet());
	}

	int getValueImpl() {
		return counter.get();
	}

	void incImpl() {
		Log.i(TAG, "increment");
		notifyChange(counter.incrementAndGet());
	}

	void setValueImpl(int value) {
		counter.set(value);
		notifyChange(value);
	}

	private void notifyChange(int value) {
		CountMeInWidget.getInstance().notifyChange(this);
	}
}
