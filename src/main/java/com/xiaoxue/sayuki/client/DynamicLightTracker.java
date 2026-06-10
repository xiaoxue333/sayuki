package com.xiaoxue.sayuki.client;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Client-side tracker for dynamic light sources (players with light-emitting relics).
 * Other players' lights are synced via {@link com.xiaoxue.sayuki.network.LightSyncPacket}.
 */
public class DynamicLightTracker {
    private static final Map<UUID, LightSource> lightSources = new HashMap<>();

    public record LightSource(UUID playerId, int lightLevel) {}

    public static void setLight(Player player, int level) {
        if (level <= 0) {
            lightSources.remove(player.getUUID());
        } else {
            lightSources.put(player.getUUID(), new LightSource(player.getUUID(), level));
        }
    }

    public static void clearLight(Player player) {
        lightSources.remove(player.getUUID());
    }

    public static Map<UUID, LightSource> getLights() {
        return lightSources;
    }

    public static int getMaxLightLevel() {
        int max = 0;
        for (LightSource ls : lightSources.values()) {
            if (ls.lightLevel > max) max = ls.lightLevel;
        }
        return max;
    }

    public static boolean hasAnyLight() {
        return !lightSources.isEmpty();
    }
}
