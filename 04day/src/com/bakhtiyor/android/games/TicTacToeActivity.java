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
package com.bakhtiyor.android.games;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.bakhtiyor.android.games.TicTacToe.Board;

public class TicTacToeActivity extends Activity {
	private static final int BOARD_SIZE = 3;
	private final ImageView[][] views = new ImageView[BOARD_SIZE][BOARD_SIZE];
	private Drawable computerDrawable;
	private Drawable humanDrawable;
	private final TicTacToe game = new TicTacToe();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		int[][] ids = new int[][] { { R.id.cell1, R.id.cell2, R.id.cell3 },
				{ R.id.cell4, R.id.cell5, R.id.cell6 },
				{ R.id.cell7, R.id.cell8, R.id.cell9 } };
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				views[i][j] = (ImageView) findViewById(ids[i][j]);
				final int fi = i, fj = j;
				views[i][j].setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						humanMove(fi, fj);
						return true;
					}
				});
			}
		}

		computerDrawable = getResources().getDrawable(R.drawable.o);
		humanDrawable = getResources().getDrawable(R.drawable.x);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.new_game);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item)
				|| optionsMenuItemSelected(item);
	}

	private boolean optionsMenuItemSelected(MenuItem menuItem) {
		boolean result = true;
		switch (menuItem.getItemId()) {
		case 0:
			newGame();
			break;
		default:
			result = false;
			break;
		}
		return result;
	}

	private void newGame() {
		game.newGame();
		updateViews();
		setTitle(R.string.app_name);
	}

	private void humanMove(int row, int col) {
		if (game.isInProgress()) {
			game.humanMove(row, col);
			if (game.board.winner(Board.HUMAN)) {
				Toast.makeText(this, R.string.win_msg,
						Toast.LENGTH_SHORT).show();
				setTitle(String.format("%s - %s", getString(R.string.app_name),
						getString(R.string.win_msg)));
			}
			if (game.board.winner(Board.COMPUTER)) {
				Toast.makeText(this, R.string.lose_msg, Toast.LENGTH_SHORT)
						.show();
				setTitle(String.format("%s - %s", getString(R.string.app_name),
						getString(R.string.lose_msg)));
			}
			updateViews();
		}
	}

	private void updateViews() {
		updateViews(game.board.getData());
	}

	private void updateViews(int[][] data) {
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				ImageView view = views[i][j];
				int state = data[i][j];
				switch (state) {
				case TicTacToe.Board.COMPUTER:
					view.setImageDrawable(computerDrawable);
					break;
				case TicTacToe.Board.HUMAN:
					view.setImageDrawable(humanDrawable);
					break;
				default:
					view.setImageDrawable(null);
					break;
				}
			}
		}
	}
}