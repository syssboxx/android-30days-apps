package com.bakhtiyor.android.twitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class ImageLoader {
	public static final int DEFAULT_BUFFER_SIZE = 8192;
	public static final String TAG = ImageLoader.class.getSimpleName();
	ExecutorService executorService = new ThreadPoolExecutor(5, 10, 2000, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());
	private final HttpClient httpClient;
	Map<String, Drawable> images = new ConcurrentHashMap<String, Drawable>();
	private final Drawable progressDrawable;

	public ImageLoader(Drawable progressDrawable) {
		this.progressDrawable = progressDrawable;
		httpClient = createMultiThreadedHttpClient(10, 10000);
	}

	public Drawable load(String url, Callback callback) {
		if (images.containsKey(url))
			return images.get(url);

		Log.i(TAG, "fetching " + url);
		executorService.execute(new LoadImage(url, callback));
		return progressDrawable;
	}

	public void release() {
		executorService.shutdown();
		images.clear();
	}

	private HttpClient createMultiThreadedHttpClient(int maxConnections, int timeout) {
		HttpParams params = new BasicHttpParams();
		ConnManagerParams.setMaxTotalConnections(params, maxConnections);
		ConnManagerParams.setTimeout(params, timeout);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
		return new DefaultHttpClient(cm, params);
	}

	public interface Callback {
		void done(String url, Bitmap bitmap);
	}

	private class LoadImage implements Runnable {
		private final String url;
		private final Callback callback;

		LoadImage(String url, Callback callback) {
			this.url = url;
			this.callback = callback;
		}

		public void run() {
			images.put(url, progressDrawable);
			HttpContext localContext = new BasicHttpContext();
			HttpUriRequest request = new HttpGet(url);
			try {
				HttpResponse response = httpClient.execute(request, localContext);
				if (response.getStatusLine().getStatusCode() == 200 && response.getEntity() != null) {
					byte[] bytes = EntityUtils.toByteArray(response.getEntity());
					Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
					images.put(url, new BitmapDrawable(bitmap));
					callback.done(url, bitmap);
				}
			} catch (IOException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
	}
}
