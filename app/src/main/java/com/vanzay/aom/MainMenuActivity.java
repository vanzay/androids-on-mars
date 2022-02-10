package com.vanzay.aom;

import android.os.Bundle;
import android.view.ViewGroup;

import com.vanzay.aom.sounds.SoundPlayer;

public class MainMenuActivity extends FullScreenActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_menu);

        scaleImageView(R.id.splash_background);
        scaleImageView(R.id.splash_title);

        TextMenu menu = new TextMenu(this);
        menu.addItem(findViewById(R.id.play_menu_item));
        menu.addItem(findViewById(R.id.settings_menu_item));
        menu.addItem(findViewById(R.id.scores_menu_item));
        menu.addItem(findViewById(R.id.help_menu_item));
        menu.addItem(findViewById(R.id.quit_menu_item));
    }

    @Override
    public void onResume() {
        super.onResume();
        ((ViewGroup) findViewById(R.id.main_menu_layout)).startLayoutAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SoundPlayer.unload();
    }
}
