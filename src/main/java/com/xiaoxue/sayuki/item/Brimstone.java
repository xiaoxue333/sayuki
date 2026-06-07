package com.xiaoxue.sayuki.item;

import com.xiaoxue.sayuki.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class Brimstone extends Item implements ICurioItem {
    public Brimstone(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.brimstone.1"));
        tooltip.add(Component.translatable("tooltip.sayuki.brimstone.2"));
        tooltip.add(Component.translatable("tooltip.sayuki.brimstone.3",
                String.format("%.1f", Config.brimstoneCooldownTicks / 20.0f),
                Config.brimstoneIdleSeconds));
    }
}
