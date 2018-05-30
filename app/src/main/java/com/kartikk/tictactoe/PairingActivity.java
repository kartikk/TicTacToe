package com.kartikk.tictactoe;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.kartikk.tictactoe.databinding.ActivityPairingBinding;
import com.kartikk.tictactoe.sockets.CustomWebSocket;
import com.kartikk.tictactoe.tasks.PairTask;
import com.kartikk.tictactoe.util.Helper;

public class PairingActivity extends AppCompatActivity {

    ActivityPairingBinding binding;
    CustomWebSocket customWebSocket;
    String uid;
    PairTask pairTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pairing);
        setSupportActionBar((Toolbar) binding.toolbar);
        customWebSocket = Helper.getCustomWebSocket();
        uid = customWebSocket.getUid();
        binding.pairingCodeTV.setText(uid);
    }

    @Override
    protected void onStart() {
        super.onStart();
        pairTask = new PairTask(this);
        pairTask.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(pairTask != null){
            pairTask.cancel(true);
        }
        pairTask = new PairTask(this);
        pairTask.execute();
    }

    public void pairButtonClicked(View v) {
        String oppUid = binding.pairingCodeET.getText().toString().trim();
        CustomWebSocket.enqueue(Helper.constructPairingPayload(oppUid, 0));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(pairTask!=null) {
            pairTask.cancel(true);
        }
    }
}
