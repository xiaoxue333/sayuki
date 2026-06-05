/**
 * Sayuki — Black Blood (Curios relic slot item, heal 12 HP every 6 seconds)
 */
package com.xiaoxue.sayuki.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class BlackBlood extends Item implements ICurioItem {

    public BlackBlood(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return CuriosApi.getCuriosInventory(slotContext.entity()).resolve()
                .flatMap(handler -> handler.findFirstCurio(
                        s -> s.getItem() == ModItems.BURNING_BLOOD.get()))
                .isEmpty();
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.black_blood.1"));
        tooltip.add(Component.translatable("tooltip.sayuki.black_blood.2"));
    }
}
