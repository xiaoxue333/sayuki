/**
 * Sayuki — Silence Effect (60s cooldown marker for Heaven Ear Ornaments)
 * While active, the Heaven Ear Ornaments cannot trigger again.
 * Icon uses vanilla paper texture.
 */
package com.xiaoxue.sayuki.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class SilenceEffect extends MobEffect {

    public SilenceEffect() {
        super(MobEffectCategory.NEUTRAL, 0xDDDDCC);
    }
}
