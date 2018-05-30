package com.kartikk.tictactoe.util;

import android.util.Pair;

import com.kartikk.tictactoe.sockets.CustomWebSocket;

import org.json.JSONException;
import org.json.JSONObject;

public class Helper {

    private static CustomWebSocket customWebSocket;

    public static JSONObject constructPayload(Pair<Integer, Integer> pair, String oppUid, int opCode) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject payload = new JSONObject();
            payload.put("x", pair.first);
            payload.put("y", pair.second);
            jsonObject.put("op", opCode);
            jsonObject.put("op_uid", oppUid);
            jsonObject.put("payload", payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject constructPairingPayload(String oppUid, int type) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject payload = new JSONObject();
            payload.put("type", type);
            payload.put("uid", customWebSocket.getUid());
            jsonObject.put("op", 2);
            jsonObject.put("op_uid", oppUid);
            jsonObject.put("payload", payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static  void resetWebSocket() {
        customWebSocket = null;
    }

    public static CustomWebSocket getCustomWebSocket() {
        if(customWebSocket == null) {
            customWebSocket = new CustomWebSocket();
        }
        return customWebSocket;
    }
}
