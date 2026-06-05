/**
 * Sayuki — Heaven's Door Effect (3s, marks AI-disabled mob)
 * Applied to attacker when Heaven Ear Ornaments triggers. Visual marker only;
 * actual NoAI manipulation is handled in ModEventHandler.
 */
package com.xiaoxue.sayuki.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class HeavenDoorEffect extends MobEffect {

    public HeavenDoorEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFFFAA);
    }
}
