package com.vanzay.aom.animations;

class Layer {
    private int imageId;
    private int x;
    private int y;
    private boolean visible = true;

    public Layer(int imageId, int x, int y) {
        this.imageId = imageId;
        this.x = x;
        this.y = y;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
