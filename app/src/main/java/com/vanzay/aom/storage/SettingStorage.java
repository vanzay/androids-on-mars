package com.vanzay.aom.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingStorage {
    private static final String SETTINGS_NAME = "com.vanzay.aom.settings";
    private static final String SOUND_ENABLED_KEY = "sound_enabled";
    private static final String FAST_STEPS_ENABLED_KEY = "fast_steps_enabled";

    private static boolean soundEnabled;
    private static boolean fastStepsEnabled;

    public static void loadSettings(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        SettingStorage.soundEnabled = preferences.getBoolean(SOUND_ENABLED_KEY, true);
        SettingStorage.fastStepsEnabled = preferences.getBoolean(FAST_STEPS_ENABLED_KEY, false);
    }

    public static void toggleSoundEnabled() {
        soundEnabled = !soundEnabled;
    }

    public static void toggleFastStepsEnabled() {
        fastStepsEnabled = !fastStepsEnabled;
    }

    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    public static boolean isFastStepsEnabled() {
        return fastStepsEnabled;
    }

    public static void saveSettings(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SOUND_ENABLED_KEY, soundEnabled);
        editor.putBoolean(FAST_STEPS_ENABLED_KEY, fastStepsEnabled);
        editor.apply();
    }
}
