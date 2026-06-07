package com.xiaoxue.sayuki.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class RunicPyramid extends Item implements ICurioItem {

    public static final String PKEY_TRACKING = "SayukiRunicPyramidTracking";

    public RunicPyramid(Properties properties) {
        super(properties);
    }

    /**
     * Called when a new effect is applied to the player.
     * If not currently tracking and the effect is beneficial, track it and double its duration.
     * Returns the potentially modified duration (or -1 for infinite = no change).
     */
    public static int onEffectAdded(Player player, MobEffect effect, int duration) {
        if (duration == -1) return -1; // infinite, no change

        // Must be a real beneficial effect, not one of our custom power effects
        if (effect.getCategory() != MobEffectCategory.BENEFICIAL) return duration;
        String key = ForgeRegistries.MOB_EFFECTS.getKey(effect).toString();
        if (key.startsWith("sayuki:")) return duration;

        // If already tracking, don't intervene
        if (!player.getPersistentData().getString(PKEY_TRACKING).isEmpty()) return duration;

        // Track this effect
        player.getPersistentData().putString(PKEY_TRACKING, key);
        return duration * 2;
    }

    /**
     * Called when an effect expires. If it matches the tracked effect, clear tracking.
     */
    public static void onEffectExpired(Player player, MobEffect effect) {
        String key = ForgeRegistries.MOB_EFFECTS.getKey(effect).toString();
        if (key.equals(player.getPersistentData().getString(PKEY_TRACKING))) {
            player.getPersistentData().putString(PKEY_TRACKING, "");
        }
    }

    /**
     * Called on unequip: clear tracking.
     */
    public static void onUnequip(Player player) {
        player.getPersistentData().putString(PKEY_TRACKING, "");
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.runic_pyramid.1"));
        tooltip.add(Component.translatable("tooltip.sayuki.runic_pyramid.2"));
    }
}
