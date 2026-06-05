/**
 * Sayuki — Stellar Meteor settings (pos + radius NBT serialization)
 * Compat: Goety-2 and IronsSpellbooks — internal data class, no conflict
 */
package com.xiaoxue.sayuki.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public final class StellarMeteorSettings {
    private final BlockPos pos;
    private final float meteorRadius;

    public StellarMeteorSettings(BlockPos pos, float meteorRadius) {
        this.pos = pos;
        this.meteorRadius = meteorRadius;
    }

    public BlockPos getPos() {
        return pos;
    }

    public float getMeteorRadius() {
        return meteorRadius;
    }

    public CompoundTag write(CompoundTag tag) {
        tag.putLong(StellarMeteorConstants.TAG_POS, pos.asLong());
        tag.putFloat(StellarMeteorConstants.TAG_RADIUS, meteorRadius);
        return tag;
    }

    public static StellarMeteorSettings read(CompoundTag tag) {
        BlockPos pos = BlockPos.of(tag.getLong(StellarMeteorConstants.TAG_POS));
        float radius = tag.getFloat(StellarMeteorConstants.TAG_RADIUS);
        return new StellarMeteorSettings(pos, radius);
    }
}
