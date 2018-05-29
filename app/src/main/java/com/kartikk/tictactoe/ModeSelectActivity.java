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

public class ModeSelectActivity extends AppCompatActivity {

    ActivitySelectModeBinding binding;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_mode);
        setSupportActionBar((Toolbar) binding.toolbar);
        context = this;
    }

    public void modeButtonClicked(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(Constants.MODE_EXTRA_ID, Integer.parseInt(v.getTag().toString()));
        context.startActivity(intent);
    }
}
