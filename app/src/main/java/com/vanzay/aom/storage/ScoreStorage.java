package com.vanzay.aom.storage;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreStorage {
    private static final String SCORES_NAME = "com.vanzay.aom.scores";
    private static final String SCORE_KEY = "score";
    private static final int SCORES_COUNT = 7;

    public static List<Integer> getScores(Context context) {
        List<Integer> scores = new ArrayList<>(SCORES_COUNT);
        SharedPreferences preferences = context.getSharedPreferences(SCORES_NAME, Context.MODE_PRIVATE);
        for (int i = 0; i < SCORES_COUNT; i++) {
            scores.add(preferences.getInt(SCORE_KEY + i, 0));
        }
        return scores;
    }

    public static void saveScore(Context context, int value) {
        List<Integer> scores = getScores(context);
        if (value > scores.get(SCORES_COUNT - 1)) {
            scores.add(value);
            Collections.sort(scores, Collections.reverseOrder());

            SharedPreferences settings = context.getSharedPreferences(SCORES_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            for (int i = 0; i < SCORES_COUNT; i++) {
                editor.putInt(SCORE_KEY + i, scores.get(i));
            }
            editor.apply();
        }
    }
}
