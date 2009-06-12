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
package com.bakhtiyor.android.snowfall;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

public class SnowFall extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SnowFallView snowFallView = new SnowFallView(this);
		setContentView(snowFallView);
		snowFallView.setBackgroundDrawable(getResources().getDrawable(R.drawable.winter));
	}

	private class SnowFallView extends View {
		private int snow_flake_count = 10;
		private final List<Drawable> drawables = new ArrayList<Drawable>();
		private int[][] coords;
		private final Drawable snow_flake;

		public SnowFallView(Context context) {
			super(context);
			setFocusable(true);
			setFocusableInTouchMode(true);

			snow_flake = context.getResources().getDrawable(R.drawable.snow_flake);
			snow_flake.setBounds(0, 0, snow_flake.getIntrinsicWidth(), snow_flake
			        .getIntrinsicHeight());
		}

		@Override
		protected void onSizeChanged(int width, int height, int oldw, int oldh) {
			super.onSizeChanged(width, height, oldw, oldh);
			Random random = new Random();
			Interpolator interpolator = new LinearInterpolator();

			snow_flake_count = Math.max(width, height) / 20;
			coords = new int[snow_flake_count][];
			drawables.clear();
			for (int i = 0; i < snow_flake_count; i++) {
				Animation animation = new TranslateAnimation(0, height / 10
				        - random.nextInt(height / 5), 0, height + 30);
				animation.setDuration(10 * height + random.nextInt(5 * height));
				animation.setRepeatCount(-1);
				animation.initialize(10, 10, 10, 10);
				animation.setInterpolator(interpolator);

				coords[i] = new int[] { random.nextInt(width - 30), -30 };

				drawables.add(new AnimateDrawable(snow_flake, animation));
				animation.setStartOffset(random.nextInt(20 * height));
				animation.startNow();
			}
		}

		@Override
		protected void onDraw(Canvas canvas) {
			for (int i = 0; i < snow_flake_count; i++) {
				Drawable drawable = drawables.get(i);
				canvas.save();
				canvas.translate(coords[i][0], coords[i][1]);
				drawable.draw(canvas);
				canvas.restore();
			}
			invalidate();
		}

	} 
}