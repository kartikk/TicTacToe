package com.kartikk.tictactoe.tasks;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.kartikk.tictactoe.GameActivity;
import com.kartikk.tictactoe.State;
import com.kartikk.tictactoe.sockets.CustomWebSocket;
import com.kartikk.tictactoe.util.Constants;
import com.kartikk.tictactoe.util.Helper;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class PairTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = PairTask.class.getSimpleName();
    WeakReference<Activity> activity;
    String oppUid;
    Intent intent;

    public PairTask(Activity activity) {
        this.activity = new WeakReference<>(activity);
        this.intent = new Intent(activity, GameActivity.class);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            JSONObject jsonObject = CustomWebSocket.dequeue();
            Log.e(TAG, "dequed at pair task" + jsonObject.toString());
            int type = jsonObject.getInt("type");
            oppUid = jsonObject.getString("uid");
            if (type == 0) {
                // Hosting
                CustomWebSocket.enqueue(Helper.constructPairingPayload(oppUid, 1));
                intent.putExtra(Constants.IS_HOSTING_EXTRA_ID, true);
            } else {
                // Joining
                intent.putExtra(Constants.IS_HOSTING_EXTRA_ID, false);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error occured in PairTask", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Activity pairActivity = activity.get();
        if (pairActivity != null) {
            intent.putExtra(Constants.OPP_UID_EXTRA_ID, oppUid);
            intent.putExtra(Constants.MODE_EXTRA_ID, State.TWO_PLAYER_NW_MODE);
            pairActivity.startActivity(intent);
            pairActivity.finish();
        }
    }
}
