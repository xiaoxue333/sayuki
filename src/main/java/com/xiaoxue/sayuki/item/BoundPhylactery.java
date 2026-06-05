/**
 * Sayuki — Bound Phylactery (Curios relic slot item, +1 armor, Goety soul regen)
 * Compat: Goety-2 — non-hard dependency, soul energy regen when Goety totem/Arca is present
 */
package com.xiaoxue.sayuki.item;

import com.xiaoxue.sayuki.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class BoundPhylactery extends Item implements ICurioItem {

    public BoundPhylactery(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return CuriosApi.getCuriosInventory(slotContext.entity()).resolve()
                .flatMap(handler -> handler.findFirstCurio(
                        s -> s.getItem() == ModItems.PHYLACTERY_UNBOUND.get()))
                .isEmpty();
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.bound_phylactery.1",
                Config.boundPhylacteryHealthGainIntervalSeconds));
        tooltip.add(Component.translatable("tooltip.sayuki.bound_phylactery.2"));
    }
}
