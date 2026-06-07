/**
 * Sayuki — Iron's Spells 'n Spellbooks compat layer (non-hard dependency)
 * Uses reflection to avoid class-loading issues when IronSpellbooks is not installed.
 */
package com.xiaoxue.sayuki.compat;

import com.xiaoxue.sayuki.Config;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IronSpellsCompat {

    private static final boolean IRON_SPELLS_LOADED = ModList.get().isLoaded("irons_spellbooks");
    private static Method magicDataGetPlayerMagicData = null;
    private static Method magicDataGetMana = null;
    private static Method magicDataSetMana = null;
    private static Method summonGetSummoner = null;

    /** Cached list of all spell_power attributes (including addon ones). Lazily populated. */
    private static List<Attribute> allSpellPowerAttributes = null;

    static {
        if (IRON_SPELLS_LOADED) {
            try {
                Class<?> magicDataClass = Class.forName("io.redspace.ironsspellbooks.api.magic.MagicData");
                magicDataGetPlayerMagicData = magicDataClass.getMethod("getPlayerMagicData", LivingEntity.class);
                magicDataGetMana = magicDataClass.getMethod("getMana");
                magicDataSetMana = magicDataClass.getMethod("setMana", float.class);
                try {
                    Class<?> iSummonClass = Class.forName("io.redspace.ironsspellbooks.api.spells.ISummon");
                    summonGetSummoner = iSummonClass.getMethod("getSummoner");
                } catch (Exception ignored2) {}
            } catch (Exception e) {}
        }
    }

    public static boolean isLoaded() {
        return IRON_SPELLS_LOADED;
    }

    // ===== Mana =====

    public static float getMana(LivingEntity entity) {
        if (!IRON_SPELLS_LOADED) return -1;
        try {
            Object magicData = magicDataGetPlayerMagicData.invoke(null, entity);
            if (magicData != null) {
                return (float) magicDataGetMana.invoke(magicData);
            }
        } catch (Exception ignored) {}
        return -1;
    }

    public static void setMana(LivingEntity entity, float mana) {
        if (!IRON_SPELLS_LOADED) return;
        try {
            Object magicData = magicDataGetPlayerMagicData.invoke(null, entity);
            if (magicData != null) {
                magicDataSetMana.invoke(magicData, mana);
            }
        } catch (Exception ignored) {}
    }

    public static float getMaxMana(LivingEntity entity) {
        if (!IRON_SPELLS_LOADED) return -1;
        Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(
                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "max_mana"));
        if (attr != null) {
            return (float) entity.getAttributeValue(attr);
        }
        return -1;
    }

    // ===== Summons =====

    public static LivingEntity getSummonOwner(LivingEntity entity) {
        if (!IRON_SPELLS_LOADED || summonGetSummoner == null) return null;
        try {
            if (summonGetSummoner.getDeclaringClass().isInstance(entity)) {
                return (LivingEntity) summonGetSummoner.invoke(entity);
            }
        } catch (Exception ignored) {}
        return null;
    }

    // ===== Max Mana modifier (via ISS mana attribute) =====

    public static void applyMaxManaModifier(LivingEntity entity, UUID uuid, int bonus) {
        if (!IRON_SPELLS_LOADED) return;
        Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(
                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "max_mana"));
        if (attr == null) return;
        AttributeInstance instance = entity.getAttribute(attr);
        if (instance == null) return;
        instance.removeModifier(uuid);
        instance.addPermanentModifier(new AttributeModifier(uuid, "sayuki_max_mana", bonus,
                AttributeModifier.Operation.ADDITION));
    }

    public static void removeMaxManaModifier(LivingEntity entity, UUID uuid) {
        if (!IRON_SPELLS_LOADED) return;
        Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(
                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "max_mana"));
        if (attr == null) return;
        AttributeInstance instance = entity.getAttribute(attr);
        if (instance == null) return;
        instance.removeModifier(uuid);
    }

    // ===== Single school spell power =====

    public static void applySpellPowerModifier(LivingEntity entity, UUID uuid, String school, double bonus) {
        if (!IRON_SPELLS_LOADED) return;
        Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(
                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", school + "_spell_power"));
        if (attr == null) return;
        AttributeInstance instance = entity.getAttribute(attr);
        if (instance == null) return;
        instance.removeModifier(uuid);
        instance.addPermanentModifier(new AttributeModifier(uuid, "sayuki_" + school + "_power", bonus,
                AttributeModifier.Operation.ADDITION));
    }

    public static void removeSpellPowerModifier(LivingEntity entity, UUID uuid, String school) {
        if (!IRON_SPELLS_LOADED) return;
        Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(
                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", school + "_spell_power"));
        if (attr == null) return;
        AttributeInstance instance = entity.getAttribute(attr);
        if (instance == null) return;
        instance.removeModifier(uuid);
    }

    // ===== All-school spell power (Data Disk: +0.1 to every spell school including addons) =====

    private static List<Attribute> getOrCacheSpellPowerAttributes() {
        if (allSpellPowerAttributes != null) return allSpellPowerAttributes;
        allSpellPowerAttributes = new ArrayList<>();
        for (ResourceLocation key : ForgeRegistries.ATTRIBUTES.getKeys()) {
            if (key.getPath().endsWith("_spell_power")) {
                Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(key);
                if (attr != null) {
                    allSpellPowerAttributes.add(attr);
                }
            }
        }
        return allSpellPowerAttributes;
    }

    public static void applyAllSpellPowerBonus(LivingEntity entity, UUID uuid) {
        if (!IRON_SPELLS_LOADED) return;
        double bonus = Config.DATA_DISK_SPELL_POWER_BONUS;
        for (Attribute attr : getOrCacheSpellPowerAttributes()) {
            AttributeInstance instance = entity.getAttribute(attr);
            if (instance == null) continue;
            instance.removeModifier(uuid);
            instance.addPermanentModifier(new AttributeModifier(uuid, "sayuki_data_disk_all_power", bonus,
                    AttributeModifier.Operation.ADDITION));
        }
    }

    public static void removeAllSpellPowerBonus(LivingEntity entity, UUID uuid) {
        if (!IRON_SPELLS_LOADED) return;
        for (Attribute attr : getOrCacheSpellPowerAttributes()) {
            AttributeInstance instance = entity.getAttribute(attr);
            if (instance == null) continue;
            instance.removeModifier(uuid);
        }
    }
}
