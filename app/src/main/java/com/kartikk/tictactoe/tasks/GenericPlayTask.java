package com.kartikk.tictactoe.tasks;

import android.os.AsyncTask;
import android.util.Pair;

import com.kartikk.tictactoe.State;

public abstract class GenericPlayTask extends AsyncTask<Pair<Integer, Integer>, Void, Pair<Integer, Integer>> {
    protected State gameState;

    public GenericPlayTask(State gameState){
        this.gameState = gameState;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        gameState.pauseGame();
    }

    @Override
    protected void onPostExecute(Pair<Integer, Integer> response) {
        super.onPostExecute(response);
        if(response!=null && !gameState.isGameOver()) {
            gameState.setNWState(response.first, response.second);
            gameState.resumeGame();
        }
    }
}
