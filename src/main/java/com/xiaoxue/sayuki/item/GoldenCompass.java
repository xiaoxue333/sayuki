package com.xiaoxue.sayuki.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.biome.Biomes;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class GoldenCompass extends Item implements ICurioItem {
    public GoldenCompass(Properties properties) { super(properties); }

    private static final String PKEY_COMPASS_CHEST_X = "SayukiGoldenCompassChestX";
    private static final String PKEY_COMPASS_CHEST_Y = "SayukiGoldenCompassChestY";
    private static final String PKEY_COMPASS_CHEST_Z = "SayukiGoldenCompassChestZ";
    private static final String PKEY_COMPASS_MARKER = "SayukiGoldenCompassMarker";
    private static final int SEARCH_RADIUS = 48;

    /** Call every 60 ticks from onPlayerTick. Highlights nearest chest in deep dark. */
    public static void tick(Player player) {
        Level level = player.level();
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;

        if (!level.getBiome(player.blockPosition()).is(Biomes.DEEP_DARK)) {
            removeHighlight(player);
            return;
        }

        // Check if existing marker is still alive
        int markerId = player.getPersistentData().getInt(PKEY_COMPASS_MARKER);
        if (markerId != 0) {
            Entity existing = serverLevel.getEntity(markerId);
            if (existing != null && existing.isAlive()) return;
        }

        // Find nearest chest
        BlockPos chestPos = findNearestChest(serverLevel, player.blockPosition());
        if (chestPos == null) {
            removeHighlight(player);
            return;
        }

        // Create glowing marker at chest center
        AreaEffectCloud marker = new AreaEffectCloud(serverLevel,
                chestPos.getX() + 0.5, chestPos.getY() + 0.5, chestPos.getZ() + 0.5);
        marker.setRadius(0.6f);
        marker.setDuration(Integer.MAX_VALUE);
        marker.setWaitTime(0);
        marker.setRadiusOnUse(0);
        marker.setRadiusPerTick(0);
        marker.setNoGravity(true);
        marker.setGlowingTag(true);
        serverLevel.addFreshEntity(marker);

        player.getPersistentData().putInt(PKEY_COMPASS_CHEST_X, chestPos.getX());
        player.getPersistentData().putInt(PKEY_COMPASS_CHEST_Y, chestPos.getY());
        player.getPersistentData().putInt(PKEY_COMPASS_CHEST_Z, chestPos.getZ());
        player.getPersistentData().putInt(PKEY_COMPASS_MARKER, marker.getId());
    }

    /** Remove highlight marker and clear tracking data. */
    public static void removeHighlight(Player player) {
        Level level = player.level();
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;

        int markerId = player.getPersistentData().getInt(PKEY_COMPASS_MARKER);
        if (markerId != 0) {
            Entity marker = serverLevel.getEntity(markerId);
            if (marker != null) {
                marker.discard();
            }
        }

        player.getPersistentData().remove(PKEY_COMPASS_CHEST_X);
        player.getPersistentData().remove(PKEY_COMPASS_CHEST_Y);
        player.getPersistentData().remove(PKEY_COMPASS_CHEST_Z);
        player.getPersistentData().remove(PKEY_COMPASS_MARKER);
    }

    /** Called when a player opens a block - if it's the tracked chest, remove highlight. */
    public static void onBlockInteract(Player player, BlockPos pos) {
        if (player.level().isClientSide()) return;
        int cx = player.getPersistentData().getInt(PKEY_COMPASS_CHEST_X);
        int cy = player.getPersistentData().getInt(PKEY_COMPASS_CHEST_Y);
        int cz = player.getPersistentData().getInt(PKEY_COMPASS_CHEST_Z);
        if (cx == 0 && cy == 0 && cz == 0) return;
        if (pos.getX() == cx && pos.getY() == cy && pos.getZ() == cz) {
            removeHighlight(player);
        }
    }

    /** Find the nearest chest block within search radius. */
    private static BlockPos findNearestChest(ServerLevel level, BlockPos origin) {
        BlockPos nearest = null;
        double nearestDist = Double.MAX_VALUE;

        int r = SEARCH_RADIUS;
        for (BlockPos pos : BlockPos.betweenClosed(
                origin.getX() - r, origin.getY() - r, origin.getZ() - r,
                origin.getX() + r, origin.getY() + r, origin.getZ() + r)) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ChestBlockEntity) {
                double dist = pos.distSqr(origin);
                if (dist < nearestDist) {
                    nearestDist = dist;
                    nearest = pos.immutable();
                }
            }
        }
        return nearest;
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.golden_compass.1"));
    }
}
