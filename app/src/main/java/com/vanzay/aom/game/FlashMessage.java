package com.vanzay.aom.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;

public class FlashMessage {

    public static final int FONT_SIZE = 24;
    public static final int CENTER_X = 160;
    public static final int CENTER_Y = 220;
    public static final int MIN_WIDTH = 150;
    public static final int ALPHA_STEP = 15;

    // fixed
    private float scale;
    private String message;
    // customizable
    private float padding = 5;
    private int backgroundColor = 0xff0000;
    private int borderColor = 0x701010;
    private int textColor = 0xffffff;
    // dynamic
    private int messageAlpha;
    private Paint paint;
    private RectF rect;
    private int posYFix;
    private float centerX;
    private float centerY;

    public FlashMessage(float scale) {
        this.scale = scale;
    }

    public void alert(String message) {
        this.message = message;
        messageAlpha = 255;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        paint.setTextAlign(Paint.Align.CENTER);

        float textSize = scale * FONT_SIZE;
        paint.setTextSize(textSize);

        float width = paint.measureText(message);
        padding = scale * padding;
        float halfWidth = (Math.max(width, scale * MIN_WIDTH)) / 2 + padding;
        float halfHeight = textSize / 2 + padding;
        centerX = scale * CENTER_X;
        centerY = scale * CENTER_Y;
        rect = new RectF(centerX - halfWidth, centerY - halfHeight,
                centerX + halfWidth, centerY + halfHeight);

        posYFix = ((int) (textSize - paint.getFontMetrics().descent)) >> 1;
    }

    public boolean isVisible() {
        return messageAlpha > 0;
    }


    public void draw(Canvas c) {
        if (messageAlpha <= 0) {
            return;
        }

        int colorTransparency = 0xff000000 & (messageAlpha << 24);

        // background
        paint.setColor(backgroundColor | colorTransparency);
        paint.setStyle(Paint.Style.FILL);
        c.drawRoundRect(rect, 6, 6, paint);

        // border
        paint.setColor(borderColor | colorTransparency);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        c.drawRoundRect(rect, 6, 6, paint);

        // text
        paint.setColor(textColor | colorTransparency);
        paint.setStrokeWidth(0);
        c.drawText(message, centerX, centerY + posYFix, paint);

        messageAlpha -= ALPHA_STEP;
    }
}
