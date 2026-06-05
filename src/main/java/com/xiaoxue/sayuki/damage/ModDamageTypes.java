/**
 * Sayuki — Custom damage types (water_magic, whip) via ResourceKey<DamageType>
 * Compat: Goety-2 — uses goety: namespace damage types, no clash
 * Compat: IronsSpellbooks — uses irons_spellbooks: school-based damage types, no clash
 */
package com.xiaoxue.sayuki.damage;

import com.xiaoxue.sayuki.Sayuki;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

public class ModDamageTypes {

    public static final ResourceKey<DamageType> WATER_MAGIC =
            ResourceKey.create(Registries.DAMAGE_TYPE,
                    ResourceLocation.fromNamespaceAndPath(Sayuki.MOD_ID, "water_magic"));

    public static final ResourceKey<DamageType> WHIP =
            ResourceKey.create(Registries.DAMAGE_TYPE,
                    ResourceLocation.fromNamespaceAndPath(Sayuki.MOD_ID, "whip"));

    public static final ResourceKey<DamageType> WHISPERING_ECHO =
            ResourceKey.create(Registries.DAMAGE_TYPE,
                    ResourceLocation.fromNamespaceAndPath(Sayuki.MOD_ID, "whispering_echo"));

    public static DamageSource waterMagic(Entity entity) {
        return create(entity, WATER_MAGIC);
    }

    public static DamageSource waterMagic(Entity directEntity, Entity causingEntity) {
        return create(directEntity, causingEntity, WATER_MAGIC);
    }

    public static DamageSource whip(Entity entity) {
        return create(entity, WHIP);
    }

    public static DamageSource whip(Entity directEntity, Entity causingEntity) {
        return create(directEntity, causingEntity, WHIP);
    }

    public static DamageSource whisperingEcho(Entity entity) {
        return create(entity, WHISPERING_ECHO);
    }

    private static DamageSource create(Entity entity, ResourceKey<DamageType> key) {
        Holder<DamageType> holder = entity.level().registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(key);
        return new DamageSource(holder, entity);
    }

    private static DamageSource create(Entity directEntity, Entity causingEntity, ResourceKey<DamageType> key) {
        Holder<DamageType> holder = directEntity.level().registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(key);
        return new DamageSource(holder, directEntity, causingEntity);
    }
}
