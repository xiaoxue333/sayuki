/**
 * Sayuki — Mini Regent (遗物/Curios relic), ISS compat: each mana consumption grants +ATK, idle reset
 */
package com.xiaoxue.sayuki.item;

import com.xiaoxue.sayuki.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class MiniRegent extends Item implements ICurioItem {

    public MiniRegent(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.mini_regent.1"));
        tooltip.add(Component.translatable("tooltip.sayuki.mini_regent.2",
                Config.miniRegentCooldownSeconds));
        tooltip.add(Component.translatable("tooltip.sayuki.mini_regent.3",
                Config.miniRegentIdleSeconds));
    }
}
