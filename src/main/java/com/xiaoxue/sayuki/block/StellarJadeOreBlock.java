/**
 * Sayuki — Stellar Jade Ore Block (DropExperienceBlock with custom amethyst sound)
 * Compat: Goety-2 and IronsSpellbooks — block ID: sayuki:stellar_jade_ore, no conflict
 */
package com.xiaoxue.sayuki.block;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.util.ForgeSoundType;
public class StellarJadeOreBlock extends DropExperienceBlock {

    public static final SoundType STELLAR_JADE_ORE_SOUND = new ForgeSoundType(
        0.9F, 1.0F,
        () -> SoundEvents.AMETHYST_BLOCK_BREAK,
        () -> SoundEvents.AMETHYST_BLOCK_STEP,
        () -> SoundEvents.AMETHYST_BLOCK_PLACE,
        () -> SoundEvents.AMETHYST_BLOCK_HIT,
        () -> SoundEvents.AMETHYST_BLOCK_FALL
);

    public StellarJadeOreBlock(Properties properties) {
        super(properties, ConstantInt.of(10));
    }
}