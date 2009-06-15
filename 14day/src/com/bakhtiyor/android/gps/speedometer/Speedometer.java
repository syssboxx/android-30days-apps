package com.bakhtiyor.android.gps.speedometer;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

public class Speedometer extends Activity {
	private LocationListener locationListener;
	private LocationManager locationManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final TextView speedView = (TextView) findViewById(R.id.speed);
		final SpeedometerView speedometerView = (SpeedometerView) findViewById(R.id.speedometer);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				if (location.hasSpeed()) {
					float speed = location.getSpeed() * 3.6f;// km/h
					speedView.setText(String.format("%.0f km/h", speed));
					speedometerView.setValue(speed);
				}
			}

			public void onProviderDisabled(String provider) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
			}
		};
	}

	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(locationListener);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Criteria criteria = new Criteria();
		criteria.setSpeedRequired(true);
		String providerName = locationManager.getBestProvider(criteria, true);
		if (providerName != null) {
			locationManager.requestLocationUpdates(providerName, 0, 0, locationListener);
		} else {
			TextView notice = (TextView) findViewById(R.id.notice);
			notice.setText(R.string.not_enabled);
		}
	}
}