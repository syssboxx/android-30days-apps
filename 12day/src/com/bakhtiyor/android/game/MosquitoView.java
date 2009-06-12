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
package com.bakhtiyor.android.game;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MosquitoView extends SurfaceView implements SurfaceHolder.Callback {
	public static final String TAG = MosquitoView.class.getSimpleName();

	static class MosquitoThread extends Thread {
		class Spot {
			final PointF point;
			final long timestamp;
			Spot(PointF point, long timestamp) {
				this.point = new PointF();
				this.point.set(point);
				this.timestamp = timestamp;
			}
		}

		private final SurfaceHolder surfaceHolder;
		private volatile boolean isRunning = false;
		private volatile boolean hasSlapped = false;
		private long lastTime;
		private final float speed = 300;
		private float flyAngle = 25;
		private PointF lastPoint;
		private final Random angleRandom = new Random();
		private final RectF gameRect = new RectF(1, 1, 640, 960);
		private final RectF screenRect;
		private final Drawable mosquitoRDrawable;
		private final Drawable mosquitoLDrawable;
		private final Drawable splatDrawable;
		private final MediaPlayer noisePlayer;
		private final MediaPlayer slapPlayer;
		private final List<Spot> spots = new CopyOnWriteArrayList<Spot>();
		private long cycle;

		MosquitoThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
			this.surfaceHolder = surfaceHolder;
			screenRect = new RectF(gameRect.centerX() - 320 / 2, gameRect.centerY() - 480 / 2, 320,
					480);
			mosquitoRDrawable = context.getResources().getDrawable(R.drawable.mosquito_r1);
			mosquitoLDrawable = context.getResources().getDrawable(R.drawable.mosquito_l1);
			splatDrawable = context.getResources().getDrawable(R.drawable.splat);
			lastPoint = new PointF(screenRect.centerX(), screenRect.centerY());
			noisePlayer = MediaPlayer.create(context, R.raw.mosquito);
			noisePlayer.setLooping(true);
			slapPlayer = MediaPlayer.create(context, R.raw.slap);
		}

		@Override
		public void run() {
			lastTime = System.currentTimeMillis();
			noisePlayer.start();
			try {
				while (isRunning) {
					Canvas canvas = null;
					try {
						canvas = surfaceHolder.lockCanvas(null);
						synchronized (surfaceHolder) {
							updatePhysics();
							doDraw(canvas);
						}
					} finally {
						if (canvas != null) {
							surfaceHolder.unlockCanvasAndPost(canvas);
						}
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						Log.i(TAG, e.getMessage(), e);
					}
					cycle++;
				}
			} finally {
				release();
			}
		}

		private void setRunning(boolean isRunning) {
			this.isRunning = isRunning;
		}

		private void slap() {
			hasSlapped = true;
			if (!slapPlayer.isPlaying()) {
				slapPlayer.start();
			}
		}

		private void release() {
			if (noisePlayer != null) {
				noisePlayer.release();
			}
			if (slapPlayer != null) {
				slapPlayer.release();
			}
		}

		private void updatePhysics() {
			long now = System.currentTimeMillis();
			if (lastTime > now)
				return;
			float elapsed = (float) ((now - lastTime) / 1000.0);
			float s = speed * elapsed;
			float x = lastPoint.x + ((float) (s * Math.cos(Math.toRadians(flyAngle))));
			float y = lastPoint.y + ((float) (s * Math.sin(Math.toRadians(flyAngle))));

			PointF point = new PointF(x, y);
			point = handleBorders(point);
			updateSpots(now, point);

			if (cycle % 3 == 0) {
				setVolume(point);
			}

			lastPoint = point;
			lastTime = now;
		}

		private PointF handleBorders(PointF point) {
			if (point.x < gameRect.left) {
				point.x = gameRect.left;
				flyAngle = randomAngle();
			} else if (point.x > gameRect.right) {
				point.x = gameRect.right;
				flyAngle = randomAngle();
			}
			if (point.y < gameRect.top) {
				point.y = gameRect.top;
				flyAngle = randomAngle();
			} else if (point.y > gameRect.bottom) {
				point.y = gameRect.bottom;
				flyAngle = randomAngle();
			}
			return point;
		}

		private void updateSpots(long now, PointF point) {
			if (!spots.isEmpty()) {
				for (Spot spot : spots) {
					if (now - spot.timestamp > 3000) {
						spots.remove(spot);
					}
				}
			}
			if (hasSlapped) {
				Spot spot = new Spot(point, now);
				spots.add(spot);
			}
			hasSlapped = false;
		}

		private void setVolume(PointF point) {
			float x = point.x;
			float y = point.y;
			if (noisePlayer != null && noisePlayer.isPlaying()) {
				float deltaCenter = (float) Math.sqrt((screenRect.centerX() - x)
						* (screenRect.centerX() - x) + (screenRect.centerY() - y)
						* (screenRect.centerY() - y));
				float volume = 1 - 1.3f * deltaCenter
						/ Math.max(gameRect.right, gameRect.bottom);
				noisePlayer.setVolume(volume, volume);
			}
		}

		private float randomAngle() {
			float angle = flyAngle + 90 + angleRandom.nextInt(45) - angleRandom.nextInt(45);
			return angle % 360;
		}

		private void doDraw(Canvas canvas) {
			canvas.drawColor(Color.WHITE);

			float dx = lastPoint.x - screenRect.left;
			float dy = lastPoint.y - screenRect.top;
			canvas.save();
			canvas.translate(dx, dy);
			Drawable mosquito = (flyAngle > 90 && flyAngle < 270) ? mosquitoLDrawable
					: mosquitoRDrawable;
			mosquito.setBounds(0, 0, mosquito.getIntrinsicWidth(), mosquito.getIntrinsicHeight());
			mosquito.draw(canvas);
			canvas.restore();

			if (!spots.isEmpty()) {
				splatDrawable.setBounds(0, 0, splatDrawable.getIntrinsicWidth(), splatDrawable
						.getIntrinsicHeight());
				for (Spot spot : spots) {
					canvas.save();
					float x = spot.point.x - screenRect.left;
					float y = spot.point.y - screenRect.top;
					canvas.translate(x, y);
					splatDrawable.draw(canvas);
					canvas.restore();
				}
			}
		}
	}

	private final MosquitoThread mosquitoThread;

	public MosquitoView(Context context) {
		this(context, null);
	}

	public MosquitoView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MosquitoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		mosquitoThread = new MosquitoThread(holder, context, null);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mosquitoThread.setRunning(true);
		mosquitoThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		mosquitoThread.setRunning(false);
		mosquitoThread.interrupt();
		while (retry) {
			try {
				mosquitoThread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}

	public void slap() {
		mosquitoThread.slap();
	}

}
