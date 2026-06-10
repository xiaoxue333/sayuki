package com.xiaoxue.sayuki.mixin;

import com.xiaoxue.sayuki.client.DynamicLightManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Injects dynamic block-light at the client-side LightEngine level.
 * When a dynamic light source is active, getRawBrightness returns a boosted value
 * for block positions within range.
 */
@Mixin(LightEngine.class)
public abstract class LightEngineMixin {

    @ModifyVariable(
            method = "getRawBrightness",
            at = @At(value = "RETURN"),
            ordinal = 0)
    private int sayuki$addDynamicLight(int original, BlockPos pos, int skyDarken) {
        int level = DynamicLightManager.lightLevel;
        if (level <= 0) return original;

        double dx = pos.getX() + 0.5 - DynamicLightManager.x;
        double dy = pos.getY() + 0.5 - DynamicLightManager.y;
        double dz = pos.getZ() + 0.5 - DynamicLightManager.z;

        int dist = (int) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist >= level) return original;

        int contribution = level - dist;
        return Math.max(original, contribution);
    }
}
