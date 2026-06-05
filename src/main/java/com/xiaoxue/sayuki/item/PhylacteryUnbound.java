/**
 * Sayuki — Phylactery Unbound (Curios relic slot item, +5 armor, Goety %-based soul regen)
 * Mutually exclusive with Bound Phylactery.
 * Compat: Goety-2 — non-hard dependency, 2% of max soul energy per second
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

public class PhylacteryUnbound extends Item implements ICurioItem {

    public PhylacteryUnbound(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return CuriosApi.getCuriosInventory(slotContext.entity()).resolve()
                .flatMap(handler -> handler.findFirstCurio(
                        s -> s.getItem() == ModItems.BOUND_PHYLACTERY.get()))
                .isEmpty();
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.phylactery_unbound.1",
                Config.unboundPhylacteryHealthGainIntervalSeconds));
        tooltip.add(Component.translatable("tooltip.sayuki.phylactery_unbound.2"));
    }
}
