package com.vanzay.aom.animations;

import android.graphics.Canvas;

import com.vanzay.aom.images.ImageTools;

public class Animation {

    enum State {
        PLAYING, PAUSED, FINISHED
    }

    public static final int MILLISECONDS_PER_TICK = 100;

    private final Frame[] frames;
    private final boolean looping;

    private State state = State.PLAYING;
    private int currentFrameIndex;
    private int currentFrameTime;

    public Animation(Frame[] frames) {
        this(frames, false);
    }

    public Animation(Frame[] frames, boolean looping) {
        this.frames = frames;
        this.looping = looping;
    }

    public void reset() {
        state = State.PLAYING;
        currentFrameIndex = 0;
        currentFrameTime = 0;
    }

    public void pause() {
        state = State.PAUSED;
    }

    public void resume() {
        state = State.PLAYING;
    }

    public boolean isFinished() {
        return state == State.FINISHED;
    }

    public int getFramesCount() {
        return frames.length;
    }

    public void goToFrame(int index) {
        if (index >= 0 && index < frames.length) {
            currentFrameIndex = index;
        }
    }

    public void tick() {
        if (state != State.PLAYING) {
            return;
        }

        currentFrameTime += MILLISECONDS_PER_TICK;
        if (currentFrameTime < frames[currentFrameIndex].getDuration()) {
            return;
        }

        if (++currentFrameIndex >= frames.length) {
            if (looping) {
                reset();
            } else {
                state = State.FINISHED;
            }
        } else {
            currentFrameTime = 0;
        }
    }

    public void draw(Canvas c, int x, int y) {
        for (Layer layer : frames[currentFrameIndex].getLayers()) {
            if (layer.isVisible()) {
                ImageTools.draw(c, x + layer.getX(), y + layer.getY(), layer.getImageId(),
                        ImageTools.ALIGN_TOP | ImageTools.ALIGN_LEFT);
            }
        }
    }
}
