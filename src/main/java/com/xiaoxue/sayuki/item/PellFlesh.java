package com.xiaoxue.sayuki.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class PellFlesh extends Item implements ICurioItem {
    public PellFlesh(Properties properties) { super(properties); }

    /**
     * Clamp food level to at least 1, preventing starvation death.
     * Reference: Farmer's Delight "Nourishment" effect.
     */
    public static void clampFood(Player player) {
        if (player.getFoodData().getFoodLevel() < 1) {
            player.getFoodData().setFoodLevel(1);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.pell_flesh.1"));
    }
}
