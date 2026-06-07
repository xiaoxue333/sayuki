/**
 * Sayuki — MobEffect registration (combat, whisper)
 * Compat: Goety-2 and IronsSpellbooks — uses sayuki: namespace, no effect name clash
 */
package com.xiaoxue.sayuki.effect;

import com.xiaoxue.sayuki.Sayuki;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Sayuki.MOD_ID);

    public static final RegistryObject<MobEffect> COMBAT =
            EFFECTS.register("combat", CombatEffect::new);

    public static final RegistryObject<MobEffect> WHISPER =
            EFFECTS.register("whisper", WhisperEffect::new);

    public static final RegistryObject<MobEffect> HEAVEN_DOOR =
            EFFECTS.register("heaven_door", HeavenDoorEffect::new);

    public static final RegistryObject<MobEffect> SILENCE =
            EFFECTS.register("silence", SilenceEffect::new);

    public static final RegistryObject<MobEffect> VULNERABLE_POWER =
            EFFECTS.register("vulnerable_power", VulnerablePowerEffect::new);

    public static final RegistryObject<MobEffect> WEAK_POWER =
            EFFECTS.register("weak_power", WeakPowerEffect::new);

    public static final RegistryObject<MobEffect> DOOM_POWER =
            EFFECTS.register("doom_power", DoomPowerEffect::new);

    public static final RegistryObject<MobEffect> POISON_POWER =
            EFFECTS.register("poison_power", PoisonPowerEffect::new);

    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }
}
