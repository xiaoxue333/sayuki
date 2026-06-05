/**
 * Sayuki — Heart Ear Ornaments (Curios ear_ornament slot item, +max health +attack speed)
 * Compat: Goety-2 and IronsSpellbooks — uses sayuki:ear_ornament slot, no conflict
 */
package com.xiaoxue.sayuki.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class HeartEarOrnaments extends Item implements ICurioItem {

    public HeartEarOrnaments(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.heart_ear_ornaments.1"));
        tooltip.add(Component.translatable("tooltip.sayuki.heart_ear_ornaments.2"));
    }
}
