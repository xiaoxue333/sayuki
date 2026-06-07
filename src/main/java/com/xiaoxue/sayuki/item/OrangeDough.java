package com.xiaoxue.sayuki.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.xiaoxue.sayuki.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class OrangeDough extends Item implements ICurioItem {

    private static final UUID SLOT_MODIFIER_UUID = UUID.fromString("c5d6e7f8-a9b0-1234-cdef-0123456789ab");

    public OrangeDough(Properties properties) {
        super(properties);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (!slotContext.entity().level().isClientSide()) {
            Multimap<String, AttributeModifier> modifiers = HashMultimap.create();
            modifiers.put("relic", new AttributeModifier(SLOT_MODIFIER_UUID, "Orange Dough bonus",
                    Config.ORANGE_DOUGH_SLOT_BONUS, AttributeModifier.Operation.ADDITION));
            CuriosApi.getCuriosInventory(slotContext.entity()).ifPresent(
                    handler -> handler.addTransientSlotModifiers(modifiers));
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!slotContext.entity().level().isClientSide()) {
            Multimap<String, AttributeModifier> modifiers = HashMultimap.create();
            modifiers.put("relic", new AttributeModifier(SLOT_MODIFIER_UUID, "Orange Dough bonus",
                    Config.ORANGE_DOUGH_SLOT_BONUS, AttributeModifier.Operation.ADDITION));
            CuriosApi.getCuriosInventory(slotContext.entity()).ifPresent(
                    handler -> handler.removeSlotModifiers(modifiers));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.orange_dough.1"));
    }
}
