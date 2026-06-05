/**
 * Sayuki — Heaven Ear Ornaments (Curios ear_ornament slot item)
 * When wearer is attacked, strips attacker AI for 3s,
 * grants Heaven's Door effect to attacker, and applies
 * Silence effect to wearer for 60s cooldown.
 */
package com.xiaoxue.sayuki.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class HeavenEarOrnaments extends Item implements ICurioItem {

    public HeavenEarOrnaments(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.heaven_ear_ornaments.1"));
        tooltip.add(Component.translatable("tooltip.sayuki.heaven_ear_ornaments.2"));
    }
}
