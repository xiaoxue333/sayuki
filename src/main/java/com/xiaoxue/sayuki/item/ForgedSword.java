/**
 * Sayuki — Forged Sword, dynamically created/upgraded by Fencing Manual.
 * Attack damage = 1 + 0.1 * forgeLevel, attack speed = 1.0, infinite durability.
 */
package com.xiaoxue.sayuki.item;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

@SuppressWarnings("deprecation")
public class ForgedSword extends SwordItem {

    private static final Tier FORGED_TIER = new Tier() {
        @Override public int getUses() { return 1; }
        @Override public float getSpeed() { return 1.5F; }
        @Override public float getAttackDamageBonus() { return 0; }
        @Override public int getLevel() { return 0; }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
    };

    public ForgedSword(Properties properties) {
        super(FORGED_TIER, 0, 0, properties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = ArrayListMultimap.create();
        if (slot == EquipmentSlot.MAINHAND) {
            int forgeLevel = stack.getOrCreateTag().getInt("ForgeLevel");
            double damage = 1.0 + forgeLevel * 0.1;
            map.put(Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Forged damage",
                            damage, AttributeModifier.Operation.ADDITION));
            map.put(Attributes.ATTACK_SPEED,
                    new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Forged speed",
                            1.0F - 4.0F, AttributeModifier.Operation.ADDITION));
        }
        return map;
    }
}
