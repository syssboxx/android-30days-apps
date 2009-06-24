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
package com.bakhtiyor.android.barcode.upc;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;

import org.xmlrpc.android.XMLRPCClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class BarcodeReader extends Activity {
	private static final String GOOGLE_SEARCH_URI = "http://google.com/?q=%s";
	private static final String LOCAL_SEARCH_URI = "http://local.google.com/maps?q=%s";
	private static final String TAG = BarcodeReader.class.getSimpleName();
	private TextView barcodeView;
	private View bottomView;
	private TextView countryView;
	private Animation fadeInAnimation;
	private Animation fadeOutAnimation;
	private TextView sizeView;
	private TextView titleView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main);
		bottomView = findViewById(R.id.bottom);
		barcodeView = (TextView) findViewById(R.id.barcode);
		titleView = (TextView) findViewById(R.id.title);
		sizeView = (TextView) findViewById(R.id.size);
		countryView = (TextView) findViewById(R.id.country);
		fadeInAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
		fadeOutAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
		Button scanButton = (Button) findViewById(R.id.scan);
		scanButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				scan();
			}
		});
		Button localSearchButton = (Button) findViewById(R.id.Local_search);
		localSearchButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String url = String.format(LOCAL_SEARCH_URI, URLEncoder.encode(titleView.getText()
						.toString()));
				openBrowser(url);
			}
		});
		Button googleSearchButton = (Button) findViewById(R.id.google_search);
		googleSearchButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String url = String.format(GOOGLE_SEARCH_URI, URLEncoder.encode(titleView.getText()
						.toString()));
				openBrowser(url);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (IntentIntegrator.REQUEST_CODE == requestCode && data != null) {
			IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode,
					data);
			if (scanResult != null) {
				String format = scanResult.getFormatName();
				String barcode = scanResult.getContents();
				barcodeView.setText(String.format("%s: %s", format, barcode));
				if (format.contains("UPC")) {
					new FetchTask().execute(barcode);
				} else {
					Toast.makeText(this, "Only UPC barcode fotmat supported by now",
							Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	private void scan() {
		if (bottomView.getVisibility() == View.VISIBLE) {
			bottomView.startAnimation(fadeOutAnimation);
			bottomView.setVisibility(View.INVISIBLE);
		}
		IntentIntegrator.initiateScan(this);
	}

	private void showNotFoundDialog(final String barcode) {
		DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (which == DialogInterface.BUTTON_POSITIVE) {
					String url = String.format(GOOGLE_SEARCH_URI, URLEncoder.encode(barcode));
					openBrowser(url);
				}
				dialog.dismiss();
			}
		};
		AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Not Found").setMessage(
				String.format(
						"%s is not found in UPC barcode database. Would you like to google it?",
						barcode)).setPositiveButton("Google It", clickListener).setNegativeButton(
				"Close", clickListener).create();
		dialog.show();
	}

	private void openBrowser(String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		Log.i(TAG, url);
		startActivity(intent);
	}

	private class FetchTask extends UserTask<String, Void, Map<String, Object>> {
		private String barcode;

		@Override
		public Map<String, Object> doInBackground(String... params) {
			barcode = params[0];
			XMLRPCClient client = new XMLRPCClient("http://www.upcdatabase.com/rpc");
			try {
				@SuppressWarnings("unchecked")
				Map<String, Object> result = (Map<String, Object>) client.call("lookupUPC",
						new Object[] { barcode });
				return result;
			} catch (Throwable t) {
				Log.e(TAG, t.getMessage(), t);
			}
			return Collections.<String, Object> emptyMap();
		}

		@Override
		public void onPostExecute(Map<String, Object> result) {
			super.onPostExecute(result);
			setProgressBarIndeterminateVisibility(false);
			if (!result.isEmpty()
					&& result.get("message").toString().equalsIgnoreCase("Database entry found")) {
				String desc = result.containsKey("description") ? result.get("description")
						.toString() : "Not available";
				String size = result.containsKey("size") ? result.get("size").toString()
						: "Not available";
				String issuerCountry = result.containsKey("issuerCountry") ? result.get(
						"issuerCountry").toString() : "Not available";
				titleView.setText(desc);
				sizeView.setText(size);
				countryView.setText(issuerCountry);
				bottomView.setVisibility(View.VISIBLE);
				bottomView.startAnimation(fadeInAnimation);
				for (String key : result.keySet()) {
					Log.i(TAG, key + ": " + result.get(key).toString());
				}
			} else {
				showNotFoundDialog(barcode);
			}
		}

		@Override
		public void onPreExecute() {
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
		}
	}
}