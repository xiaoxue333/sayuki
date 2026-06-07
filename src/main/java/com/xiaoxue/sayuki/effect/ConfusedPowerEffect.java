/**
 * Sayuki — Confused Power Effect (attack speed randomized between 0~3)
 */
package com.xiaoxue.sayuki.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class ConfusedPowerEffect extends MobEffect {

    public ConfusedPowerEffect() {
        super(MobEffectCategory.HARMFUL, 0x9370DB);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
