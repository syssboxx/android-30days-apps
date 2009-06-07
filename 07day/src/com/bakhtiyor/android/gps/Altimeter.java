package com.bakhtiyor.android.gps;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

public class Altimeter extends Activity {
	private TextView meter;
	private LocationListener locationListener;
	private LocationManager locationManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		meter = (TextView) findViewById(R.id.meter);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAltitudeRequired(true);
		String providerName = locationManager.getBestProvider(criteria, true);
		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				double altitude = location.getAltitude();
				meter.setText(String.format("%.0f m", altitude));
			}

			public void onProviderDisabled(String provider) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
			}
		};
		if (providerName != null) {
			locationManager.requestLocationUpdates(providerName, 0, 0, locationListener);
		} else {
			TextView notice = (TextView) findViewById(R.id.notice);
			notice.setText(R.string.enable_gps);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		locationManager.removeUpdates(locationListener);
	}
}