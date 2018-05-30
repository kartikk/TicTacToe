package com.kartikk.tictactoe.tasks;

import android.util.Log;
import android.util.Pair;

import com.kartikk.tictactoe.State;
import com.kartikk.tictactoe.sockets.CustomWebSocket;
import com.kartikk.tictactoe.util.Helper;

import org.json.JSONObject;

public class RemotePlayTask extends GenericPlayTask {
    private static final String TAG = RemotePlayTask.class.getSimpleName();
    private String oppUid;

    public RemotePlayTask(State gameState, String oppUid)
    {
        super(gameState);
        this.oppUid = oppUid;
    }

    @Override
    protected Pair<Integer, Integer> doInBackground(Pair<Integer, Integer>... pairs) {
        Pair<Integer, Integer> response = null;
        if(pairs.length > 0) {
            CustomWebSocket.enqueue(Helper.constructPayload(pairs[0], oppUid, 2));
        }

        if(!gameState.isGameOver()) {
            try {
                JSONObject payload = CustomWebSocket.dequeue();
                Log.e("TAG", "dequed at remote play task" + payload.toString());
                response = new Pair<>(payload.getInt("x"), payload.getInt("y"));
            } catch (Exception e) {
                Log.e(TAG, "Exception in remote play", e);
            }
        }
        return response;
    }
}
