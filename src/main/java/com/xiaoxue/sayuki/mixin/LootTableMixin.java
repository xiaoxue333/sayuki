package com.xiaoxue.sayuki.mixin;

import com.xiaoxue.sayuki.item.GuMu;
import com.xiaoxue.sayuki.item.ModItems;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Driftwood: every loot generation rolls twice and picks the result with higher rarity.
 * Prismatic Gem: rolls from a random chest loot table instead, keeps the rarer of original vs random.
 * Priority: Prismatic Gem > Driftwood > Doom Tier effects.
 * Doom Tier 3: loot quantity -25%.
 * Doom Tier 7: rare loot probability -10%.
 */
@Mixin(LootTable.class)
public abstract class LootTableMixin {

    private static final ThreadLocal<Boolean> SAYUKI_LOOT_REENTRY = ThreadLocal.withInitial(() -> false);
    private static Method getRandomItemsMethod;
    private static List<ResourceLocation> chestLootTableIds;
    private static MinecraftServer cachedServer;

    @Inject(method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;",
            at = @At("RETURN"), cancellable = true)
    private void sayuki$sayukiDoubleLoot(LootContext context, CallbackInfoReturnable<ObjectArrayList<ItemStack>> cir) {
        if (SAYUKI_LOOT_REENTRY.get()) return;

        ServerLevel level = context.getLevel();
        if (level == null || level.isClientSide()) return;

        Vec3 origin = context.getParamOrNull(LootContextParams.ORIGIN);

        // Check nearby players for Prismatic Gem or Driftwood
        boolean hasPrismatic = false;
        boolean hasDriftwood = false;
        for (Player player : level.players()) {
            if (origin != null && player.distanceToSqr(origin) > 64 * 64) continue;
            var curio = CuriosApi.getCuriosInventory(player).resolve();
            if (curio.isPresent()) {
                if (!hasPrismatic) {
                    hasPrismatic = curio.get().findFirstCurio(
                            s -> s.getItem() == ModItems.PRISMATIC_GEM.get()).isPresent();
                }
                if (!hasDriftwood) {
                    hasDriftwood = curio.get().findFirstCurio(
                            s -> s.getItem() == ModItems.DRIFTWOOD.get()).isPresent();
                }
            }
            if (hasPrismatic || hasDriftwood) break;
        }

        // Doom tier effects on loot (applied before driftwood/prismatic double-roll)
        if (origin != null) {
            int doomTier = GuMu.getSumDoomTierNearby(level, origin, 64.0);
            ObjectArrayList<ItemStack> result = cir.getReturnValue();

            // 进阶3: loot quantity -25%
            if (doomTier >= 3) {
                result = applyLootReduction(result, 0.75);
            }

            // 进阶7: rare loot probability -10%
            if (doomTier >= 7) {
                result = applyRareLootReduction(result, 0.9);
            }

            if (doomTier >= 3) {
                cir.setReturnValue(result);
            }
        }

        if (!hasPrismatic && !hasDriftwood) return;

        ObjectArrayList<ItemStack> firstRoll = cir.getReturnValue();

        SAYUKI_LOOT_REENTRY.set(true);
        try {
            if (hasPrismatic) {
                // Roll from a random chest loot table, keep the rarer result
                LootTable randomTable = getRandomChestLootTable(level);
                if (randomTable != null) {
                    ObjectArrayList<ItemStack> secondRoll = callGetRandomItems(randomTable, context);
                    if (secondRoll != null && rarityScore(secondRoll) > rarityScore(firstRoll)) {
                        cir.setReturnValue(secondRoll);
                    }
                }
            } else {
                // Driftwood: double roll from the same table
                ObjectArrayList<ItemStack> secondRoll = callGetRandomItems((LootTable) (Object) this, context);
                if (secondRoll != null && rarityScore(secondRoll) > rarityScore(firstRoll)) {
                    cir.setReturnValue(secondRoll);
                }
            }
        } finally {
            SAYUKI_LOOT_REENTRY.set(false);
        }
    }

    /** Reduce loot quantity: keep only a fraction of each stack count, remove empty stacks. */
    private static ObjectArrayList<ItemStack> applyLootReduction(ObjectArrayList<ItemStack> items, double factor) {
        ObjectArrayList<ItemStack> reduced = new ObjectArrayList<>();
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                int newCount = Math.max(1, (int) (stack.getCount() * factor));
                if (newCount > 0) {
                    ItemStack copy = stack.copy();
                    copy.setCount(newCount);
                    reduced.add(copy);
                }
            } else {
                reduced.add(stack);
            }
        }
        return reduced;
    }

    /** Reduce rare items: each rare item has a chance to be removed. */
    private static ObjectArrayList<ItemStack> applyRareLootReduction(ObjectArrayList<ItemStack> items, double keepChance) {
        ObjectArrayList<ItemStack> reduced = new ObjectArrayList<>();
        for (ItemStack stack : items) {
            if (!stack.isEmpty() && stack.getRarity().ordinal() >= net.minecraft.world.item.Rarity.RARE.ordinal()) {
                // 10% chance to lose each rare item (keepChance = 0.9)
                if (Math.random() >= keepChance) continue;
            }
            reduced.add(stack);
        }
        return reduced;
    }

    @SuppressWarnings("unchecked")
    private static ObjectArrayList<ItemStack> callGetRandomItems(LootTable table, LootContext context) {
        try {
            if (getRandomItemsMethod == null) {
                getRandomItemsMethod = LootTable.class.getDeclaredMethod("getRandomItems", LootContext.class);
                getRandomItemsMethod.setAccessible(true);
            }
            return (ObjectArrayList<ItemStack>) getRandomItemsMethod.invoke(table, context);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static LootTable getRandomChestLootTable(ServerLevel level) {
        MinecraftServer server = level.getServer();
        if (chestLootTableIds == null || cachedServer != server) {
            cachedServer = server;
            chestLootTableIds = new ArrayList<>();
            try {
                LootDataManager manager = server.getLootData();
                Field field = LootDataManager.class.getDeclaredField("elements");
                field.setAccessible(true);
                Map<ResourceLocation, LootTable> map = (Map<ResourceLocation, LootTable>) field.get(manager);
                for (ResourceLocation key : map.keySet()) {
                    if (key.getPath().startsWith("chests/")) {
                        chestLootTableIds.add(key);
                    }
                }
            } catch (Exception ignored) {}
        }
        if (chestLootTableIds.isEmpty()) return null;
        ResourceLocation randomId = chestLootTableIds.get(level.random.nextInt(chestLootTableIds.size()));
        return server.getLootData().getLootTable(randomId);
    }

    private static int rarityScore(ObjectArrayList<ItemStack> items) {
        int score = 0;
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                score += stack.getRarity().ordinal() * stack.getCount();
            }
        }
        return score;
    }
}
