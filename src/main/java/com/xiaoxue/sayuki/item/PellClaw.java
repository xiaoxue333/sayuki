package com.xiaoxue.sayuki.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class PellClaw extends Item implements ICurioItem {
    public PellClaw(Properties properties) { super(properties); }

    private static final UUID ARMOR_BONUS_UUID = UUID.fromString("6f3d8a2b-9c4e-5d1f-a7e2-1b8c3d4e5f60");
    private static final String PKEY_BONUS_HEAD = "SayukiPellClawBonusHead";
    private static final String PKEY_BONUS_CHEST = "SayukiPellClawBonusChest";
    private static final String PKEY_BONUS_LEGS = "SayukiPellClawBonusLegs";
    private static final String PKEY_BONUS_FEET = "SayukiPellClawBonusFeet";

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    /** Force a single armor piece to 1 durability. */
    public static void setArmorToOne(ItemStack armor) {
        if (!armor.isEmpty() && armor.isDamageableItem() && armor.getMaxDamage() > 1) {
            armor.setDamageValue(armor.getMaxDamage() - 1);
        }
    }

    /** Force all currently worn armor to 1 durability. */
    public static void setAllArmorToOne(Player player) {
        setArmorToOne(player.getItemBySlot(EquipmentSlot.HEAD));
        setArmorToOne(player.getItemBySlot(EquipmentSlot.CHEST));
        setArmorToOne(player.getItemBySlot(EquipmentSlot.LEGS));
        setArmorToOne(player.getItemBySlot(EquipmentSlot.FEET));
    }

    /** Called when an armor piece breaks — increment the bonus for its slot. */
    public static void onArmorBreak(Player player, ItemStack broken) {
        String key = getBonusKeyForItem(broken);
        if (key == null) return;
        int current = player.getPersistentData().getInt(key);
        player.getPersistentData().putInt(key, current + 1);
        applyArmorBonus(player);
    }

    /** Apply accumulated armor bonus — only counts slots that actually have armor worn. */
    public static void applyArmorBonus(Player player) {
        int total = 0;
        if (!player.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) total += player.getPersistentData().getInt(PKEY_BONUS_HEAD);
        if (!player.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) total += player.getPersistentData().getInt(PKEY_BONUS_CHEST);
        if (!player.getItemBySlot(EquipmentSlot.LEGS).isEmpty()) total += player.getPersistentData().getInt(PKEY_BONUS_LEGS);
        if (!player.getItemBySlot(EquipmentSlot.FEET).isEmpty()) total += player.getPersistentData().getInt(PKEY_BONUS_FEET);

        var attr = player.getAttribute(Attributes.ARMOR);
        if (attr != null) {
            attr.removeModifier(ARMOR_BONUS_UUID);
            if (total > 0) {
                attr.addTransientModifier(new AttributeModifier(ARMOR_BONUS_UUID,
                        "PellClawArmorBonus", total, AttributeModifier.Operation.ADDITION));
            }
        }
    }

    /** Remove the armor bonus modifier. */
    public static void removeArmorBonus(Player player) {
        var attr = player.getAttribute(Attributes.ARMOR);
        if (attr != null) {
            attr.removeModifier(ARMOR_BONUS_UUID);
        }
    }

    private static String getBonusKeyForItem(ItemStack stack) {
        if (stack.getItem() instanceof net.minecraft.world.item.ArmorItem armorItem) {
            return getBonusKey(armorItem.getEquipmentSlot());
        }
        return null;
    }

    private static String getBonusKey(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> PKEY_BONUS_HEAD;
            case CHEST -> PKEY_BONUS_CHEST;
            case LEGS -> PKEY_BONUS_LEGS;
            case FEET -> PKEY_BONUS_FEET;
            default -> null;
        };
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.pell_claw.1"));
    }
}
