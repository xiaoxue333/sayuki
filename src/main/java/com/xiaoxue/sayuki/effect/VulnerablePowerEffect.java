/**
 * Sayuki — Vulnerable Power Effect (target takes +50% damage from attacks)
 */
package com.xiaoxue.sayuki.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class VulnerablePowerEffect extends MobEffect {

    public VulnerablePowerEffect() {
        super(MobEffectCategory.HARMFUL, 0x8B0000);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
