/**
 * Sayuki — Stellar Meteor structure piece (NBT save/load via StellarMeteorSettings)
 * Compat: Goety-2 and IronsSpellbooks — internal to sayuki:stellar_meteor structure, no conflict
 */
package com.xiaoxue.sayuki.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class StellarMeteorStructurePiece extends StructurePiece {

    public static StructurePieceType createType() {
        return new StructurePieceType() {
            @Override
            public StructurePiece load(StructurePieceSerializationContext context, CompoundTag tag) {
                return new StellarMeteorStructurePiece(tag);
            }
        };
    }

    private final StellarMeteorSettings settings;

    protected StellarMeteorStructurePiece(BlockPos center, float radius,
            StructurePieceType type) {
        super(type, 0, createBoundingBox(center));
        this.settings = new StellarMeteorSettings(center, radius);
    }

    private static BoundingBox createBoundingBox(BlockPos origin) {
        int range = 4 * 16;
        ChunkPos chunkPos = new ChunkPos(origin);
        return new BoundingBox(
                chunkPos.getMinBlockX() - range, origin.getY(),
                chunkPos.getMinBlockZ() - range,
                chunkPos.getMaxBlockX() + range, origin.getY(),
                chunkPos.getMaxBlockZ() + range);
    }

    public StellarMeteorStructurePiece(CompoundTag tag) {
        super(ModStructures.METEOR_PIECE_TYPE.get(), tag);
        this.settings = StellarMeteorSettings.read(tag);
    }

    public StellarMeteorSettings getSettings() {
        return settings;
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        settings.write(tag);
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager,
            ChunkGenerator chunkGenerator, RandomSource rand, BoundingBox bounds,
            ChunkPos chunkPos, BlockPos blockPos) {
        StellarMeteorPlacer.place(level, settings, bounds, rand);
    }
}
