package com.xiaoxue.sayuki.mixin;

import com.xiaoxue.sayuki.item.PandorasBox;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Pandora's Box: doubles natural spawn attempts per position when a player
 * with Pandora's Box equipped is in the same chunk.
 */
@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {

    @Unique
    private static final ThreadLocal<Boolean> SAYUKI$PANDORA_REENTRY = ThreadLocal.withInitial(() -> false);

    @Inject(
            method = "spawnCategoryForPosition(Lnet/minecraft/world/entity/MobCategory;" +
                     "Lnet/minecraft/server/level/ServerLevel;" +
                     "Lnet/minecraft/world/level/chunk/ChunkAccess;" +
                     "Lnet/minecraft/core/BlockPos;" +
                     "Lnet/minecraft/world/level/NaturalSpawner$SpawnPredicate;" +
                     "Lnet/minecraft/world/level/NaturalSpawner$AfterSpawnCallback;)V",
            at = @At("RETURN"),
            cancellable = false)
    private static void sayuki$doubleSpawnAttempts(MobCategory category, ServerLevel level, ChunkAccess chunk,
            BlockPos pos, NaturalSpawner.SpawnPredicate predicate, NaturalSpawner.AfterSpawnCallback callback,
            CallbackInfo ci) {
        if (SAYUKI$PANDORA_REENTRY.get()) return;

        for (ServerPlayer player : level.players()) {
            if (player.chunkPosition().equals(chunk.getPos()) && PandorasBox.isEquippedBy(player)) {
                SAYUKI$PANDORA_REENTRY.set(true);
                try {
                    NaturalSpawner.spawnCategoryForPosition(category, level, chunk, pos, predicate, callback);
                } finally {
                    SAYUKI$PANDORA_REENTRY.set(false);
                }
                break;
            }
        }
    }
}
