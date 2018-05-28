package com.kartikk.tictactoe;

import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kartikk.tictactoe.databinding.ActivityGameBinding;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = GameActivity.class.getSimpleName();
    private ActivityGameBinding binding;
    private State gameState;

    TextView statusTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game);
        statusTV = binding.statusTV;
        gameState = new State(this);
    }

    public void blockClicked(View v) {
        if (!gameState.isGamePaused()) {
            int i = Integer.parseInt(v.getTag(R.id.xCo).toString()), j = Integer.parseInt(v.getTag(R.id.yCo).toString());
            if (gameState.getValue(i, j) == 0) {
                gameState.setValue(i, j);
                gameState.changeTurn(i, j);
            } else {
                statusTV.setText(R.string.wrong_move);
            }
        }
    }

    public void reset(@Nullable View v) {
        gameState.resetState();
        statusTV.setText(R.string.start_tv);
    }

    public void setStatusTV(int resID) {
        statusTV.setText(resID);
    }
}
