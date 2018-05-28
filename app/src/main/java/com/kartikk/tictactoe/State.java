package com.kartikk.tictactoe;


import android.app.Activity;
import android.content.Context;
import android.widget.ImageButton;

import java.lang.ref.WeakReference;

public class State {
    // 0 empty, 1 X's turn, 2 O's turn
    private int[][] board;

    private WeakReference<Activity> gameActivity;
    private Context context;

    private static final int score_win = 3;
    private int turn = 1;
    private int turnCount = 0;
    private Boolean gamePaused = false;


    public State(Activity gameActivity) {
        this.board = new int[3][3];
        this.gameActivity = new WeakReference<>(gameActivity);
        this.context = gameActivity.getApplicationContext();
    }

    public int getValue(int x, int y) {
        return board[x][y];
    }

    public void setValue(int x, int y) {
        setValue(x, y, turn);
    }

    /**
     * Sets the board value at the given x,y
     * WARNING!! must be called from the UI thread
     *
     * @param x     x coordinate of the last move
     * @param y     y coordinate of the last move
     * @param value value to be set in the location
     */
    public void setValue(int x, int y, int value) {
        this.board[x][y] = value;
        if (gameActivity.get() != null) {
            int id = context.getResources().getIdentifier("b" + x + y, "id", context.getPackageName());
            if (id != 0) {
                if (value == 0) {
                    ((ImageButton) gameActivity.get().findViewById(id)).setImageDrawable(null);
                } else {
                    ((ImageButton) gameActivity.get().findViewById(id)).setImageResource(value == 1 ? R.drawable.x_vector : R.drawable.circle_vector);
                }
            }
        }
    }

    /**
     * Checks if the last move by the current player, is a winning move in the horizontal or
     * vertical orientations
     *
     * @param x x coordinate of the last move
     * @param y y coordinate of the last move
     * @return true if winning move, false otherwise
     */
    public Boolean winStraight(int x, int y) {
        int score_vert = 0;
        int score_hor = 0;
        for (int i = 0; i < board.length; i++) {
            if (board[i][y] == turn) {
                score_vert++;
            }
            if (board[x][i] == turn) {
                score_hor++;
            }
        }
        return score_vert >= score_win || score_hor >= score_win;
    }

    /**
     * Checks if the last move by the current player, is a winning move in the leading diagonal or
     * counter-diagonal
     *
     * @param x x coordinate of the last move
     * @param y y coordinate of the last move
     * @return true if winning move, false otherwise
     */
    public Boolean winDiag(int x, int y) {
        int score_diag_1 = 0;
        int score_diag_2 = 0;
        if (x == y || board.length - 1 - y == x) {
            for (int i = 0; i < board.length; i++) {
                if (board[i][i] == turn) {
                    score_diag_1++;
                }
                if (board[i][board.length - i - 1] == turn) {
                    score_diag_2++;
                }
            }
        }
        return score_diag_1 >= score_win || score_diag_2 >= score_win;
    }

    /**
     * Changes turn to the next player if game isn't over
     *
     * @param x x coordinate of the last move
     * @param y y coordinate of the last move
     */
    public void changeTurn(int x, int y) {
        GameActivity gameActivity = (GameActivity) this.gameActivity.get();
        if (gameActivity == null) {
            return;
        }
        if (winStraight(x, y) || winDiag(x, y)) {
            // Game won
            gameActivity.setStatusTV(turn == 1 ? R.string.x_win_tv : R.string.o_win_tv);
            gamePaused = true;
        } else {
            // change turn
            if (turn == 1) {
                turn = 2;
                gameActivity.setStatusTV(R.string.o_turn);
            } else {
                turn = 1;
                gameActivity.setStatusTV(R.string.x_turn);
            }
            turnCount++;
            if (turnCount == 9) {
                // Game over, Tie
                gameActivity.setStatusTV(R.string.tie_tv);
                gamePaused = true;
            }
        }
    }

    public Boolean isGamePaused() {
        return gamePaused;
    }

    public void resetState() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                setValue(i, j, 0);
            }
        }
        turn = 1;
        turnCount = 0;
        gamePaused = false;
    }
}