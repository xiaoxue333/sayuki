/**
 * Sayuki — Brilliance Enchantment (华彩): relic effect triggers twice
 * Treasure only (cannot be obtained from enchanting table), hidden from enchanted books.
 * Applied to relics via Glitter item on anvil.
 */
package com.xiaoxue.sayuki.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class BrillianceEnchantment extends Enchantment {

    public BrillianceEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.VANISHABLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
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
        return true;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }
}
