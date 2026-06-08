/**
 * Sayuki — Swiftness Enchantment (迅捷): adds +1 projectile bounce per level (max 3)
 * Treasure only, not discoverable, only obtainable via Beautiful Bracelet anvil.
 */
package com.xiaoxue.sayuki.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SwiftnessEnchantment extends Enchantment {

    public SwiftnessEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.VANISHABLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 3;
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
