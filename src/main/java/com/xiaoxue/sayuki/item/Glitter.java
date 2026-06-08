package com.xiaoxue.sayuki.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class Glitter extends Item implements ICurioItem {
    public Glitter(Properties properties) {
        super(properties);
    }

    /**
     * Check if this item can apply the Brilliance enchantment to a relic item via anvil.
     */
    public static boolean canApplyBrilliance(ItemStack stack) {
        return stack.getItem() instanceof ICurioItem;
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.glitter.1"));
    }
}
