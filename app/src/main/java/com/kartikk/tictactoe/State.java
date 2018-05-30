package com.kartikk.tictactoe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageButton;

import java.lang.ref.WeakReference;

import com.kartikk.tictactoe.sockets.CustomWebSocket;
import com.kartikk.tictactoe.tasks.CPUPlayTask;
import com.kartikk.tictactoe.tasks.GenericPlayTask;
import com.kartikk.tictactoe.tasks.RemotePlayTask;
import com.kartikk.tictactoe.util.Constants;
import com.kartikk.tictactoe.util.Helper;

public class State {
    // 0 empty, 1 X's turn, 2 O's turn
    private int[][] board;
    public static final int BOARD_EMPTY = 0, X_TURN = 1, O_TURN = 2;

    // 0 two player, 1 two network player, 2 single local player
    private int mode;
    public static final int TWO_PLAYER_MODE = 0, TWO_PLAYER_NW_MODE = 1, SINGLE_PLAYER_MODE = 2;

    private WeakReference<Activity> gameActivity;
    private Context context;

    private static final int score_win = 3;
    private int turn = X_TURN, turnCount = 0;
    private Boolean gamePaused = false, isHost = true, gameOver = false;
    private String oppUid;

    private GenericPlayTask playTask;

    public State(Activity gameActivity) {
        Intent intent = gameActivity.getIntent();
        this.board = new int[3][3];
        this.mode = intent.getIntExtra(Constants.MODE_EXTRA_ID, State.TWO_PLAYER_MODE);
        this.gameActivity = new WeakReference<>(gameActivity);
        this.context = gameActivity.getApplicationContext();

        if (mode == TWO_PLAYER_NW_MODE) {
            Helper.getCustomWebSocket();
            this.oppUid = intent.getStringExtra(Constants.OPP_UID_EXTRA_ID);
            this.isHost = intent.getBooleanExtra(Constants.IS_HOSTING_EXTRA_ID, isHost);
            if (!isHost) {
                turn = O_TURN;
                gamePaused = true;
                playTask = new RemotePlayTask(this, oppUid);
                playTask.execute();
                ((GameActivity)gameActivity).setStatusTV(R.string.opponent_turn);
            }
        }
    }

    public int getValue(int x, int y) {
        return board[x][y];
    }

    public void setValue(int x, int y) {
        setValue(x, y, turn);
    }

    /**
     * Sets the board value at the given x,y. Takes care of updating the UI
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
                ImageButton button = gameActivity.get().findViewById(id);
                if (value == BOARD_EMPTY) {
                    button.setEnabled(true);
                    button.setImageDrawable(null);
                    button.setBackgroundResource(R.color.buttonGrey);
                } else {
                    button.setBackgroundResource(R.color.transparent);
                    button.setImageResource(value == 1 ? R.drawable.x_vector : R.drawable.circle_vector);
                    button.invalidate();
                }
            }
        }

        if (winStraight(x, y, value) || winDiag(x, y, value)) {
            // Game won
            ((GameActivity)gameActivity.get()).setStatusTV(value == X_TURN ? R.string.x_win_tv : R.string.o_win_tv);
            gameOver = true;
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
    public Boolean winStraight(int x, int y, int _turn) {
        int score_vert = 0;
        int score_hor = 0;
        for (int i = 0; i < board.length; i++) {
            if (board[i][y] == _turn) {
                score_vert++;
            }

            if (board[x][i] == _turn) {
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
    public Boolean winDiag(int x, int y, int _turn) {
        int score_diag_1 = 0;
        int score_diag_2 = 0;
        if (x == y || board.length - 1 - y == x) {
            for (int i = 0; i < board.length; i++) {
                if (board[i][i] == _turn) {
                    score_diag_1++;
                }
                if (board[i][board.length - i - 1] == _turn) {
                    score_diag_2++;
                }
            }
        }
        return score_diag_1 >= score_win || score_diag_2 >= score_win;
    }

    /**
     * Completes the current move and sets the state up for the opponent for their move
     *
     * @param x x coordinate of the last move
     * @param y y coordinate of the last move
     */
    public void toggleTurn(int x, int y) {
        GameActivity gameActivity = (GameActivity) this.gameActivity.get();
        if (gameActivity == null || gamePaused || gameOver) return;

        setValue(x, y);

        turnCount++;
        if (turnCount >= 9) {
            // Game over, Tie
            gamePaused = true;
            gameActivity.setStatusTV(R.string.tie_tv);

        } else {
            if(!gameOver) {
                int msg = getComplementTurn() == X_TURN ? R.string.x_turn : R.string.o_turn;
                msg = mode == TWO_PLAYER_NW_MODE ? (R.string.opponent_turn): msg;
                gameActivity.setStatusTV(msg);
            }
            doNextMove(x, y);
        }
    }

    /**
     * Uses an AsyncTask Framework to fetch the opponent's move, either from
     * a remote human player or a local CPU player
     * @param x x coordinate of the last move
     * @param y y coordinate of the last move
     */
    private void doNextMove(int x, int y) {
        switch (mode) {
            case TWO_PLAYER_MODE: {
                turn = getComplementTurn();
                break;
            }
            case TWO_PLAYER_NW_MODE: {
                if(playTask != null) playTask.cancel(true);
                playTask = new RemotePlayTask(this, oppUid);
                playTask.execute(new Pair<>(x, y));
                break;
            }

            case SINGLE_PLAYER_MODE: {

            }
        }
    }

    public void setNWState(final int x, final int y) {
        setValue(x, y, getComplementTurn());
        if(!gameOver) {
            ((GameActivity)gameActivity.get()).setStatusTV(R.string.your_turn);
        }
    }

    public Boolean isGamePaused() {
        return gamePaused;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void resetState() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                setValue(i, j, 0);
            }
        }
        turn = isHost ? X_TURN : O_TURN;
        turnCount = 0;
        gamePaused = false;
        gameOver = false;
    }

    private int getComplementTurn() {
        return turn == X_TURN ? O_TURN : X_TURN;
    }

    public void shutdown() {
        if (mode == TWO_PLAYER_NW_MODE) {
            //customWebSocket.shutdown();
            if(playTask!=null) {
                playTask.cancel(true);
            }
        }
    }

    public void pauseGame(){
        gamePaused = true;
    }

    public void resumeGame() {
        gamePaused = false;
    }
}