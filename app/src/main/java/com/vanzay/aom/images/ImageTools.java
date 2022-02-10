package com.vanzay.aom.images;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;

import com.vanzay.aom.FullScreenActivity;

import java.io.DataInputStream;

public class ImageTools {

    static class Info {
        short imageID;
        Rect clip;
        Rect origSize;
        Rect scaledSize;
    }

    public static final int ALIGN_TOP = 1;
    public static final int ALIGN_BOTTOM = 1 << 1;
    public static final int ALIGN_VERTICAL = 1 << 2;
    public static final int ALIGN_LEFT = 1 << 3;
    public static final int ALIGN_RIGHT = 1 << 4;
    public static final int ALIGN_HORIZONTAL = 1 << 5;

    private static float scale;
    private static Bitmap[] image;
    private static Info[] index;

    public static void loadImages(FullScreenActivity context) {
        scale = context.getScale();

        AssetManager asset = context.getAssets();
        try (DataInputStream dis = new DataInputStream(asset.open("g.idt"))) {
            byte totalGroups = dis.readByte();
            short totalImages = dis.readShort();

            image = new Bitmap[totalGroups];
            index = new Info[totalImages];

            int current = 0;
            for (int group = 0; group < totalGroups; group++) {
                image[group] = BitmapFactory.decodeStream(asset.open("g" + group + ".png"));

                short images = dis.readShort();
                for (int i = 0; i < images; i++) {
                    index[current] = new Info();
                    index[current].imageID = (short) group;
                    short x = dis.readShort();
                    short y = dis.readShort();
                    short width = dis.readShort();
                    short height = dis.readShort();
                    index[current].clip = new Rect(x, y, x + width, y + height);
                    index[current].origSize = new Rect(0, 0, width, height);
                    index[current].scaledSize = new Rect(0, 0, (int) (scale * width), (int) (scale * height));
                    current++;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void drawOrigSize(Canvas c, float x, float y, int id, int align) {
        if ((id < 0) || (id >= index.length)) {
            return;
        }

        Info info = index[id];

        x = alignX(x, info.origSize.right, align);
        y = alignY(y, info.origSize.bottom, align);

        c.save();
        c.translate(x, y);
        c.drawBitmap(image[info.imageID], info.clip, info.origSize, null);
        c.restore();
    }

    public static void draw(Canvas c, float x, float y, int id, int align) {
        if ((id < 0) || (id >= index.length)) {
            return;
        }

        Info info = index[id];

        x = alignX(scale * x, info.scaledSize.right, align);
        y = alignY(scale * y, info.scaledSize.bottom, align);

        c.save();
        c.translate(x, y);
        c.drawBitmap(image[info.imageID], info.clip, info.scaledSize, null);
        c.restore();
    }

    public static void drawRounded(Canvas c, float x, float y, float radius, int id, int align) {
        if ((id < 0) || (id >= index.length)) {
            return;
        }

        Info info = index[id];

        x = alignX(scale * x, info.scaledSize.right, align);
        y = alignY(scale * y, info.scaledSize.bottom, align);

        c.save();
        c.translate(x, y);
        Path path = new Path();
        path.addCircle(info.scaledSize.right / 2.f, info.scaledSize.bottom / 2.f, scale * radius, Path.Direction.CCW);
        c.clipPath(path);
        c.drawBitmap(image[info.imageID], info.clip, info.scaledSize, null);
        c.restore();
    }

    private static float alignX(float x, float width, int align) {
        if ((align & ALIGN_RIGHT) != 0) {
            x -= width;
        }
        if ((align & ALIGN_HORIZONTAL) != 0) {
            x -= width / 2;
        }
        return x;
    }

    private static float alignY(float y, float height, int align) {
        if ((align & ALIGN_BOTTOM) != 0) {
            y -= height;
        }
        if ((align & ALIGN_VERTICAL) != 0) {
            y -= height / 2;
        }
        return y;
    }
}
