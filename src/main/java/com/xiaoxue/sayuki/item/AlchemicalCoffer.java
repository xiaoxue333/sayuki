package com.xiaoxue.sayuki.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.*;

public class AlchemicalCoffer extends Item implements ICurioItem {

    private static final UUID SLOT_MODIFIER_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private static final int SLOT_BONUS = 4;

    public AlchemicalCoffer(Properties properties) { super(properties); }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (slotContext.entity().level().isClientSide()) return;
        if (!(slotContext.entity() instanceof Player player)) return;

        // +4 relic slots
        Multimap<String, AttributeModifier> modifiers = HashMultimap.create();
        modifiers.put("relic", new AttributeModifier(SLOT_MODIFIER_UUID, "Alchemical Coffer bonus",
                SLOT_BONUS, AttributeModifier.Operation.ADDITION));
        CuriosApi.getCuriosInventory(player).ifPresent(
                handler -> handler.addTransientSlotModifiers(modifiers));

        // 4 random beneficial potion effects for 30s
        List<MobEffect> beneficial = new ArrayList<>();
        for (MobEffect effect : ForgeRegistries.MOB_EFFECTS) {
            if (effect.isBeneficial()) beneficial.add(effect);
        }
        if (beneficial.isEmpty()) return;
        Random rand = new Random();
        Collections.shuffle(beneficial, rand);
        for (int i = 0; i < Math.min(4, beneficial.size()); i++) {
            player.addEffect(new MobEffectInstance(beneficial.get(i), 30 * 20, 0));
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!slotContext.entity().level().isClientSide()) {
            Multimap<String, AttributeModifier> modifiers = HashMultimap.create();
            modifiers.put("relic", new AttributeModifier(SLOT_MODIFIER_UUID, "Alchemical Coffer bonus",
                    SLOT_BONUS, AttributeModifier.Operation.ADDITION));
            CuriosApi.getCuriosInventory(slotContext.entity()).ifPresent(
                    handler -> handler.removeSlotModifiers(modifiers));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.alchemical_coffer.1"));
        tooltip.add(Component.translatable("tooltip.sayuki.alchemical_coffer.2"));
    }
}
