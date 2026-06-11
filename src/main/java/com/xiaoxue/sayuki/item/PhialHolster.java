package com.xiaoxue.sayuki.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
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

public class PhialHolster extends Item implements ICurioItem {
    public static final int EFFECT_CAP_BONUS = 1;
    private static final UUID SLOT_MODIFIER_UUID = UUID.fromString("e1f2a3b4-c5d6-7890-abcd-ef1234567890");
    private static final int SLOT_BONUS = 1;

    public PhialHolster(Properties properties) { super(properties); }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (slotContext.entity().level().isClientSide()) return;
        Multimap<String, AttributeModifier> modifiers = HashMultimap.create();
        modifiers.put("relic", new AttributeModifier(SLOT_MODIFIER_UUID, "Phial Holster bonus",
                SLOT_BONUS, AttributeModifier.Operation.ADDITION));
        CuriosApi.getCuriosInventory(slotContext.entity()).ifPresent(
                handler -> handler.addTransientSlotModifiers(modifiers));
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!slotContext.entity().level().isClientSide()) {
            Multimap<String, AttributeModifier> modifiers = HashMultimap.create();
            modifiers.put("relic", new AttributeModifier(SLOT_MODIFIER_UUID, "Phial Holster bonus",
                    SLOT_BONUS, AttributeModifier.Operation.ADDITION));
            CuriosApi.getCuriosInventory(slotContext.entity()).ifPresent(
                    handler -> handler.removeSlotModifiers(modifiers));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.phial_holster.1"));
    }
}
