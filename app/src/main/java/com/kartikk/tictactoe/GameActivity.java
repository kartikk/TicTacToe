package com.kartikk.tictactoe;

import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.kartikk.tictactoe.databinding.ActivityGameBinding;
import com.kartikk.tictactoe.util.Constants;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = GameActivity.class.getSimpleName();
    private ActivityGameBinding binding;
    private State gameState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game);
        setSupportActionBar((Toolbar) binding.toolbar);
        gameState = new State(this);
        if(getIntent().getIntExtra(Constants.MODE_EXTRA_ID, State.TWO_PLAYER_MODE) == State.TWO_PLAYER_NW_MODE){
            binding.restartButton.setVisibility(View.INVISIBLE);
        }
    }

    public void blockClicked(View v) {
        if (!gameState.isGamePaused() && !gameState.isGameOver()) {
            int i = Integer.parseInt(v.getTag(R.id.xCo).toString()), j = Integer.parseInt(v.getTag(R.id.yCo).toString());
            if (gameState.getValue(i, j) == State.BOARD_EMPTY) {
                gameState.toggleTurn(i, j);
            }
            v.setEnabled(false);
        }
    }

    public void reset(@Nullable View v) {
        gameState.resetState();
        binding.statusTV.setText(R.string.start_tv);
    }

    public void setStatusTV(int resID) {
        binding.statusTV.setText(resID);
        binding.statusTV.invalidate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameState.shutdown();
    }

}
