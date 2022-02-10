package com.vanzay.aom;

import android.os.Bundle;
import android.widget.TextView;

import com.vanzay.aom.storage.SettingStorage;

public class PauseMenuActivity extends FullScreenActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pause_menu);

        TextMenu menu = new TextMenu(this);

        TextView soundMenuItem = findViewById(R.id.sound_menu_item);
        soundMenuItem.setText(SettingStorage.isSoundEnabled() ? R.string.sound_on : R.string.sound_off);
        menu.addItem(soundMenuItem);

        TextView moveAnimationMenuItem = findViewById(R.id.move_animation_menu_item);
        moveAnimationMenuItem.setText(SettingStorage.isFastStepsEnabled() ? R.string.fast_steps_on : R.string.fast_steps_off);
        menu.addItem(moveAnimationMenuItem);

        menu.addItem(findViewById(R.id.quit_menu_item));
    }
}
