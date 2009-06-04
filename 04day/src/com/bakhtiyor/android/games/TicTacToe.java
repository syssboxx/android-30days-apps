/*
 * Copyright (c) 2009 Bakhtiyor Khodjayev (http://www.bakhtiyor.com/)
 * 
 * Original code source 
 * http://voyager.cs.bgsu.edu/gzimmer/cs301/examples/minimaxTicTacToeB.java
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

import java.util.ArrayList;
import java.util.List;

public class TicTacToe {

	public class Board {
		public static final int EMPTY = 0, HUMAN = 1, COMPUTER = 2;
		private final int[][] board;

		Board() {
			board = new int[3][3];
			setUpGame();
		}

		Board(Board b) {
			board = new int[3][3];
			for (int row = 0; row < 3; row++) {
				for (int col = 0; col < 3; col++) {
					board[row][col] = b.board[row][col];
				}
			}
		}

		public int[][] getData() {
			return board;
		}

		Move[] getLegalMoves(Board aboard) {
			List<Move> lmoves = new ArrayList<Move>();
			for (int col = 0; col <= 2; col++) {
				for (int row = 0; row <= 2; row++) {
					if (aboard.board[row][col] == EMPTY) {
						lmoves.add(new Move(row, col, 0));
						// System.out.println("M "+row+" "+col);
					}

				}
			}
			Move[] theList = new Move[lmoves.size()];
			for (int i = 0; i < lmoves.size(); i++) {
				theList[i] = lmoves.get(i);
			}

			return theList;
		}

		int pieceAt(int row, int col) {
			return board[row][col];
		}

		void setPieceAt(int row, int col, int piece) {
			board[row][col] = piece;
		}

		void setUpGame() {
			for (int row = 0; row < 3; row++) {
				for (int col = 0; col < 3; col++) {
					board[row][col] = EMPTY;
				}
			}

		}

		boolean winner(int player) {
			for (int row = 0; row <= 2; row++)
				if ((board[row][0] == player && board[row][1] == player && board[row][2] == player))
					return true;
			for (int col = 0; col <= 2; col++)
				if ((board[0][col] == player && board[1][col] == player && board[2][col] == player))
					return true;
			if ((board[0][0] == player && board[1][1] == player && board[2][2] == player))
				return true;
			if ((board[2][0] == player && board[1][1] == player && board[0][2] == player))
				return true;

			return false;
		}
		
		boolean isTied() {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (board[i][j] == 0) {
						return false;
					}
				}
			}
			return true;
		}
	}

	class Move {
		public int row, col;
		public int mmscore;

		Move(int r, int c, int mms) {
			row = r;
			col = c;
			mmscore = mms;
		}
	}


	Board board;
	private boolean inProgress;

	public TicTacToe() {
		board = new Board();
		inProgress = true;
	}

	public void computerMove() {
		Board bcopy;
		Move cMove;

		bcopy = new Board(board);
		cMove = cminimax(bcopy, 3, Board.COMPUTER);
		board.setPieceAt(cMove.row, cMove.col, 2);

	}

	public Board getBoard() {
		return board;
	}

	public void humanMove(int row, int col) {
		if (board.pieceAt(row, col) == Board.EMPTY && inProgress
				&& !board.isTied()) {
			board.setPieceAt(row, col, Board.HUMAN);
			if (board.winner(Board.HUMAN)) {
				inProgress = false;
			} else {
				if (board.isTied())
					return;
				computerMove();
				if (board.winner(Board.COMPUTER)) {
					inProgress = false;
				}
			}
		}

	}

	public boolean isInProgress() {
		return inProgress;
	}

	public void newGame() {
		board = new Board();
		inProgress = true;
	}


	/**
	 * the classic minimax algorithm
	 *
	 * @param curboard
	 *            the current board configuration
	 * @param depth
	 *            the maximum recursion depth
	 * @param player
	 *            the player whose turn is being processed in this invocation
	 * @return
	 */
	private Move cminimax(Board curboard, int depth, int player) {
		Move[] possMoves = curboard.getLegalMoves(curboard);
		Board tboard;
		Move tMove;
		int mmscore, pos;
		if ((depth == 0) || (possMoves.length == 0) || curboard.winner(1) || curboard.winner(2)) {
			int score = staticValue(curboard);
			return (new Move(0, 0, score));
		} else {
			int otherPlayer;
			if (player == Board.HUMAN) {
				otherPlayer = Board.COMPUTER;
			} else {
				otherPlayer = Board.HUMAN;
			}
			for (int i = 0; i < possMoves.length; i++) {
				tboard = new Board(curboard);
				tboard.setPieceAt(possMoves[i].row, possMoves[i].col, player);
				tMove = cminimax(tboard, depth - 1, otherPlayer);
				possMoves[i].mmscore = tMove.mmscore;
			}
			mmscore = possMoves[0].mmscore;
			pos = 0;
			for (int i = 1; i < possMoves.length; i++) {
				if (player == Board.COMPUTER && mmscore < possMoves[i].mmscore) {
					mmscore = possMoves[i].mmscore;
					pos = i;
				} else if (player == Board.HUMAN && mmscore > possMoves[i].mmscore) {
					mmscore = possMoves[i].mmscore;
					pos = i;
				}
			}

		}
		return (possMoves[pos]);
	}

	/**
	 *
	 * @param curboard
	 *            the current board to be evaluated
	 * @return
	 */
	private int staticValue(Board curboard) { //
		int score = 0;

		// human -
		if (curboard.winner(Board.HUMAN)) {
			score = -10000;
			return score;
		}
		if (curboard.pieceAt(1, 1) == Board.HUMAN) {
			score -= 25;
		}
		if (curboard.pieceAt(0, 0) == Board.HUMAN) {
			score -= 10;
		}
		if (curboard.pieceAt(0, 2) == Board.HUMAN) {
			score -= 10;
		}
		if (curboard.pieceAt(2, 0) == Board.HUMAN) {
			score -= 10;
		}
		if (curboard.pieceAt(2, 2) == Board.HUMAN) {
			score -= 10;
		}

		if (curboard.winner(Board.COMPUTER)) {
			score = 10000;
			return score;
		}
		if (curboard.pieceAt(1, 1) == Board.COMPUTER) {
			score += 25;
		}
		if (curboard.pieceAt(0, 0) == Board.COMPUTER) {
			score += 10;
		}
		if (curboard.pieceAt(0, 2) == Board.COMPUTER) {
			score += 10;
		}
		if (curboard.pieceAt(2, 0) == Board.COMPUTER) {
			score += 10;
		}
		if (curboard.pieceAt(2, 2) == Board.COMPUTER) {
			score += 10;
		}

		return score;
	}
}
