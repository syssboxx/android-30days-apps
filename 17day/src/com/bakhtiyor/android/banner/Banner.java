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
package com.bakhtiyor.android.banner;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Banner extends Activity {
	private BannerView bannerView;

	private class BannerView extends View {
		public static final int SPEED_SLOW = -1;
		public static final int SPEED_NORMAL = 0;
		public static final int SPEED_FAST = 1;
		private static final int CELL_SIZE = 20;
		private static final int SCREEN_HEIGHT = 320;
		private static final int SCREEN_WIDTH = 480;
		private final Drawable tile;
		private Timer timer;
		private String text;
		private Bitmap textBitmap;
		private long counter;
		private int scrollCycle;
		private final RectF screenRect = new RectF(0, 0, SCREEN_WIDTH - 1, SCREEN_HEIGHT - 1);
		private final Rect frameRect;

		public BannerView(Context context) {
			super(context);
			tile = context.getResources().getDrawable(R.drawable.bg_tile);
			frameRect = new Rect();
			frameRect.top = 0;
			frameRect.bottom = SCREEN_HEIGHT - 1;
		}

		@Override
		public void draw(Canvas canvas) {
			super.draw(canvas);
			if (textBitmap != null) {
				int startPosition = (int) (counter % scrollCycle) * CELL_SIZE;
				frameRect.left = startPosition;
				frameRect.right = startPosition + SCREEN_WIDTH - 1;
				canvas.drawBitmap(textBitmap, frameRect, screenRect, null);
			}
			tile.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			tile.draw(canvas);
		}

		public String getText() {
			return text;
		}

		public void setText(String text, int speed, boolean antialias) {
			clearTimer();
			this.text = text;
			counter = 0;
			int interval = 100;
			switch (speed) {
			case SPEED_SLOW:
				interval = 200;
				break;
			case SPEED_FAST:
				interval = 50;
				break;
			default:
				break;
			}
			textBitmap = createTextBitmap(text, antialias);
			scrollCycle = textBitmap.getWidth() / CELL_SIZE;
			startTimer(interval);
		}

		@Override
		protected void onAttachedToWindow() {
			super.onAttachedToWindow();
		}

		@Override
		protected void onDetachedFromWindow() {
			super.onDetachedFromWindow();
			clearTimer();
		}

		private Bitmap createTextBitmap(String text, boolean antiAlias) {
			Paint paint = new Paint();
			paint.setColor(Color.WHITE);
			Typeface typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);
			paint.setTypeface(typeface);
			paint.setTextSize(14);
			if (antiAlias) {
				paint.setFlags(Paint.ANTI_ALIAS_FLAG);
			}
			Rect rect = new Rect();
			paint.getTextBounds(text, 0, text.length(), rect);
			rect.top = -11;
			rect.bottom = 5;
			rect.right += 48;
			Bitmap bitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(bitmap);
			canvas.drawColor(Color.BLACK);
			canvas.drawText(text, 23, rect.height() - 4, paint);
			int screenHeight = SCREEN_HEIGHT;
			float ratio = screenHeight / rect.height();
			return Bitmap.createScaledBitmap(bitmap, (int) (rect.width() * ratio), screenHeight,
					false);
		}

		private void clearTimer() {
			if (timer != null) {
				timer.cancel();
				timer.purge();
			}
		}

		private void startTimer(int interval) {
			timer = new Timer();
			final Runnable invalidateTask = new Runnable() {
				public void run() {
					invalidate();
				}
			};
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					counter++;
					runOnUiThread(invalidateTask);
				}
			}, 1000, interval);
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		bannerView = new BannerView(this);
		setContentView(bannerView);
		setScreenFlags();
		setBannerText();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(getApplication()).inflate(R.menu.mainmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent intent = new Intent(this, Settings.class);
			startActivityForResult(intent, 1);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			setBannerText();
		}
	}

	private void setBannerText() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String text = sharedPreferences.getString(Settings.TEXT,
				getString(R.string.settings_text_default));
		boolean antialias = sharedPreferences.getBoolean(Settings.ANTIALIAS, false);
		String strSpeed = sharedPreferences.getString(Settings.SPEED, "normal");
		int speed = 0;
		if (strSpeed.equals("fast")) {
			speed = 1;
		} else if (strSpeed.equals("slow")) {
			speed = -1;
		}
		bannerView.setText(text, speed, antialias);
	}

	private void setScreenFlags() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		getWindow().setAttributes(lp);
	}
}