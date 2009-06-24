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
package com.bakhtiyor.android.magic8ball;

import java.util.Random;

import android.app.Activity;
import android.graphics.LinearGradient;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class Magic8Ball extends Activity {
	private final static String[] ANSWERS = new String[] { "Ask Again\nLater",
			"Better Not\nTell You\nNow", "Concentrate\nand Ask\nAgain", "Don't\nCount\non It",
			"It Is\nCertain", "Most\nLikely", "My Reply\nis No", "My Sources\nSay No", "\nNo",
			"Outlook\nGood", "Outlook Not\nSo Good", "Reply Hazy,\nTry Again",
			"Signs Point\nto Yes", "\nYes", "Yes,\nDefinitely", "You May\nRely On\nIt" };
	private TextView predictionView;
	private ShakeDetector shakeDetector;
	private ShakeDetector.ShakeListener shakeListener;
	private View triangleView;
	private Animation triangleAnimation;
	private Animation ballAnimation;
	private View ballView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setScreenFlags();
		ballView = findViewById(R.id.ball);
		triangleView = findViewById(R.id.triangle);
		triangleView.setBackgroundDrawable(getTriangleDrawable());
		predictionView = (TextView) findViewById(R.id.prediction);
		shakeListener = new ShakeDetector.ShakeListener() {
			public void onShakeDetected() {
				animate();
			}
		};
		ballAnimation = AnimationUtils.loadAnimation(this, R.anim.ball);
		triangleAnimation = AnimationUtils.loadAnimation(this, R.anim.triangle);
		triangleAnimation.setStartOffset(1000);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			animate();
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (shakeDetector != null) {
			shakeDetector.shutdown();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		shakeDetector = new ShakeDetector(this, shakeListener);
	}

	private void animate() {
		predictionView.setText(ANSWERS[new Random().nextInt(ANSWERS.length)]);
		if (triangleView.getVisibility() == View.INVISIBLE) {
			triangleView.setVisibility(View.VISIBLE);
		}
		ballView.startAnimation(ballAnimation);
		triangleView.startAnimation(triangleAnimation);
	}

	private Drawable getTriangleDrawable() {
		ShapeDrawable triangle = new ShapeDrawable();
		Path path = new Path();
		path.moveTo(0, 0);
		path.lineTo(200, 0);
		path.lineTo(100, 173);
		path.close();
		triangle.setShape(new PathShape(path, 200, 173));
		triangle.getPaint()
				.setShader(
						new LinearGradient(100, 0, 100, 200, 0xFF000088, 0xFF000000,
								Shader.TileMode.MIRROR));
		triangle.getPaint().setColor(0xFF000000);
		return triangle;
	}

	private void setScreenFlags() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		getWindow().setAttributes(lp);
	}
}