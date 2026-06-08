/**
 * Sayuki — Custom enchantment registration
 */
package com.xiaoxue.sayuki.enchantment;

import com.xiaoxue.sayuki.Sayuki;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Sayuki.MOD_ID);

    public static final RegistryObject<Enchantment> INSTINCT =
            ENCHANTMENTS.register("instinct", InstinctEnchantment::new);

    public static final RegistryObject<Enchantment> BRILLIANCE =
            ENCHANTMENTS.register("brilliance", BrillianceEnchantment::new);

    public static final RegistryObject<Enchantment> SWIFTNESS =
            ENCHANTMENTS.register("swiftness", SwiftnessEnchantment::new);

    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }
}
