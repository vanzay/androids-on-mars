package com.vanzay.aom;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.vanzay.aom.game.GameFlow;
import com.vanzay.aom.game.GameView;

public class GameActivity extends FullScreenActivity {
    public static final int PAUSE_MENU_REQUEST = 1;
    public static final String ACTION_QUIT = "quit";

    private GameView view;
    private GameFlow flow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        flow = new GameFlow(this);
        flow.startGame();

        view = new GameView(this, flow);
        setContentView(view);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startMainMenuActivity();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            flow.showPauseMenu();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAUSE_MENU_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null && ACTION_QUIT.equals(data.getAction())) {
                    startMainMenuActivity();
                }
            }
            flow.setState(GameFlow.State.ACTIVE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        flow.setState(GameFlow.State.FINISHED);
    }

    private void startMainMenuActivity() {
        // do it in case of closing MainMenuActivity
        //startActivity(new Intent(this, MainMenuActivity.class));
        finish();
    }

    public void invalidate() {
        if (view != null) {
            view.postInvalidate();
        }
    }

    public void alert(String message) {
        view.alert(message);
    }
}
