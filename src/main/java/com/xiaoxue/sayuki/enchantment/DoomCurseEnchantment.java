package com.xiaoxue.sayuki.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Do-nothing curse enchantment used as a compatibility marker.
 * Each layer of 塔之诅咒 on GuMu stores one instance of this enchantment
 * in the vanilla Enchantments NBT, so external mods (千咒卷轴, 暴戾之咒, etc.)
 * can detect and remove them as curse enchantments without applying any effect.
 */
public class DoomCurseEnchantment extends Enchantment {

    public DoomCurseEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.BREAKABLE, EquipmentSlot.values());
    }

    @Override
    public int getMaxLevel() {
        return 1; // one entry per curse layer
    }

    @Override
    public boolean isCurse() {
        return true;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isDiscoverable() {
        return false; // not obtainable via enchanting table
    }

    @Override
    public boolean isTradeable() {
        return false;
    }
}
