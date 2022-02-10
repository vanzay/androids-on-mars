package com.vanzay.aom;

import android.app.Activity;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.vanzay.aom.storage.SettingStorage;

import java.util.ArrayList;
import java.util.List;

public class TextMenu implements OnTouchListener {
    private Activity activity;
    private List<TextView> items = new ArrayList<>();

    public TextMenu(Activity activity) {
        this.activity = activity;
    }

    public void addItem(TextView item) {
        item.setOnTouchListener(this);
        items.add(item);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        view.performClick();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ((TextView) view).setTextColor(activity.getResources().getColor(R.color.menu_text_color_selected));
                break;
            case MotionEvent.ACTION_MOVE:
                int color = isPointInView(event.getX(), event.getY(), view)
                        ? R.color.menu_text_color_selected
                        : R.color.menu_text_color;
                ((TextView) view).setTextColor(activity.getResources().getColor(color));
                break;
            case MotionEvent.ACTION_UP:
                if (isPointInView(event.getX(), event.getY(), view)) {
                    ((TextView) view).setTextColor(activity.getResources().getColor(R.color.menu_text_color));
                    handleMenuAction(view.getId());
                }
                break;
        }

        return true;
    }

    private boolean isPointInView(float x, float y, View view) {
        return x >= 0 && x <= view.getWidth() && y >= 0 && y <= view.getHeight();
    }

    private void handleMenuAction(int itemId) {
        // Resource IDs will be non-final in Android Gradle Plugin version 5.0, avoid using them in switch case statements
        if (itemId == R.id.play_menu_item) {
            activity.startActivity(new Intent(activity, GameActivity.class));
            // do it for saving memory
            // activity.finish();
        } else if (itemId == R.id.settings_menu_item) {
            activity.startActivity(new Intent(activity, SettingsMenuActivity.class));
        } else if (itemId == R.id.scores_menu_item) {
            activity.startActivity(new Intent(activity, ScoresActivity.class));
        } else if (itemId == R.id.help_menu_item) {
            activity.startActivity(new Intent(activity, HelpActivity.class));
        } else if (itemId == R.id.sound_menu_item) {
            SettingStorage.toggleSoundEnabled();
            SettingStorage.saveSettings(activity);
            TextView item = (TextView) activity.findViewById(itemId);
            item.setText(SettingStorage.isSoundEnabled() ? R.string.sound_on : R.string.sound_off);
        } else if (itemId == R.id.move_animation_menu_item) {
            SettingStorage.toggleFastStepsEnabled();
            SettingStorage.saveSettings(activity);
            TextView item = (TextView) activity.findViewById(itemId);
            item.setText(SettingStorage.isFastStepsEnabled() ? R.string.fast_steps_on : R.string.fast_steps_off);
        } else if (itemId == R.id.quit_menu_item) {
            activity.setResult(Activity.RESULT_OK, new Intent().setAction(GameActivity.ACTION_QUIT));
            activity.finish();
        }
    }
}
