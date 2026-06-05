/**
 * Sayuki — Regalite (遗物/Curios relic), equipping grants Forged Sword netherite-pickaxe mining +1 XP per forge.
 */
package com.xiaoxue.sayuki.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class Regalite extends Item implements ICurioItem {

    public Regalite(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.regalite.1"));
        tooltip.add(Component.translatable("tooltip.sayuki.regalite.2"));
    }
}
