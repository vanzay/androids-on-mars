package com.vanzay.aom.game;

import android.graphics.Canvas;

import com.vanzay.aom.animations.Animation;
import com.vanzay.aom.animations.AnimationData;
import com.vanzay.aom.images.Images;
import com.vanzay.aom.utils.RandomUtils;

import java.util.Arrays;
import java.util.List;

public class FieldCell {

    public enum Type {
        FREE, BLUE, GREEN, RED, GREY, YELLOW, PINK, MULTICOLOR
    }

    public enum State {
        NONE, IDLE, ARRIVE, EYES, SIGNAL, MOVE_UP, MOVE_RIGHT, MOVE_DOWN, MOVE_LEFT
    }

    public static final List<Type> SIMPLE_CELL_TYPES = Arrays.asList(Type.BLUE, Type.GREEN, Type.RED, Type.GREY, Type.YELLOW);
    public static final int SPECIAL_IDLE_ANIMATION_PERIOD = 100;

    private Type type = Type.FREE;
    private State state = State.NONE;
    private Animation animation;
    private boolean appearing;
    private boolean disappearing;
    private boolean highlighted;

    public FieldCell() {
    }

    public FieldCell(Type type) {
        this.type = type;
        setState(State.ARRIVE);
        animation.goToFrame(RandomUtils.random(animation.getFramesCount()));
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
        setState(State.IDLE);
    }

    public void clear() {
        type = Type.FREE;
        state = State.NONE;
        animation = null;
        appearing = false;
        disappearing = false;
        highlighted = false;
    }

    public boolean isFree() {
        return type == Type.FREE;
    }

    public boolean isSimple() {
        return SIMPLE_CELL_TYPES.contains(type);
    }

    public boolean isSpecial() {
        return type == Type.MULTICOLOR;
    }

    public boolean isFrozen() {
        return type == Type.PINK;
    }

    public boolean equals(FieldCell cell) {
        if (cell == null) {
            return false;
        }
        return type == cell.type;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public boolean isAppearing() {
        return appearing;
    }

    public void setAppearing(boolean appearing) {
        this.appearing = appearing;
    }

    public boolean isDisappearing() {
        return disappearing;
    }

    public void setDisappearing(boolean disappearing) {
        this.disappearing = disappearing;
    }

    public void setState(State state) {
        if (this.state == state) {
            return;
        }

        this.state = state;
        String animationName = "ID_" + type.name() + "_" + state.name();
        animation = new Animation(AnimationData.getFrames(animationName), !isSpecialAnimationType(state));
    }

    private boolean isSpecialAnimationType(State state) {
        return state == State.SIGNAL || state == State.EYES;
    }

    public int getIdleImageId() {
        switch (type) {
            case BLUE:
                return Images.ID_ANDROID_BLUE_FRONT;
            case GREEN:
                return Images.ID_ANDROID_GREEN_FRONT;
            case RED:
                return Images.ID_ANDROID_RED_FRONT;
            case GREY:
                return Images.ID_ANDROID_GREY_FRONT;
            case YELLOW:
                return Images.ID_ANDROID_YELLOW_FRONT;
            case MULTICOLOR:
                return Images.ID_ANDROID_MULTICOLOR_FRONT_1;
            case PINK:
                return Images.ID_ANDROID_PINK_FRONT;
        }
        return -1;
    }

    public void tick() {
        if (animation != null) {
            animation.tick();

            switch (state) {
                case IDLE:
                    if (RandomUtils.random(SPECIAL_IDLE_ANIMATION_PERIOD) == 0) {
                        setState(RandomUtils.random(2) == 0
                                ? State.SIGNAL
                                : State.EYES);
                    }
                    break;
                case SIGNAL:
                case EYES:
                    if (animation.isFinished()) {
                        setState(State.IDLE);
                    }
                    break;
            }
        }
    }

    public void draw(Canvas c, int x, int y) {
        if (animation != null) {
            animation.draw(c, x, y);
        }
    }
}
