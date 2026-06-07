package com.xiaoxue.sayuki.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.*;

public class CallingBell extends Item implements ICurioItem {

    private static final UUID SLOT_MODIFIER_UUID = UUID.fromString("f1a2b3c4-d5e6-7890-abcd-ef1234567890");
    private static final int SLOT_BONUS = 3;
    private static final String TAG_ENCHANTED = "SayukiCallingBellEnchanted";

    public CallingBell(Properties properties) {
        super(properties);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (!slotContext.entity().level().isClientSide()) {
            Multimap<String, AttributeModifier> modifiers = HashMultimap.create();
            modifiers.put("relic", new AttributeModifier(SLOT_MODIFIER_UUID, "Calling Bell bonus",
                    SLOT_BONUS, AttributeModifier.Operation.ADDITION));
            CuriosApi.getCuriosInventory(slotContext.entity()).ifPresent(
                    handler -> handler.addTransientSlotModifiers(modifiers));
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!slotContext.entity().level().isClientSide()) {
            Multimap<String, AttributeModifier> modifiers = HashMultimap.create();
            modifiers.put("relic", new AttributeModifier(SLOT_MODIFIER_UUID, "Calling Bell bonus",
                    SLOT_BONUS, AttributeModifier.Operation.ADDITION));
            CuriosApi.getCuriosInventory(slotContext.entity()).ifPresent(
                    handler -> handler.removeSlotModifiers(modifiers));
        }
    }

    /**
     * Apply all binding-type enchantments found in the modpack to this stack.
     * Called once — uses a tag marker to avoid re-applying.
     */
    public static void applyBindingEnchantments(ItemStack stack) {
        if (stack.isEmpty() || stack.getItem() != ModItems.CALLING_BELL.get()) return;
        if (stack.getOrCreateTag().getBoolean(TAG_ENCHANTED)) return;

        Map<Enchantment, Integer> all = new HashMap<>(stack.getAllEnchantments());
        for (Enchantment ench : ForgeRegistries.ENCHANTMENTS) {
            if (isBindingEnchantment(ench)) {
                all.putIfAbsent(ench, 1);
            }
        }
        EnchantmentHelper.setEnchantments(all, stack);
        stack.getOrCreateTag().putBoolean(TAG_ENCHANTED, true);
    }

    private static boolean isBindingEnchantment(Enchantment ench) {
        ResourceLocation id = ForgeRegistries.ENCHANTMENTS.getKey(ench);
        if (id == null) return false;
        String path = id.getPath().toLowerCase(Locale.ROOT);
        return path.contains("bind") || path.contains("soulbound") || path.contains("soul_bind");
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide() && !stack.getOrCreateTag().getBoolean(TAG_ENCHANTED)) {
            applyBindingEnchantments(stack);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.calling_bell.1"));
    }
}
