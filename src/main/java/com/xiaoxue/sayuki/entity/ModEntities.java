/**
 * Sayuki — Entity registration (stellar_jade_projectile, heart_grenade_projectile)
 * Compat: Goety-2 and IronsSpellbooks — uses sayuki: namespace, no entity name clash
 */
package com.xiaoxue.sayuki.entity;

import com.xiaoxue.sayuki.Sayuki;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Sayuki.MOD_ID);

    public static final RegistryObject<EntityType<StellarJadeProjectile>> STELLAR_JADE_PROJECTILE =
            ENTITY_TYPES.register("stellar_jade_projectile",
                    () -> EntityType.Builder.<StellarJadeProjectile>of(StellarJadeProjectile::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(64)
                            .updateInterval(10)
                            .noSummon()
                            .noSave()
                            .build("stellar_jade_projectile"));

    public static final RegistryObject<EntityType<HeartGrenadeProjectile>> HEART_GRENADE_PROJECTILE =
            ENTITY_TYPES.register("heart_grenade_projectile",
                    () -> EntityType.Builder.<HeartGrenadeProjectile>of(HeartGrenadeProjectile::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(64)
                            .updateInterval(10)
                            .noSummon()
                            .build("heart_grenade_projectile"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
