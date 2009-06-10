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
package com.bakhtiyor.android.converter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class CurrencyConverter extends Activity {
	private class CurrencyUpdateTask extends UserTask<String, Integer, Currency> {
		private ProgressDialog progressDialog;

		@Override
		public Currency doInBackground(String... params) {
			String code = params[0];
			return converterService.getCurrency(code);
		}

		@Override
		public void onPostExecute(Currency result) {
			progressDialog.dismiss();
			currentCurrency = result;
			if (result != null) {
				currencies.put(result.code, result);
			}
		}

		@Override
		public void onPreExecute() {
			progressDialog = ProgressDialog
			        .show(CurrencyConverter.this, getString(R.string.loading_title),
			                getString(R.string.loading_txt), true, false);
		}
	}

	private static final String KEY_CURRENCIES = "currencies";
	private static final String TAG = CurrencyConverter.class.getSimpleName();
	private final Map<String, Currency> currencies = new HashMap<String, Currency>();
	private Currency currentCurrency;
	private MoneyConverterService converterService;
	private Spinner fromList;
	private Spinner toList;
	private EditText fromCurrency;
	private EditText toCurrency;

	private TextView textView;

	@SuppressWarnings("serial")
	private static final Map<String, String> CURRENCY_MAP = new HashMap<String, String>() {
		{
			put("AED", "Arab Emirates Dirham");
			put("ARS", "Argentina Peso");
			put("AUD", "Australian Dollar");
			put("BRL", "Brazilian Real");
			put("CAD", "Canadian Dollar");
			put("CHF", "Swiss Franc");
			put("CLP", "Chilean Peso");
			put("CNY", "Chinese Yuan");
			put("CYP", "Cyprus Pound");
			put("CZK", "Czech Koruna");
			put("DKK", "Danish Krone");
			put("EUR", "Eurozone Euro");
			put("GBP", "Pound Sterling");
			put("HKD", "Hong Kong Dollar");
			put("HUF", "Hungarian Forint");
			put("ILS", "Israeli Sheqel");
			put("INR", "Indian Rupee");
			put("JPY", "Japanese Yen");
			put("KRW", "South Korean Won");
			put("MAD", "Moroccan Dirham");
			put("MXN", "Mexican Peso");
			put("NOK", "Norwegian Krone");
			put("NZD", "New Zealand Dollar");
			put("PHP", "Philippine Peso");
			put("PKR", "Pakistani Rupee");
			put("PLN", "Polish Zloty");
			put("RUB", "Russian Rouble");
			put("SEK", "Swedish Krona");
			put("SGD", "Singapore Dollar");
			put("THB", "Thai Baht");
			put("TRY", "Turkish New Lira");
			put("USD", "United States Dollar");
			put("ZAR", "South African Rand");
		}
	};

	@SuppressWarnings("serial")
	private static final List<String> CURRENCY_LIST = new ArrayList<String>(CURRENCY_MAP.keySet()) {
		{
			add("---");
			Collections.sort(this);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.containsKey(KEY_CURRENCIES)) {
			@SuppressWarnings("unchecked")
			Map<String, Currency> savedCurrencies = (Map<String, Currency>) savedInstanceState
			        .getSerializable(KEY_CURRENCIES);
			currencies.putAll(savedCurrencies);
		}
		setContentView(R.layout.main);
		fromList = (Spinner) findViewById(R.id.from_curruncy_list);
		toList = (Spinner) findViewById(R.id.to_curruncy_list);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		        android.R.layout.simple_spinner_item, CURRENCY_LIST);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fromList.setAdapter(adapter);
		converterService = new MoneyConverterService();
		toList.setAdapter(adapter);
		fromList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position > 0) {
					requestCurrency(CURRENCY_LIST.get(position));
					convert();
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		toList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				convert();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		fromCurrency = (EditText) findViewById(R.id.from_currency_edit);
		toCurrency = (EditText) findViewById(R.id.to_currency_edit);
		fromCurrency.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				convert();
				return false;
			}
		});
		textView = (TextView) findViewById(R.id.text);
	}

	@Override
	protected void onResume() {
		super.onResume();
		fromList.setSelection(0);
		toList.setSelection(0);
		fromCurrency.setText("");
		toCurrency.setText("");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (!currencies.isEmpty()) {
			outState.putSerializable(KEY_CURRENCIES, (Serializable) currencies);
		}
	}

	private void convert() {
		Double amount = null;
		try {
			amount = Double.valueOf(fromCurrency.getText().toString());
		} catch (Exception e) {
		}
		if (amount != null && currentCurrency != null) {
			String code = CURRENCY_LIST.get(toList.getSelectedItemPosition());
			if (code.equals("---"))
				return;
			Double total = 0.0;
			if (code.equals(currentCurrency.code)) {
				total = amount;
			} else {
				Double rate = currentCurrency.rates.get(code);
				total = amount * rate;
			}
			toCurrency.setText(String.format("%.2f", total));
			textView.setText(String.format("%.2f %s = %.2f %s", amount, CURRENCY_MAP
			        .get(currentCurrency.code), total, CURRENCY_MAP.get(code)));
		}
	}

	private void requestCurrency(final String code) {
		if (currencies.get(code) == null) {
			try {
				new CurrencyUpdateTask().execute(code);
			} catch (Throwable t) {
				Log.e(TAG, t.getMessage(), t);
			}
		}
	}
}