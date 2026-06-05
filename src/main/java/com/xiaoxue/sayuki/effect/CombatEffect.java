/**
 * Sayuki — Combat Effect (7s counter, refreshed on attack)
 * When this effect expires, the Whisper effect is applied.
 */
package com.xiaoxue.sayuki.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class CombatEffect extends MobEffect {

    public CombatEffect() {
        super(MobEffectCategory.NEUTRAL, 0xFFDDAA);
    }
}
