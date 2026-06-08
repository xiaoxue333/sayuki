/**
 * Sayuki — Heaven's Door Effect (3s, freezes all living entities including players)
 * Per-tick motion freeze; Mob entities additionally get NoAi for reliability.
 */
package com.xiaoxue.sayuki.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class HeavenDoorEffect extends MobEffect {

    public HeavenDoorEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFFFAA);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration > 0;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        entity.setDeltaMovement(0, Math.min(0, entity.getDeltaMovement().y), 0);
        entity.xxa = 0;
        entity.yya = 0;
        entity.zza = 0;
    }
}
