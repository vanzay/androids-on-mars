package com.vanzay.aom.animations;

class Frame {
    private int duration;
    private Layer[] layers;

    public Frame(int duration, Layer[] layers) {
        this.duration = duration;
        this.layers = layers;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Layer[] getLayers() {
        return layers;
    }

    public void setLayers(Layer[] layers) {
        this.layers = layers;
    }
}
