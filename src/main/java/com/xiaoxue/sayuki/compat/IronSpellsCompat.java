/**
 * Sayuki — Iron's Spells 'n Spellbooks compat layer (non-hard dependency)
 * Uses reflection to avoid class-loading issues when IronSpellbooks is not installed.
 */
package com.xiaoxue.sayuki.compat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Method;

public class IronSpellsCompat {

    private static final boolean IRON_SPELLS_LOADED = ModList.get().isLoaded("irons_spellbooks");
    private static Method magicDataGetPlayerMagicData = null;
    private static Method magicDataGetMana = null;
    private static Method magicDataSetMana = null;
    private static Method summonGetSummoner = null;

    static {
        if (IRON_SPELLS_LOADED) {
            try {
                Class<?> magicDataClass = Class.forName("io.redspace.ironsspellbooks.api.magic.MagicData");
                magicDataGetPlayerMagicData = magicDataClass.getMethod("getPlayerMagicData", LivingEntity.class);
                magicDataGetMana = magicDataClass.getMethod("getMana");
                magicDataSetMana = magicDataClass.getMethod("setMana", float.class);
                // ISummon interface — all ISS summoned entities implement this
                try {
                    Class<?> iSummonClass = Class.forName("io.redspace.ironsspellbooks.api.spells.ISummon");
                    summonGetSummoner = iSummonClass.getMethod("getSummoner");
                } catch (Exception ignored2) {
                    // older ISS version without ISummon interface
                }
            } catch (Exception e) {
                // IronSpellbooks loaded but reflection failed — silently degrade
            }
        }
    }

    public static boolean isLoaded() {
        return IRON_SPELLS_LOADED;
    }

    /** Get the entity's current mana. Returns -1 if unavailable. */
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

    /** Set the entity's mana. */
    public static void setMana(LivingEntity entity, float mana) {
        if (!IRON_SPELLS_LOADED) return;
        try {
            Object magicData = magicDataGetPlayerMagicData.invoke(null, entity);
            if (magicData != null) {
                magicDataSetMana.invoke(magicData, mana);
            }
        } catch (Exception ignored) {}
    }

    /** Get the entity's max mana via the max_mana attribute. Returns -1 if unavailable. */
    public static float getMaxMana(LivingEntity entity) {
        if (!IRON_SPELLS_LOADED) return -1;
        Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(
                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "max_mana"));
        if (attr != null) {
            return (float) entity.getAttributeValue(attr);
        }
        return -1;
    }

    /** Get the summoner of an ISS summoned entity. Returns null if not an ISS summon. */
    public static LivingEntity getSummonOwner(LivingEntity entity) {
        if (!IRON_SPELLS_LOADED || summonGetSummoner == null) return null;
        try {
            if (summonGetSummoner.getDeclaringClass().isInstance(entity)) {
                return (LivingEntity) summonGetSummoner.invoke(entity);
            }
        } catch (Exception ignored) {}
        return null;
    }
}
