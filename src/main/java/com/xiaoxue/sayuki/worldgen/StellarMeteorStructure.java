/**
 * Sayuki — Stellar Meteor structure (Structure with custom Jigsaw/CODEC)
 * Compat: Goety-2 and IronsSpellbooks — structure ID: sayuki:stellar_meteor, no name clash
 */
package com.xiaoxue.sayuki.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import com.xiaoxue.sayuki.Sayuki;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.LegacyRandomSource;

import java.util.Optional;

public class StellarMeteorStructure extends Structure {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Sayuki.MOD_ID, "stellar_meteor");
    public static final ResourceKey<StructureSet> STRUCTURE_SET_KEY =
            ResourceKey.create(Registries.STRUCTURE_SET, ID);
    public static final ResourceKey<Structure> KEY =
            ResourceKey.create(Registries.STRUCTURE, ID);
    public static final TagKey<Biome> BIOME_TAG_KEY =
            TagKey.create(Registries.BIOME, ID);
    public static final Codec<StellarMeteorStructure> CODEC = simpleCodec(StellarMeteorStructure::new);

    public StellarMeteorStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.METEOR_STRUCTURE_TYPE.get();
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        var worldgenRandom = new WorldgenRandom(new LegacyRandomSource(0L));
        worldgenRandom.setLargeFeatureSeed(context.seed(), context.chunkPos().x, context.chunkPos().z);

        if (!worldgenRandom.nextBoolean()) {
            return Optional.empty();
        }

        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG,
                (piecesBuilder) -> generatePieces(piecesBuilder, context));
    }

    private static void generatePieces(StructurePiecesBuilder piecesBuilder, GenerationContext context) {
        var chunkPos = context.chunkPos();
        var random = context.random();
        var heightAccessor = context.heightAccessor();
        var generator = context.chunkGenerator();

        final int centerX = chunkPos.getMinBlockX() + random.nextInt(16);
        final int centerZ = chunkPos.getMinBlockZ() + random.nextInt(16);
        final float meteorRadius = random.nextFloat() * 4.0f + 3.0f;

        final int centerY = generator.getBaseHeight(centerX, centerZ,
                Heightmap.Types.WORLD_SURFACE_WG, heightAccessor, context.randomState());

        BlockPos actualPos = new BlockPos(centerX, centerY, centerZ);

        piecesBuilder.addPiece(new StellarMeteorStructurePiece(actualPos, meteorRadius,
                ModStructures.METEOR_PIECE_TYPE.get()));
    }
}
