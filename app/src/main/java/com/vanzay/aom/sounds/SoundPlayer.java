package com.vanzay.aom.sounds;

import android.content.Context;
import android.media.MediaPlayer;

import com.vanzay.aom.R;
import com.vanzay.aom.storage.SettingStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SoundPlayer {

    private static Map<Integer, MediaPlayer> players;
    private static boolean playing;

    public static void load(Context context) {
        players = new HashMap<>();
        players.put(R.raw.score, loadSound(context, R.raw.score));
        players.put(R.raw.great_score, loadSound(context, R.raw.great_score));
        players.put(R.raw.game_over, loadSound(context, R.raw.game_over));
    }

    public static void play(int resourceId) {
        if (SettingStorage.isSoundEnabled()
                && !playing
                && players != null
                && players.containsKey(resourceId)) {
            Objects.requireNonNull(players.get(resourceId)).start();
            playing = true;
        }
    }

    public static void unload() {
        if (players == null) {
            return;
        }
        for (MediaPlayer mp : players.values()) {
            if (mp != null) {
                mp.release();
            }
        }
        players = null;
    }

    private static MediaPlayer loadSound(Context context, int resourceId) {
        MediaPlayer player = MediaPlayer.create(context, resourceId);
        player.setLooping(false);
        player.setOnCompletionListener(mp -> playing = false);
        return player;
    }
}
