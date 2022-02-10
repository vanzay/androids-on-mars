package com.vanzay.aom.game;

public class MoveCounter extends Counter {

    private int shiftX;
    private int shiftY;
    private int stepX;
    private int stepY;

    public MoveCounter(int stepX, int stepY, int steps) {
        super(steps);
        this.stepX = stepX;
        this.stepY = stepY;
    }

    public int getShiftX() {
        return shiftX;
    }

    public void setShiftX(int shiftX) {
        this.shiftX = shiftX;
    }

    public int getShiftY() {
        return shiftY;
    }

    public void setShiftY(int shiftY) {
        this.shiftY = shiftY;
    }

    public void increase() {
        super.increase();
        shiftX += stepX;
        shiftY += stepY;
    }
}
