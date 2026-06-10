package com.xiaoxue.sayuki.item;

import com.xiaoxue.sayuki.compat.TetraCompat;
import com.xiaoxue.sayuki.enchantment.ModEnchantments;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class TriBoomerang extends Item implements ICurioItem {
    public TriBoomerang(Properties properties) {
        super(properties.durability(3).setNoRepair());
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    /**
     * Check if this item can be used as an Instinct enchantment source in the anvil.
     * Valid targets: any vanilla WEAPON-category item, plus Tetra modular weapons.
     */
    public static boolean canApplyInstinct(ItemStack weapon) {
        Item item = weapon.getItem();
        if (EnchantmentCategory.WEAPON.canEnchant(item)) return true;
        return TetraCompat.isMelee(weapon);
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.tri_boomerang.1"));
    }
}
