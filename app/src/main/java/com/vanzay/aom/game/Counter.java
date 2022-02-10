package com.vanzay.aom.game;

public class Counter {

    private int limit;
    private int value;

    public Counter(int limit) {
        this.limit = limit;
        this.value = 0;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void increase() {
        value++;
    }

    public float getProgress() {
        return (float) value / limit;
    }

    public boolean completed() {
        return value >= limit;
    }
}
