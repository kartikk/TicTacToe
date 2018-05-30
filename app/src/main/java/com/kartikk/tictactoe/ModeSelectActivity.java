package com.kartikk.tictactoe;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.kartikk.tictactoe.databinding.ActivitySelectModeBinding;
import com.kartikk.tictactoe.util.Constants;
import com.kartikk.tictactoe.util.Helper;

public class ModeSelectActivity extends AppCompatActivity {

    ActivitySelectModeBinding binding;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helper.getCustomWebSocket();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_mode);
        context = this;
    }

    public void modeButtonClicked(View v) {
        Intent intent;
        int tag = Integer.parseInt(v.getTag().toString());
        if( tag == State.TWO_PLAYER_NW_MODE) {
            intent = new Intent(this, PairingActivity.class);
        } else {
            intent = new Intent(this, GameActivity.class);
        }
        intent.putExtra(Constants.MODE_EXTRA_ID, tag);
        context.startActivity(intent);
    }
}
