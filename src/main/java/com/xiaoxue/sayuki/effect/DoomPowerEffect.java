/**
 * Sayuki — Doom Power Effect (negative, infinite duration, configurable max level)
 */
package com.xiaoxue.sayuki.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class DoomPowerEffect extends MobEffect {

    public DoomPowerEffect() {
        super(MobEffectCategory.HARMFUL, 0x2D0040);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
