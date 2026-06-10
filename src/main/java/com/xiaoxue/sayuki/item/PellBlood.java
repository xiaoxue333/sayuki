package com.xiaoxue.sayuki.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class PellBlood extends Item implements ICurioItem {
    public PellBlood(Properties properties) { super(properties); }

    // Bounce +1 handled in ModEventHandler.onProjectileImpact
    // ISS Scarlet spell power applied in ModEventHandler.onCurioChange

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.pell_blood.1"));
        tooltip.add(Component.translatable("tooltip.sayuki.pell_blood.2"));
    }
}
