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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class NigmaService {
	private static final int DEFAULT_BUFFER_SIZE = 8192;
	private static final String TAG = NigmaService.class.getSimpleName();
	private final HttpClient httpClient = new DefaultHttpClient();
	private final String URL_PATTERN = "http://nigma.ru/index.php?s=%s&nm=1";
	private static final Pattern REACT_PATTERN = Pattern
	        .compile("(<div class=\"react\">(.*?)</div>)");

	public List<String> getChemicalReactions(String reaction) {
		String url = String.format(URL_PATTERN, URLEncoder.encode(reaction));
		byte[] response = fetch(url);
		List<String> results = new ArrayList<String>();
		if (response != null) {
			String responseString = new String(response);
			Matcher matcher = REACT_PATTERN.matcher(responseString);
			while (matcher.find()) {
				String cleared = matcher.group(1).replaceAll("(?i)\\<([\\s/]?)(a|s|u).*?\\>", "")
				        .replaceAll("\\(((\\<.*?\\>)*?)(\\W+?)((\\<\\.*?\\>)*?)\\)", "")
				        .replaceAll("(\\p{InCyrillic}*?)", "");
				results.add(cleared);
			}
		}
		return !results.isEmpty() ? results : Collections.<String> emptyList();
	}

	private byte[] fetch(final String url) {
		final HttpUriRequest request = new HttpGet(url);
		try {
			HttpResponse response = httpClient.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				InputStream instream = response.getEntity().getContent();
				ByteArrayOutputStream outstream = new ByteArrayOutputStream(DEFAULT_BUFFER_SIZE);
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
}
