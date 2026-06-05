/**
 * Sayuki — Weak Power Effect (target deals -25% damage)
 */
package com.xiaoxue.sayuki.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class WeakPowerEffect extends MobEffect {

    public WeakPowerEffect() {
        super(MobEffectCategory.HARMFUL, 0x4B0082);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
