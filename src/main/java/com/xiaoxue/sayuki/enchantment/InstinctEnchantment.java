/**
 * Sayuki — Instinct Enchantment (本能): weapon damage x2
 * Treasure only (cannot be obtained from enchanting table), hidden from enchanted books.
 */
package com.xiaoxue.sayuki.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class InstinctEnchantment extends Enchantment {

    public InstinctEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getMinCost(int level) {
        return 50;
    }

    @Override
    public int getMaxCost(int level) {
        return 100;
    }

    @Override
    public boolean isTreasureOnly() {
        return true; // not obtainable from enchanting table
    }

    @Override
    public boolean isDiscoverable() {
        return false; // hidden from enchanted books / villager trades
    }
}
