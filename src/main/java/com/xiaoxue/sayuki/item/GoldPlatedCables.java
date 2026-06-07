/**
 * Sayuki — Gold-Plated Cables (Curios relic slot item, +1 lightning bolt for Cracked/Infused Core)
 */
package com.xiaoxue.sayuki.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class GoldPlatedCables extends Item implements ICurioItem {

    public GoldPlatedCables(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.gold_plated_cables.1"));
        tooltip.add(Component.translatable("tooltip.sayuki.gold_plated_cables.2"));
    }
}
