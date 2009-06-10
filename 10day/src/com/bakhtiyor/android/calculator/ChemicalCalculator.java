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
package com.bakhtiyor.android.calculator;

import java.net.URLEncoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

public class ChemicalCalculator extends Activity {
	private final NigmaService nigmaService = new NigmaService();
	private WebView webkit;
	private EditText reaction;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main);
		webkit = (WebView) findViewById(R.id.webkit);
		reaction = (EditText) findViewById(R.id.reaction);
		Button calculate = (Button) findViewById(R.id.calculate);
		calculate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new CalculateTask().execute(reaction.getText().toString());
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, 0, Menu.NONE, "Help").setIcon(android.R.drawable.ic_menu_help);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = super.onOptionsItemSelected(item);
		if (item.getItemId() == 0) {
			new AlertDialog.Builder(this).setMessage(R.string.help).setTitle("Query Help").show();
			result = true;
		}
		return result;
	}

	private class CalculateTask extends UserTask<String, Void, List<String>> {
		private final String TAG = CalculateTask.class.getSimpleName();
		private String reaction;

		@Override
		public List<String> doInBackground(String... params) {
			reaction = params[0];
			return nigmaService.getChemicalReactions(reaction);
		}

		@Override
		public void onPreExecute() {
			setProgressBarIndeterminateVisibility(true);
		}

		@Override
		public void onPostExecute(List<String> result) {
			setProgressBarIndeterminateVisibility(false);
			if (!result.isEmpty()) {
				for (String s : result) {
					Log.i(TAG, "Result: " + s);
				}
				webkit.loadData(getString(R.string.result_header) + join(result, "")
				        + getString(R.string.result_footer), "text/html", "UTF-8");
			} else {
				webkit.loadData(String.format(getString(R.string.not_found), URLEncoder
				        .encode(reaction)), "text/html", "UTF-8");
			}
		}
	}

	private String join(Collection<?> s, String delimiter) {
		StringBuffer buffer = new StringBuffer();
		Iterator<?> iter = s.iterator();
		while (iter.hasNext()) {
			buffer.append(iter.next());
			if (iter.hasNext()) {
				buffer.append(delimiter);
			}
		}
		return buffer.toString();
	}
}