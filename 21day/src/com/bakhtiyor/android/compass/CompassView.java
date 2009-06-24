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
package com.bakhtiyor.android.compass;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class CompassView extends View {
	private Float degree;
	private final Paint paint = new Paint();
	private final Path arrow = new Path();
	private int cx;
	private int cy;

	public CompassView(Context context) {
		this(context, null);
	}

	public CompassView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CompassView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		arrow.moveTo(0, -70);
		arrow.lineTo(-10, -40);
		arrow.lineTo(-3, -50);
		arrow.lineTo(-3, 20);
		arrow.lineTo(-10, 30);
		arrow.lineTo(-10, 60);
		arrow.lineTo(0, 40);
		arrow.lineTo(10, 60);
		arrow.lineTo(10, 30);
		arrow.lineTo(3, 20);
		arrow.lineTo(3, -50);
		arrow.lineTo(10, -40);
		arrow.close();

		paint.setAntiAlias(true);
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.FILL);

	}

	public Float getDegree() {
		return degree;
	}

	public void setDegree(Float degree) {
		Float oldValue = this.degree;
		this.degree = degree;
		if (oldValue != this.degree) {
			invalidate();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.translate(cx, cy);
		if (degree != null) {
			canvas.rotate(-degree);
		}
		canvas.drawPath(arrow, paint);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		cx = w / 2;
		cy = h / 2;
	}

}
