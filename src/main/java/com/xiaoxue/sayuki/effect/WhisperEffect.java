/**
 * Sayuki — Whisper Effect (infinite, signals penalty state)
 * Removed on next attack; while active, the next attack inflicts self-damage.
 */
package com.xiaoxue.sayuki.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class WhisperEffect extends MobEffect {

    public WhisperEffect() {
        super(MobEffectCategory.HARMFUL, 0x660022);
    }
}
