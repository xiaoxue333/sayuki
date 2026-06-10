/**
 * Sayuki — Jewelry Box Effect (stun: NoAI for Mob entities, 3s duration, marker effect)
 * AI freeze/recovery handled via PersistentData + LivingTickEvent in ModEventHandler.
 */
package com.xiaoxue.sayuki.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class JewelryBoxEffect extends MobEffect {

    public JewelryBoxEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFD700);
    }
}
