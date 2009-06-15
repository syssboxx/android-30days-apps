package com.bakhtiyor.android.gps.speedometer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class SpeedometerView extends View {
	private final Paint paint = new Paint();
	private final Path arrow = new Path();
	private double value = 0;
	private int centerX = 0;
	private int centerY = 0;

	public SpeedometerView(Context context) {
		this(context, null);
	}

	public SpeedometerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SpeedometerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		arrow.moveTo(-5, 0);
		arrow.lineTo(0, 90);
		arrow.lineTo(5, 0);
		arrow.lineTo(0, -5);
		arrow.close();

		paint.setAntiAlias(true);
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.FILL);
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		double oldValue = this.value;
		this.value = value;
		if (oldValue != value) {
			invalidate();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.translate(centerX, centerY);
		double v = 0;
		if (value > 0) {
			v += value > 205.0 ? 205.0 : value;
		}
		canvas.rotate(Math.round(v * 1.5) + 30);
		canvas.drawPath(arrow, paint);
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldw, int oldh) {
		super.onSizeChanged(width, height, oldw, oldh);
		centerX = width / 2;
		centerY = height / 2;
	}



}
