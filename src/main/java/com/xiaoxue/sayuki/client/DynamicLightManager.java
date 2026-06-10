package com.xiaoxue.sayuki.client;

/**
 * Volatile state for dynamic light injection into LightEngine on the client side.
 * Updated every tick from the render thread, read by LightEngine (any thread).
 */
public final class DynamicLightManager {
    public static volatile int lightLevel = 0;     // 0 = off, 15 = full
    public static volatile double x, y, z;         // world position of light source

    private DynamicLightManager() {}
}
