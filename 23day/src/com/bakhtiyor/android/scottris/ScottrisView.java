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
package com.bakhtiyor.android.scottris;

import tetrisbean.BoardEvent;
import tetrisbean.BoardListener;
import tetrisbean.TetrisBoard;
import tetrisbean.TetrisGame;
import tetrisbean.TetrisPiece;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

public class ScottrisView extends View implements BoardListener {

	private final Paint borderPaint;
	private TetrisBoard fBoard;
	private final TetrisGame fGame = new TetrisGame(14, 20);
	private final Paint pieceLPaint;
	private final Paint pieceJPaint;
	private final Paint pieceIPaint;
	private final Paint pieceZPaint;
	private final Paint pieceSPaint;
	private final Paint pieceOPaint;
	private final Paint pieceTPaint;

	public ScottrisView(Context context) {
		this(context, null);
	}

	public ScottrisView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScottrisView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		fGame.addBoardListener(this);
		borderPaint = new Paint();
		borderPaint.setColor(Color.BLACK);

		pieceLPaint = new Paint();
		pieceJPaint = new Paint();
		pieceIPaint = new Paint();
		pieceZPaint = new Paint();
		pieceSPaint = new Paint();
		pieceOPaint = new Paint();
		pieceTPaint = new Paint();

		pieceLPaint.setColor(Color.rgb(24, 105, 198));
		pieceJPaint.setColor(Color.rgb(206, 56, 173));
		pieceIPaint.setColor(Color.BLUE);
		pieceZPaint.setColor(Color.RED);
		pieceSPaint.setColor(Color.GREEN);
		pieceOPaint.setColor(Color.GRAY);
		pieceTPaint.setColor(Color.YELLOW);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		final int width = getWidth();
		final int height = getHeight();
		if (fBoard != null) {
			final int numCols = fBoard.getColumns();
			final int numRows = fBoard.getRows();

			for (int cols = 0; cols < numCols; cols++) {
				for (int rows = 0; rows < numRows; rows++) {
					final int piece = fBoard.getPieceAt(cols, rows);

					if (piece != TetrisBoard.EMPTY_BLOCK) {
						drawBlock(canvas, getPaint(piece), (cols * width / numCols) + 1, (rows
								* height / numRows) + 1, (width / numCols) - 1,
								(height / numRows) - 1);
					}
				}
			}
		}
	}

	public TetrisGame getGame() {
		return fGame;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			fGame.move(TetrisPiece.LEFT);
			return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			fGame.move(TetrisPiece.RIGHT);
			return true;
		case KeyEvent.KEYCODE_DPAD_UP:
			fGame.move(TetrisPiece.ROTATE);
			return true;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			fGame.move(TetrisPiece.DOWN);
			return true;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			fGame.move(TetrisPiece.FALL);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	private void drawBlock(Canvas canvas, Paint paint, int x, int y, int width, int height) {
		canvas.drawRect(x, y, x + width, y + height, borderPaint);
		canvas.drawRect(x - 1, y - 1, x + width - 1, y + height - 1, paint);
	}

	private Paint getPaint(int piece) {
		Paint result = null;

		switch (piece) {
		case TetrisPiece.L_PIECE:
			result = pieceLPaint;
			break;
		case TetrisPiece.J_PIECE:
			result = pieceJPaint;
			break;
		case TetrisPiece.I_PIECE:
			result = pieceIPaint;
			break;
		case TetrisPiece.Z_PIECE:
			result = pieceZPaint;
			break;
		case TetrisPiece.S_PIECE:
			result = pieceSPaint;
			break;
		case TetrisPiece.O_PIECE:
			result = pieceOPaint;
			break;
		case TetrisPiece.T_PIECE:
			result = pieceTPaint;
			break;
		}
		return result;
	}

	@Override
	public void boardChange(BoardEvent event) {
		fBoard = (TetrisBoard) event.getSource();
		post(new Runnable() {
			public void run() {
				invalidate();
			}
		});
	}
}
