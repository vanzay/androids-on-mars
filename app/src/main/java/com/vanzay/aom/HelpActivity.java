package com.vanzay.aom;

import android.os.Bundle;

public class HelpActivity extends FullScreenActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.help);

        scaleImageView(R.id.splash_background);
    }
}
