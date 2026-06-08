/**
 * Sayuki — Jewelry Box Effect (stun: NoAI for Mob entities, no levels, duration stacks)
 * AI freeze handled via MobEffectEvent.Added/Expired in ModEventHandler; effect itself is a marker.
 */
package com.xiaoxue.sayuki.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class JewelryBoxEffect extends MobEffect {

    public JewelryBoxEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFD700);
    }
}
