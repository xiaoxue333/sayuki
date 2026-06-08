/**
 * Sayuki — Goety-2 compat layer (non-hard dependency)
 * Uses reflection to avoid class-loading issues when Goety is not installed.
 */
package com.xiaoxue.sayuki.compat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class GoetyCompat {

    private static final boolean GOETY_LOADED = ModList.get().isLoaded("goety");
    private static Method seHelperIncreaseSouls = null;
    private static Method seHelperGetSEActive = null;
    private static Method seHelperGetSouls = null;
    private static Method totemFinderFindTotem = null;
    private static Method iTotemMaximumSouls = null;
    private static Object maxArcaSoulsConfig = null;
    private static Method configIntGet = null;
    private static Method iOwnedGetTrueOwner = null;

    static {
        if (GOETY_LOADED) {
            try {
                Class<?> seHelper = Class.forName("com.Polarice3.Goety.utils.SEHelper");
                seHelperIncreaseSouls = seHelper.getMethod("increaseSouls", Player.class, int.class);
                seHelperGetSEActive = seHelper.getMethod("getSEActive", Player.class);
                seHelperGetSouls = seHelper.getMethod("getSoulAmountInt", Player.class);

                Class<?> totemFinder = Class.forName("com.Polarice3.Goety.utils.TotemFinder");
                totemFinderFindTotem = totemFinder.getMethod("FindTotem", Player.class);

                Class<?> iTotem = Class.forName("com.Polarice3.Goety.api.items.magic.ITotem");
                iTotemMaximumSouls = iTotem.getMethod("maximumSouls", ItemStack.class);

                // MainConfig.MaxArcaSouls is a ForgeConfigSpec.IntValue
                Class<?> mainConfig = Class.forName("com.Polarice3.Goety.config.MainConfig");
                Field maxArcaSoulsField = mainConfig.getField("MaxArcaSouls");
                maxArcaSoulsConfig = maxArcaSoulsField.get(null);
                if (maxArcaSoulsConfig != null) {
                    configIntGet = maxArcaSoulsConfig.getClass().getMethod("get");
                }
                // IOwned interface — all Goety minions implement this
                try {
                    Class<?> iOwnedClass = Class.forName("com.Polarice3.Goety.api.entities.IOwned");
                    iOwnedGetTrueOwner = iOwnedClass.getMethod("getTrueOwner");
                } catch (Exception ignored2) {
                    // older Goety version
                }
            } catch (Exception e) {
                // Goety loaded but reflection failed — silently degrade
            }
        }
    }

    public static boolean isLoaded() {
        return GOETY_LOADED;
    }

    /** Check if player can store Goety soul energy (has Totem or active Arca). */
    public static boolean canStoreSoulEnergy(Player player) {
        if (!GOETY_LOADED) return false;
        try {
            if (seHelperGetSEActive != null) {
                Boolean active = (Boolean) seHelperGetSEActive.invoke(null, player);
                if (active != null && active) return true;
            }
            // Use TotemFinder.FindTotem (checks Curios + offhand + hotbar)
            if (totemFinderFindTotem != null) {
                ItemStack totem = (ItemStack) totemFinderFindTotem.invoke(null, player);
                if (totem != null && !totem.isEmpty()) return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    /** Get the maximum soul energy capacity for the player. */
    public static int getMaxSoulCapacity(Player player) {
        if (!GOETY_LOADED) return 0;
        try {
            // Arca active → use config value
            if (seHelperGetSEActive != null) {
                Boolean active = (Boolean) seHelperGetSEActive.invoke(null, player);
                if (active != null && active) {
                    if (maxArcaSoulsConfig != null && configIntGet != null) {
                        return (int) configIntGet.invoke(maxArcaSoulsConfig);
                    }
                }
            }
            // Totem → use ITotem.maximumSouls
            if (totemFinderFindTotem != null) {
                ItemStack totem = (ItemStack) totemFinderFindTotem.invoke(null, player);
                if (totem != null && !totem.isEmpty() && iTotemMaximumSouls != null) {
                    return (int) iTotemMaximumSouls.invoke(null, totem);
                }
            }
        } catch (Exception ignored) {}
        return 0;
    }

    /** Add soul energy to player. Returns true on success. */
    public static boolean addSoulEnergy(Player player, int amount) {
        if (!GOETY_LOADED) return false;
        if (!canStoreSoulEnergy(player)) return false;
        try {
            if (seHelperIncreaseSouls != null) {
                seHelperIncreaseSouls.invoke(null, player, amount);
                return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    /** Add soul energy as percentage of max capacity (e.g., 0.02 = 2%). Minimum 1. */
    public static boolean addSoulEnergyPercent(Player player, double ratio) {
        int maxCap = getMaxSoulCapacity(player);
        if (maxCap <= 0) return false;
        int amount = (int) Math.max(1, maxCap * ratio);
        return addSoulEnergy(player, amount);
    }

    /** Get current soul energy. Returns -1 if unavailable. */
    public static int getSouls(Player player) {
        if (!GOETY_LOADED) return -1;
        try {
            if (seHelperGetSouls != null) {
                return (int) seHelperGetSouls.invoke(null, player);
            }
        } catch (Exception ignored) {}
        return -1;
    }

    /** Get the owner of a Goety minion. Returns null if not a Goety minion. */
    public static LivingEntity getMinionOwner(LivingEntity entity) {
        if (!GOETY_LOADED || iOwnedGetTrueOwner == null) return null;
        try {
            if (iOwnedGetTrueOwner.getDeclaringClass().isInstance(entity)) {
                return (LivingEntity) iOwnedGetTrueOwner.invoke(entity);
            }
        } catch (Exception ignored) {}
        return null;
    }

    // ===== Cooldown reset (Brilliant Scarf) =====

    /** Reset all vanilla item cooldowns for the player (covers Goety focuses). */
    public static void resetFocusCooldowns(Player player) {
        try {
            var cooldownsObj = player.getCooldowns();
            var mapField = cooldownsObj.getClass().getDeclaredField("cooldowns");
            mapField.setAccessible(true);
            Object map = mapField.get(cooldownsObj);
            if (map instanceof java.util.Map) {
                ((java.util.Map<?, ?>) map).clear();
            }
        } catch (Exception ignored) {}
    }

    // ===== Focus detection (Beautiful Bracelet) =====

    public static boolean isFocus(net.minecraft.world.item.Item item) {
        String name = item.getClass().getName();
        return name.contains("Focus") && name.startsWith("com.Polarice3.Goety");
    }
}
