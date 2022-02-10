package com.vanzay.aom.utils;

import java.util.Random;

public class RandomUtils {
    private static final Random random = new Random();

    public static int random(int value) {
        return Math.abs(random.nextInt() % value);
    }
}
