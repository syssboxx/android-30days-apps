package com.bakhtiyor.android.countmein;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class CountMeInActivity extends Activity {
	protected static final String TAG = CountMeInActivity.class.getSimpleName();
	private ICountMeInService service = null;
	private final ServiceConnection serviceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder binder) {
			service = ICountMeInService.Stub.asInterface(binder);
			try {
				counterText.setText(String.valueOf(service.getValue()));
			} catch (RemoteException e) {
				Log.i(TAG, e.getMessage(), e);
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			service = null;
		}
	};
	private TextView counterText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		counterText = (TextView) findViewById(R.id.counter_text);
		final ImageButton decrement = (ImageButton) findViewById(R.id.decrement);
		final ImageButton increment = (ImageButton) findViewById(R.id.increment);
		View.OnClickListener clickListener = new View.OnClickListener() {
			public void onClick(View v) {
				try {
					if (v.equals(decrement)) {
						service.dec();
					} else if (v.equals(increment)) {
						service.inc();
					}
					counterText.setText("" + service.getValue());
				} catch (RemoteException e) {
					Log.i(TAG, e.getMessage(), e);
				}
			}
		};
		decrement.setOnClickListener(clickListener);
		increment.setOnClickListener(clickListener);
		bindService();
	}

	private void bindService() {
		Intent i = new Intent(ICountMeInService.class.getName());
		startService(i);
		bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(serviceConnection);
	}
}