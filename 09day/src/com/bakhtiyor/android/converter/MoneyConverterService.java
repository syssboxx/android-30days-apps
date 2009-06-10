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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class MoneyConverterService {
	private final HttpClient httpClient = new DefaultHttpClient();
	private static final String TAG = MoneyConverterService.class.getSimpleName();
	private static final int DEFAULT_BUFFER_SIZE = 2048;

	public Currency getCurrency(String code) {
		byte[] rssFeed = fetch(getRSSUrl(code));
		return parse(code, new ByteArrayInputStream(rssFeed));
	}

	private Currency parse(String code, InputStream instream) {
		try {
			RssParser parser = new RssParser(instream);
			return parser.parse(code);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}

	private byte[] fetch(final String url) {
		final HttpUriRequest request = new HttpGet(url);
		try {
			HttpResponse response = httpClient.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				InputStream instream = response.getEntity().getContent();
				ByteArrayOutputStream outstream = new ByteArrayOutputStream(
						DEFAULT_BUFFER_SIZE);
				byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
				int len;
				while ((len = instream.read(buffer)) > 0) {
					outstream.write(buffer, 0, len);
				}
				outstream.close();
				instream.close();
				return outstream.toByteArray();
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}

	private String getRSSUrl(String code) {
		return String.format("http://themoneyconverter.com/%s/rss.xml", code);
	}
}
