package com.vanzay.aom;

import static com.vanzay.aom.images.ImageTools.ALIGN_RIGHT;
import static com.vanzay.aom.images.ImageTools.ALIGN_TOP;
import static com.vanzay.aom.images.Images.NUMBERS;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.vanzay.aom.images.ImageTools;
import com.vanzay.aom.storage.ScoreStorage;

import java.util.List;

public class ScoresActivity extends FullScreenActivity {
    public static final int SCORE_TABLE_FADE_IN_TIME = 1000;
    public static final int SCORE_POS_X = 151;
    public static final int TOP_SCORE_POS_Y = 18;
    public static final int SCORE_POS_Y_STEP = 25;
    public static final int SCORE_POS_X_STEP = 15;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.scores);

        scaleImageView(R.id.splash_background);
        scaleImageView(R.id.splash_title);

        // draw scores
        Bitmap srcImage = BitmapFactory.decodeResource(getResources(), R.drawable.scores);
        Bitmap scoresBitmap = Bitmap.createBitmap(srcImage.getWidth(), srcImage.getHeight(),
                srcImage.getConfig());
        Canvas scoresCanvas = new Canvas(scoresBitmap);
        scoresCanvas.drawBitmap(srcImage, 0, 0, null);

        int y = TOP_SCORE_POS_Y;
        List<Integer> scores = ScoreStorage.getScores(this);
        for (Integer score : scores) {
            drawScore(scoresCanvas, SCORE_POS_X, y, score);
            y += SCORE_POS_Y_STEP;
        }

        // refresh view
        scaleImageView(R.id.scores);
        ImageView scoreTable = findViewById(R.id.scores);
        scoreTable.setImageBitmap(scoresBitmap);
        scoreTable.setVisibility(View.VISIBLE);

        // set fade in animation
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(SCORE_TABLE_FADE_IN_TIME);
        scoreTable.startAnimation(animation);
    }

    private void drawScore(Canvas c, int posX, int posY, int score) {
        int x = posX;
        do {
            int imageId = NUMBERS[score % 10];
            ImageTools.drawOrigSize(c, x, posY, imageId, ALIGN_TOP | ALIGN_RIGHT);
            score /= 10;
            x -= SCORE_POS_X_STEP;
        } while (score != 0);
    }
}
