package com.kartikk.tictactoe.tasks;

import android.os.AsyncTask;
import android.util.Pair;

import com.kartikk.tictactoe.State;

import java.util.Random;

public class CPUPlayTask extends GenericPlayTask {

    public CPUPlayTask(State gameState){
        super(gameState);
    }

    @Override
    protected Pair<Integer, Integer> doInBackground(Pair<Integer, Integer>... pairs) {
        //TODO: implement min max later
        Random random = new Random();
        int x = random.nextInt(3), y = random.nextInt(3);
        while(gameState.getValue(x, y) != State.BOARD_EMPTY){
            x = random.nextInt(3);
            y = random.nextInt(3);
        }
        return new Pair<>(x, y);
    }
}
