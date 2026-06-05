/**
 * Sayuki — Voodoo Ring (Curios ring slot item)
 * Compat: Goety-2 — shares Curios ring slot, OK
 * Compat: IronsSpellbooks — ISSB registers ring slot via IMC (capacity 2), Sayuki via datapack; Curios merges capacities gracefully
 */
package com.xiaoxue.sayuki.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class VoodooRing extends Item implements ICurioItem {

    public VoodooRing(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.voodoo_ring.1"));
    }
}