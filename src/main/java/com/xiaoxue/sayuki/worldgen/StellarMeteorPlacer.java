/**
 * Sayuki — Stellar Meteor placer (crater + body + debris generation logic)
 * Compat: Goety-2 and IronsSpellbooks — internal worldgen logic, no data conflict
 */
package com.xiaoxue.sayuki.worldgen;

import com.xiaoxue.sayuki.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public final class StellarMeteorPlacer {

    public static void place(LevelAccessor level, StellarMeteorSettings settings, BoundingBox boundingBox,
            RandomSource random) {
        var placer = new StellarMeteorPlacer(level, settings, boundingBox, random);
        placer.place();
    }

    private final StellarMeteorBlockPutter putter = new StellarMeteorBlockPutter();
    private final LevelAccessor level;
    private final RandomSource random;
    private final BlockPos center;
    private final int cx, cy, cz;
    private final float radius;
    private final float radiusSq;
    private final BoundingBox boundingBox;
    private static final BlockState CRUST = Blocks.OBSIDIAN.defaultBlockState();
    private static final BlockState ORE = ModBlocks.STELLAR_JADE_ORE.get().defaultBlockState();
    private static final BlockState BLOCK = ModBlocks.STELLAR_JADE_BLOCK.get().defaultBlockState();
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();

    private StellarMeteorPlacer(LevelAccessor level, StellarMeteorSettings settings, BoundingBox boundingBox,
            RandomSource random) {
        this.level = level;
        this.random = random;
        this.center = settings.getPos();
        this.cx = center.getX();
        this.cy = center.getY();
        this.cz = center.getZ();
        this.radius = settings.getMeteorRadius();
        this.radiusSq = this.radius * this.radius;
        this.boundingBox = boundingBox;
    }

    private void place() {
        placeCrater();
        placeMeteorBody();
        placeScatterDebris();
    }

    private void placeCrater() {
        MutableBlockPos pos = new MutableBlockPos();
        int craterRadius = (int) (radius * 2.5);
        int craterDepth = (int) (radius * 0.8);

        for (int x = cx - craterRadius; x <= cx + craterRadius; x++) {
            pos.setX(x);
            if (x < boundingBox.minX() || x > boundingBox.maxX()) continue;
            for (int z = cz - craterRadius; z <= cz + craterRadius; z++) {
                pos.setZ(z);
                if (z < boundingBox.minZ() || z > boundingBox.maxZ()) continue;

                double dx = x - cx;
                double dz = z - cz;
                double dist = Math.sqrt(dx * dx + dz * dz);

                if (dist > craterRadius) continue;

                double maxY = cy + craterDepth * (1.0 - (dist / craterRadius));

                for (int y = cy - 2; y <= maxY; y++) {
                    pos.setY(y);
                    double dy = cy - y;
                    if (dy < -2) continue;

                    BlockState current = level.getBlockState(pos);
                    if (current.getBlock() == Blocks.BEDROCK) continue;

                    putter.put(level, pos, Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    private void placeMeteorBody() {
        MutableBlockPos pos = new MutableBlockPos();
        int range = (int) Math.ceil(radius) + 1;

        for (int dx = -range; dx <= range; dx++) {
            int x = cx + dx;
            pos.setX(x);
            if (x < boundingBox.minX() || x > boundingBox.maxX()) continue;
            for (int dz = -range; dz <= range; dz++) {
                int z = cz + dz;
                pos.setZ(z);
                if (z < boundingBox.minZ() || z > boundingBox.maxZ()) continue;
                for (int dy = -range; dy <= range; dy++) {
                    int y = cy + dy;
                    pos.setY(y);

                    if (isInHollow(x, y, z)) {
                        putter.put(level, pos, AIR);
                        continue;
                    }

                    double distSq = dx * dx * 0.7 + dy * dy * 1.2 + dz * dz * 0.7;

                    if (distSq > radiusSq) continue;

                    double distFromCenter = Math.sqrt(distSq);
                    double normalized = distFromCenter / radius;

                    if (normalized < 0.15 && random.nextFloat() < 0.25) {
                        putter.put(level, pos, BLOCK);
                    } else if (normalized < 0.35 && random.nextFloat() < 0.10) {
                        putter.put(level, pos, BLOCK);
                    } else if (normalized < 0.65 && random.nextFloat() < 0.04) {
                        putter.put(level, pos, BLOCK);
                    } else if (normalized < 0.8) {
                        putter.put(level, pos, ORE);
                    } else {
                        putter.put(level, pos, CRUST);
                    }
                }
            }
        }
    }

    private boolean isInHollow(int x, int y, int z) {
        return Math.abs(x - cx) <= 1 && Math.abs(z - cz) <= 1
                && y >= cy - 1 && y <= cy + 1;
    }

    private void placeScatterDebris() {
        MutableBlockPos pos = new MutableBlockPos();
        int scatterRadius = (int) (radius * 3.0);

        for (int i = 0; i < (int) (radius * radius * 1.5); i++) {
            int rx = cx + random.nextInt(scatterRadius * 2) - scatterRadius;
            int rz = cz + random.nextInt(scatterRadius * 2) - scatterRadius;
            pos.setX(rx);
            pos.setZ(rz);
            if (rx < boundingBox.minX() || rx > boundingBox.maxX()) continue;
            if (rz < boundingBox.minZ() || rz > boundingBox.maxZ()) continue;

            double dx = rx - cx;
            double dz = rz - cz;
            double dist = Math.sqrt(dx * dx + dz * dz);

            if (dist > scatterRadius || dist < radius * 0.9) continue;

            int surfaceY = cy;
            for (int y = cy + (int) radius + 3; y >= cy - 3; y--) {
                pos.setY(y);
                if (!level.getBlockState(pos).isAir()) {
                    surfaceY = y + 1;
                    break;
                }
            }

            pos.setY(surfaceY);

            if (random.nextFloat() < 0.15) {
                putter.put(level, pos, ORE);
            } else if (random.nextFloat() < 0.4) {
                putter.put(level, pos, CRUST);
            }
        }
    }
}
