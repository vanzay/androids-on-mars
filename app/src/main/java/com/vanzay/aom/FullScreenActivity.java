package com.vanzay.aom;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class FullScreenActivity extends Activity {

    private float scale;

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        setScale((float) metrics.widthPixels / 320);   // TODO constant
    }

    public void scaleImageView(int viewId) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        ImageView view = findViewById(viewId);
        view.measure(metrics.widthPixels, metrics.heightPixels);

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.leftMargin = (int) (scale * layoutParams.leftMargin);
        layoutParams.topMargin = (int) (scale * layoutParams.topMargin);
        layoutParams.width = (int) (scale * view.getMeasuredWidth());
        layoutParams.height = (int) (scale * view.getMeasuredHeight());
    }
}
