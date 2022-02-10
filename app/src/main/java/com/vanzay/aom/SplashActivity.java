package com.vanzay.aom;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.vanzay.aom.animations.AnimationData;
import com.vanzay.aom.images.ImageTools;
import com.vanzay.aom.sounds.SoundPlayer;
import com.vanzay.aom.storage.SettingStorage;

public class SplashActivity extends FullScreenActivity {
    public static final int SPLASH_TIME = 2500;
    public static final int SPLASH_FADE_OUT_TIME = 1000;
    private View androidsView = null;
    private boolean resourcesPrepared = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);

        scaleImageView(R.id.splash_background);
        scaleImageView(R.id.splash_title);
        scaleImageView(R.id.splash_androids);

        androidsView = findViewById(R.id.splash_androids);
        androidsView.startAnimation(getFadeOutAnimation());

        new Thread() {
            @Override
            public void run() {
                prepareResources(SplashActivity.this);
                resourcesPrepared = true;
            }
        }.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && resourcesPrepared) {
            startMainMenuActivity();
        }
        return true;
    }

    private Animation getFadeOutAnimation() {
        Animation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setStartOffset(SPLASH_TIME);
        animation.setDuration(SPLASH_FADE_OUT_TIME);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                startMainMenuActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });

        return animation;
    }

    private void startMainMenuActivity() {
        // skip "androids fade out" animation
        androidsView.setVisibility(View.GONE);
        androidsView.setAnimation(null);

        // start next activity
        startActivity(new Intent(this, MainMenuActivity.class));
        finish();
    }

    private static void prepareResources(FullScreenActivity activity) {
        SettingStorage.loadSettings(activity);
        ImageTools.loadImages(activity);
        AnimationData.load(activity);
        SoundPlayer.load(activity);
    }
}
