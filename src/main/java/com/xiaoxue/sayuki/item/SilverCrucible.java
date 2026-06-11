package com.xiaoxue.sayuki.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class SilverCrucible extends Item implements ICurioItem {
    public static final String PKEY_ACTIVATIONS = "SayukiSilverCrucibleActivations";
    public static final String PKEY_USED_UP = "SayukiSilverCrucibleUsedUp";
    public SilverCrucible(Properties properties) { super(properties); }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.silver_crucible.1"));
    }
}
