/*
 * Android FancyGestureDetector
 * Copyright (c) 2009 Bakhtiyor Khodjayev (http://bakhtiyor.com)
 *
 * Version: 0.1
 * Url: http://bakhtiyor.com/2009/05/fancy-gesture-detector/
 *
 * Original Javascript jQuery Version: Anant Garg (http://anantgarg.com/2009/05/21/jquery-fancy-gestures)
 * Original ActionScript Version: Didier Brun (http://www.bytearray.org/?p=91) (didier@bytearray.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bakhtiyor.android.gestures;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.MotionEvent;

public class FancyGestureDetector {

	public interface OnGestureListener {
		public void onGesture(String gesture, float lenght);
	}

	private final static int HIGHEST_SCORE = 100000;
	private final static double SECTOR_RAD = Math.PI * 2 / 8;
	@SuppressWarnings("serial")
	private final static List<Double> ANGLES = new ArrayList<Double>() {
		{
			double step = Math.PI * 2 / 100;
			double sector;
			for (double i = -SECTOR_RAD / 2; i <= Math.PI * 2 - SECTOR_RAD / 2; i += step) {
				sector = Math.floor((i + SECTOR_RAD / 2) / SECTOR_RAD);
				add(sector);
			}
		}
	};

	private final OnGestureListener onGestureListener;
	private final Map<String, int[]> gesturesMap = new HashMap<String, int[]>();
	private final List<Double> moves = new ArrayList<Double>();
	private double lastPositionX;
	private double lastPositionY;
	private double pathLength;

	public FancyGestureDetector(OnGestureListener onGestureListener) {
		this.onGestureListener = onGestureListener;
	}

	public void addGesture(String name, int[] sequence) {
		gesturesMap.put(name, sequence);
	}

	public void addGestures(Map<String, int[]> gestures) {
		gesturesMap.putAll(gestures);
	}

	public int[] getGesture(String name) {
		return gesturesMap.get(name);
	}

	public Map<String, int[]> getGestures() {
		return Collections.<String, int[]> unmodifiableMap(gesturesMap);
	}

	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			setLastPosition(event.getX(), event.getY());
			return true;
		case MotionEvent.ACTION_MOVE:
			addMove(event);
			return true;
		case MotionEvent.ACTION_UP:
			matchGesture();
			resetMoves();
			return true;
		}
		return false;
	}

	public void removeGesture(String name) {
		gesturesMap.remove(name);
	}

	private void addMove(MotionEvent event) {
		double difx = (event.getX() - lastPositionX);
		double dify = (event.getY() - lastPositionY);

		double sqDist = (difx * difx + dify * dify);
		pathLength += Math.sqrt(sqDist);
		double sqPrec = (8 * 8);
		if (sqDist > sqPrec) {
			setLastPosition(event.getX(), event.getY());
			double angle = Math.atan2(dify, difx) + SECTOR_RAD / 2;
			if (angle < 0) {
				angle += Math.PI * 2;
			}
			int index = (int) Math.floor(angle / (Math.PI * 2) * 100);
			moves.add(ANGLES.get(index));
		}
	}

	private double costLeven(int[] a, Double[] b) {
		if (a[0] == -1)
			return b.length == 0 ? 0 : HIGHEST_SCORE;

		double[][] d = new double[a.length + 1][b.length + 1];
		double[][] w = new double[a.length + 1][b.length + 1];

		int x, y;

		for (x = 1; x <= a.length; x++) {
			for (y = 1; y < b.length; y++) {
				d[x][y] = difAngle(a[x - 1], b[y - 1]);
			}
		}

		for (y = 1; y <= b.length; y++) {
			w[0][y] = HIGHEST_SCORE;
		}
		for (x = 1; x <= a.length; x++) {
			w[x][0] = HIGHEST_SCORE;
		}
		w[0][0] = 0;

		double cost, pa, pb, pc;

		for (x = 1; x <= a.length; x++) {
			for (y = 1; y < b.length; y++) {
				cost = d[x][y];
				pa = w[x - 1][y] + cost;
				pb = w[x][y - 1] + cost;
				pc = w[x - 1][y - 1] + cost;
				w[x][y] = Math.min(Math.min(pa, pb), pc);
			}
		}
		return w[x - 1][y - 1];
	}

	private double difAngle(int a, double b) {
		double dif = Math.abs(a - b);
		if (dif > 8 / 2) {
			dif = 8 - dif;
		}
		return dif;
	}

	private void matchGesture() {
		double result = HIGHEST_SCORE;
		double res;
		String gesture = null;
		for (Map.Entry<String, int[]> entry : gesturesMap.entrySet()) {
			int[] matchMove = entry.getValue();
			res = costLeven(matchMove, moves.toArray(new Double[moves.size()]));

			if (res < result && res < 30) {
				result = res;
				gesture = entry.getKey();
			}
		}
		if (gesture != null) {
			onGestureListener.onGesture(gesture, (float) pathLength);
		}
	}

	private void resetMoves() {
		moves.clear();
		setLastPosition(0, 0);
		pathLength = 0;
	}

	private void setLastPosition(float x, float y) {
		lastPositionX = x;
		lastPositionY = y;
	}

	@SuppressWarnings("serial")
	public final static Map<String, int[]> LETTER_GESTURES = Collections
			.<String, int[]> unmodifiableMap(new HashMap<String, int[]>() {
				{
					put("A", new int[] { 5, 3 });
					put("B", new int[] { 2, 6, 0, 1, 2, 3, 4, 0, 1, 2, 3, 4 });
					put("C", new int[] { 4, 3, 2, 1, 0 });
					put("D", new int[] { 2, 6, 7, 0, 1, 2, 3, 4 });
					put("E", new int[] { 4, 3, 2, 1, 0, 4, 3, 2, 1, 0 });
					put("F", new int[] { 4, 2 });
					put("G", new int[] { 4, 3, 2, 1, 0, 7, 6, 5, 0 });
					put("H", new int[] { 2, 6, 7, 0, 1, 2 });
					put("I", new int[] { 6 });
					put("J", new int[] { 2, 3, 4 });
					put("K", new int[] { 3, 4, 5, 6, 7, 0, 1 });
					put("L", new int[] { 4, 6 });
					put("M", new int[] { 6, 1, 7, 2 });
					put("N", new int[] { 6, 1, 6 });
					put("O", new int[] { 4, 3, 2, 1, 0, 7, 6, 5, 4 });
					put("P", new int[] { 6, 7, 0, 1, 2, 3, 4, 5, 6 });
					put("Q", new int[] { 4, 3, 2, 1, 0, 7, 6, 5, 4, 0 });
					put("R", new int[] { 2, 6, 7, 0, 1, 2, 3, 4, 1 });
					put("S", new int[] { 4, 3, 2, 1, 0, 1, 2, 3, 4 });
					put("T", new int[] { 0, 2 });
					put("U", new int[] { 2, 1, 0, 7, 6 });
					put("V", new int[] { 3, 5 });
					put("W", new int[] { 2, 7, 1, 6 });
					put("X", new int[] { 1, 0, 7, 6, 5, 4, 3 });
					put("Y", new int[] { 2, 1, 0, 7, 6, 2, 3, 4, 5, 6, 7 });
					put("Z", new int[] { 0, 3, 0 });
					put(" ", new int[] { 0 });
					put("?", new int[] { 6, 7, 0, 1, 2, 3, 2 });
				}
			});
}
