package com.kartikk.tictactoe.sockets;

import android.util.Log;

import com.kartikk.tictactoe.util.Constants;
import com.kartikk.tictactoe.util.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class CustomWebSocket extends WebSocketListener {

    private static final String TAG = CustomWebSocket.class.getSimpleName();
    private static WebSocket webSocket;
    public static String uid = null;

    private static LinkedBlockingQueue<JSONObject> sendQueue, receiveQueue;

    ExecutorService s = Executors.newFixedThreadPool(1);

    public CustomWebSocket() {
        OkHttpClient okHttpClient = new OkHttpClient();
        sendQueue = new LinkedBlockingQueue<>();
        receiveQueue = new LinkedBlockingQueue<>();

        Request request = new Request.Builder().url(Constants.HEROKU_ENDPOINT).build();
        webSocket = okHttpClient.newWebSocket(request, this);
        okHttpClient.dispatcher().executorService().shutdown();

        s.execute(new Runnable() {
            @Override
            public void run() {
                while (!s.isShutdown()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = sendQueue.take();
                        webSocket.send(jsonObject.toString());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static void enqueue(JSONObject jsonObject) {
        Log.d(TAG, "Sending "+ jsonObject.toString());
        try {
            jsonObject.put("uid", uid);
            sendQueue.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject dequeue() throws  Exception{
        return receiveQueue.take();
    }


    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        webSocket.send(Constants.REGISTER_PAYLOAD);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d(TAG, "receiving: "+ text);
        if(uid==null) {
            uid = text;
        } else {
            try {
                receiveQueue.add(new JSONObject(text));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        super.onMessage(webSocket, text);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
        uid = null;
        Helper.resetWebSocket();
    }

    public void shutdown(){
        s.shutdown();
        webSocket.close(1000, null);
    }

    public String getUid() {
        return uid;
    }
}
