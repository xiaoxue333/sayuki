/**
 * Sayuki — Power Cell (Curios relic slot item, +2 relic slots, +20% max mana IronSpells compat)
 */
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

public class PowerCell extends Item implements ICurioItem {

    public static final UUID SLOT_MODIFIER_UUID = UUID.fromString("b1c2d3e4-f5a6-7890-bcde-f123456789cd");

    public PowerCell(Properties properties) {
        super(properties);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (!slotContext.entity().level().isClientSide()) {
            Multimap<String, AttributeModifier> modifiers = HashMultimap.create();
            modifiers.put("relic", new AttributeModifier(SLOT_MODIFIER_UUID, "Power Cell bonus",
                    Config.powerCellSlotBonus, AttributeModifier.Operation.ADDITION));
            CuriosApi.getCuriosInventory(slotContext.entity()).ifPresent(
                    handler -> handler.addTransientSlotModifiers(modifiers));
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!slotContext.entity().level().isClientSide()) {
            Multimap<String, AttributeModifier> modifiers = HashMultimap.create();
            modifiers.put("relic", new AttributeModifier(SLOT_MODIFIER_UUID, "Power Cell bonus",
                    Config.powerCellSlotBonus, AttributeModifier.Operation.ADDITION));
            CuriosApi.getCuriosInventory(slotContext.entity()).ifPresent(
                    handler -> handler.removeSlotModifiers(modifiers));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.power_cell.1", Config.powerCellSlotBonus));
        tooltip.add(Component.translatable("tooltip.sayuki.power_cell.2", (int) (Config.powerCellManaPercent * 100)));
    }
}
