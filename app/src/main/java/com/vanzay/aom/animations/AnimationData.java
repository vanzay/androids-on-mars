package com.vanzay.aom.animations;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AnimationData {

    private static Frame[][] data;
    private static Map<String, Integer> cache = new HashMap<>();

    public static Frame[] getFrames(int id) {
        return data[id];
    }

    public static Frame[] getFrames(String name) {
        Integer id = null;
        if (cache.containsKey(name)) {
            id = cache.get(name);
        } else {
            try {
                id = Animations.class.getField(name).getInt(null);
                cache.put(name, id);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return id == null ? null : data[id];
    }

    public static void load(Context context) {
        AssetManager asset = context.getAssets();
        try (DataInputStream dis = new DataInputStream(asset.open("anims.adt"))) {
            short count = dis.readShort();
            data = new Frame[count][];
            for (int i = 0; i < count; i++) {
                data[i] = readFrames(dis);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static Frame[] readFrames(DataInputStream dis) throws IOException {
        short count = dis.readShort();
        if (count <= 0) {
            return null;
        }

        Frame[] frames = new Frame[count];
        for (int i = 0; i < count; i++) {
            int duration = dis.readInt();
            Layer[] layers = readLayers(dis);
            frames[i] = new Frame(duration, layers);
        }
        return frames;
    }

    private static Layer[] readLayers(DataInputStream dis) throws IOException {
        byte count = dis.readByte();
        if (count <= 0) {
            return null;
        }

        Layer[] layers = new Layer[count];
        for (int i = 0; i < count; i++) {
            int imageId = dis.readShort();
            int x = dis.readShort();
            int y = dis.readShort();
            layers[i] = new Layer(imageId, x, y);
        }
        return layers;
    }
}
