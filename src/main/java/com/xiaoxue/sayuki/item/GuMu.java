package com.xiaoxue.sayuki.item;

import com.xiaoxue.sayuki.enchantment.ModEnchantments;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GuMu extends Item implements ICurioItem {

    public static final String CURSE_LIST_KEY = "SayukiCurses";
    public static final String BLIGHT_LIST_KEY = "SayukiBlights";

    public static final Set<Item> CURSE_ITEMS = Set.of(
            ModItems.ASCENDERS_BANE.get(), ModItems.BAD_LUCK.get(), ModItems.CLUMSY.get(),
            ModItems.CURSE_OF_THE_BELL.get(), ModItems.DEBT.get(), ModItems.DECAY.get(),
            ModItems.DOUBT.get(), ModItems.ENTHRALLED.get(), ModItems.FOLLY.get(),
            ModItems.GREED.get(), ModItems.GUILTY.get(), ModItems.INJURY.get(),
            ModItems.NORMALITY.get(), ModItems.POOR_SLEEP.get(), ModItems.REGRET.get(),
            ModItems.SHAME.get(), ModItems.SPORE_MIND.get(), ModItems.WRITHE.get()
    );

    /** All 13 desolate plague (荒疫) items — applied at doom tier >= 10. */
    public static final Set<Item> BLIGHT_ITEMS = Set.of(
            ModItems.ACCURSED.get(), ModItems.ANCIENT.get(), ModItems.DURIAN.get(),
            ModItems.HAUNTINGS.get(), ModItems.MAZE.get(), ModItems.MIMIC.get(),
            ModItems.MUZZLE.get(), ModItems.SCATTER.get(), ModItems.SHIELD.get(),
            ModItems.SPEAR.get(), ModItems.TROPHY.get(), ModItems.TWIST.get(),
            ModItems.VOID.get()
    );

    /** 荒疫箱 — 7 blight items drawn randomly each round (no effect yet). */
    public static final Set<Item> BLIGHT_POOL = Set.of(
            ModItems.HAUNTINGS.get(),  // 阴魂不散
            ModItems.DURIAN.get(),     // 疫后榴莲
            ModItems.ACCURSED.get(),   // 恶灵附身
            ModItems.SCATTER.get(),    // 胡思乱想
            ModItems.TWIST.get(),      // 心智扭曲
            ModItems.ANCIENT.get(),    // 先古强化
            ModItems.VOID.get()        // 虚无结晶
    );

    /** Curse IDs that cannot be removed by any means — like curse_of_the_bell. */
    public static final Set<String> UNREMOVABLE_CURSES = Set.of(
            "curse_of_the_bell", "bad_luck", "ascenders_bane", "enthralled", "folly"
    );

    public GuMu(Properties properties) {
        super(properties);
    }

    // === NBT helpers ===

    public static List<String> getCurses(ItemStack stack) {
        List<String> list = new ArrayList<>();
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(CURSE_LIST_KEY, Tag.TAG_LIST)) {
            ListTag curseList = tag.getList(CURSE_LIST_KEY, Tag.TAG_STRING);
            for (int i = 0; i < curseList.size(); i++) {
                list.add(curseList.getString(i));
            }
        }
        return list;
    }

    public static void addCurse(ItemStack stack, String curseId) {
        // Disallow duplicate curses
        if (getCurses(stack).contains(curseId)) return;
        CompoundTag tag = stack.getOrCreateTag();
        ListTag curseList;
        if (tag.contains(CURSE_LIST_KEY, Tag.TAG_LIST)) {
            curseList = tag.getList(CURSE_LIST_KEY, Tag.TAG_STRING);
        } else {
            curseList = new ListTag();
        }
        curseList.add(StringTag.valueOf(curseId));
        tag.put(CURSE_LIST_KEY, curseList);
        syncCurseEnchantments(stack);
    }

    /** Remove a curse by ID — first occurrence (for targeted removal). Unremovable curses are skipped. */
    public static void removeCurse(ItemStack stack, String curseId) {
        if (UNREMOVABLE_CURSES.contains(curseId)) return;
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(CURSE_LIST_KEY, Tag.TAG_LIST)) return;
        ListTag curseList = tag.getList(CURSE_LIST_KEY, Tag.TAG_STRING);
        for (int i = 0; i < curseList.size(); i++) {
            if (curseList.getString(i).equals(curseId)) {
                curseList.remove(i);
                break;
            }
        }
        syncCurseEnchantments(stack);
    }

    /** Remove the last removable curse (LIFO) — unremovable curses are skipped. Returns true if something was removed. */
    public static boolean removeLastCurse(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(CURSE_LIST_KEY, Tag.TAG_LIST)) return false;
        ListTag curseList = tag.getList(CURSE_LIST_KEY, Tag.TAG_STRING);
        for (int i = curseList.size() - 1; i >= 0; i--) {
            if (!UNREMOVABLE_CURSES.contains(curseList.getString(i))) {
                curseList.remove(i);
                syncCurseEnchantments(stack);
                return true;
            }
        }
        return false; // all curses are unremovable
    }

    /**
     * Sync the vanilla Enchantments NBT with the SayukiCurses list.
     * Each curse layer appears as one sayuki:doom_curse entry (level 1),
     * so external mods (千咒卷轴, 暴戾之咒, etc.) can detect/remove them.
     *
     * Called after addCurse/removeCurse/removeLastCurse.
     * Also called on player tick to detect external removals and apply LIFO.
     */
    public static void syncCurseEnchantments(ItemStack stack) {
        int curseCount = getCurses(stack).size();
        Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);

        // Count existing doom_curse entries in vanilla NBT
        int vanillaCurseCount = 0;
        for (var entry : enchants.entrySet()) {
            if (entry.getKey() == ModEnchantments.DOOM_CURSE.get()) {
                vanillaCurseCount += entry.getValue();
            }
        }

        // If vanilla has fewer than SayukiCurses, rebuild: one doom_curse per curse layer
        if (vanillaCurseCount != curseCount) {
            // Remove all existing doom_curse entries
            enchants.entrySet().removeIf(e -> e.getKey() == ModEnchantments.DOOM_CURSE.get());
            // Add one level 1 entry per curse layer
            if (curseCount > 0) {
                enchants.put(ModEnchantments.DOOM_CURSE.get(), curseCount);
            }
            EnchantmentHelper.setEnchantments(enchants, stack);
        }
    }

    /**
     * Check if external mods removed doom_curse from the vanilla Enchantments NBT,
     * and apply LIFO removal to SayukiCurses accordingly.
     * Returns true if a LIFO removal was performed.
     */
    public static boolean syncExternalCurseRemoval(ItemStack stack) {
        int curseCount = getCurses(stack).size();
        Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);

        int vanillaCurseCount = 0;
        for (var entry : enchants.entrySet()) {
            if (entry.getKey() == ModEnchantments.DOOM_CURSE.get()) {
                vanillaCurseCount += entry.getValue();
            }
        }

        if (vanillaCurseCount < curseCount) {
            // External mod removed curse(s) — apply LIFO removal skipping unremovable curses
            int toRemove = curseCount - vanillaCurseCount;
            int removed = 0;
            for (int i = 0; i < toRemove; i++) {
                if (!removeLastCurse(stack)) break; // stop if all remaining are unremovable
                removed++;
            }
            // Only re-sync if we actually removed something
            if (removed > 0) {
                syncCurseEnchantments(stack);
            }
            return true;
        }
        return false;
    }

    /** Returns the number of occurrences of a specific curse. */
    public static int getCurseCount(ItemStack stack, String curseId) {
        int count = 0;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(CURSE_LIST_KEY, Tag.TAG_LIST)) {
            ListTag curseList = tag.getList(CURSE_LIST_KEY, Tag.TAG_STRING);
            for (int i = 0; i < curseList.size(); i++) {
                if (curseList.getString(i).equals(curseId)) count++;
            }
        }
        return count;
    }

    /** Returns the number of occurrences of a specific curse for the player's GuMu. */
    public static int getCurseCountForPlayer(Player player, String curseId) {
        var curio = CuriosApi.getCuriosInventory(player).resolve();
        if (curio.isEmpty()) return 0;
        var guMu = curio.get().findFirstCurio(
                stack -> stack.getItem() == ModItems.GU_MU.get());
        if (guMu.isEmpty()) return 0;
        return getCurseCount(guMu.get().stack(), curseId);
    }

    public static boolean isCurseItem(Item item) {
        return CURSE_ITEMS.contains(item);
    }

    // === Blight (荒疫) helpers ===

    public static List<String> getBlights(ItemStack stack) {
        List<String> list = new ArrayList<>();
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(BLIGHT_LIST_KEY, Tag.TAG_LIST)) {
            ListTag blightList = tag.getList(BLIGHT_LIST_KEY, Tag.TAG_STRING);
            for (int i = 0; i < blightList.size(); i++) {
                list.add(blightList.getString(i));
            }
        }
        return list;
    }

    /** Add one blight to the GuMu. Blights stack infinitely. */
    public static void addBlight(ItemStack stack, String blightId) {
        CompoundTag tag = stack.getOrCreateTag();
        ListTag blightList;
        if (tag.contains(BLIGHT_LIST_KEY, Tag.TAG_LIST)) {
            blightList = tag.getList(BLIGHT_LIST_KEY, Tag.TAG_STRING);
        } else {
            blightList = new ListTag();
        }
        blightList.add(StringTag.valueOf(blightId));
        tag.put(BLIGHT_LIST_KEY, blightList);
    }

    /** Remove all occurrences of a specific blight. Returns how many were removed. */
    public static int removeAllBlight(ItemStack stack, String blightId) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(BLIGHT_LIST_KEY, Tag.TAG_LIST)) return 0;
        ListTag blightList = tag.getList(BLIGHT_LIST_KEY, Tag.TAG_STRING);
        int removed = 0;
        for (int i = blightList.size() - 1; i >= 0; i--) {
            if (blightList.getString(i).equals(blightId)) {
                blightList.remove(i);
                removed++;
            }
        }
        return removed;
    }

    /** Returns the total number of blights applied. */
    public static int getTotalBlightCount(ItemStack stack) {
        return getBlights(stack).size();
    }

    /** Returns the number of occurrences of a specific blight for the player's GuMu. */
    public static int getBlightCountForPlayer(Player player, String blightId) {
        var curio = CuriosApi.getCuriosInventory(player).resolve();
        if (curio.isEmpty()) return 0;
        var guMu = curio.get().findFirstCurio(
                stack -> stack.getItem() == ModItems.GU_MU.get());
        if (guMu.isEmpty()) return 0;
        int count = 0;
        CompoundTag tag = guMu.get().stack().getTag();
        if (tag != null && tag.contains(BLIGHT_LIST_KEY, Tag.TAG_LIST)) {
            ListTag blightList = tag.getList(BLIGHT_LIST_KEY, Tag.TAG_STRING);
            for (int i = 0; i < blightList.size(); i++) {
                if (blightList.getString(i).equals(blightId)) count++;
            }
        }
        return count;
    }

    public static boolean isBlightItem(Item item) {
        return BLIGHT_ITEMS.contains(item);
    }

    // === Blight round & effect multipliers ===

    /** Get the current blight round for a player (= spear count). */
    public static int getBlightRound(Player player) {
        return getBlightCountForPlayer(player, "spear");
    }

    /**
     * 荒疫之矛 attack damage multiplier.
     * Round 1: +100% → 2.0, Round 2: +175% → 2.75,
     * Round 3: +250% → 3.5, Round n (n>=4): 3.5 + (n-3)*0.75
     */
    public static double getSpearMultiplier(int round) {
        if (round <= 0) return 1.0;
        if (round == 1) return 2.0;
        if (round == 2) return 2.75;
        if (round == 3) return 3.5;
        return 3.5 + (round - 3) * 0.75;
    }

    /**
     * 荒疫之盾 health multiplier.
     * Round 1: +50% → 1.5, Round 2: +100% → 2.0,
     * Round 3: +150% → 2.5, Round n (n>=4): 2.5 + (n-3)*0.5
     */
    public static double getShieldMultiplier(int round) {
        if (round <= 0) return 1.0;
        if (round == 1) return 1.5;
        if (round == 2) return 2.0;
        if (round == 3) return 2.5;
        return 2.5 + (round - 3) * 0.5;
    }

    // === Doom Tier utility ===

    /**
     * Get the doom tier (number of active curses) for a player equipped with GuMu.
     * Returns 0 if no GuMu equipped in curios.
     */
    public static int getDoomTier(Player player) {
        var curio = CuriosApi.getCuriosInventory(player).resolve();
        if (curio.isEmpty()) return 0;
        var guMu = curio.get().findFirstCurio(
                stack -> stack.getItem() == ModItems.GU_MU.get());
        if (guMu.isEmpty()) return 0;
        return getCurses(guMu.get().stack()).size();
    }

    /**
     * Get the highest doom tier among players within radius of a position.
     * Used for proximity-based world effects (spawn, loot, enemy buffs).
     * For multi-player: use {@link #getSumDoomTierNearby} instead.
     */
    public static int getMaxDoomTierNearby(ServerLevel level, Vec3 pos, double radius) {
        int maxTier = 0;
        for (Player player : level.players()) {
            if (player.distanceToSqr(pos) > radius * radius) continue;
            int tier = getDoomTier(player);
            if (tier > maxTier) maxTier = tier;
        }
        return maxTier;
    }

    /**
     * Get the sum of doom tiers among all players within radius of a position.
     * Multi-player: 2 players with tier 5 + 3 = global tier 8.
     */
    public static int getSumDoomTierNearby(ServerLevel level, Vec3 pos, double radius) {
        int sum = 0;
        for (Player player : level.players()) {
            if (player.distanceToSqr(pos) > radius * radius) continue;
            sum += getDoomTier(player);
        }
        return sum;
    }

    /**
     * Check if a player with doom tier >= threshold is within radius.
     */
    public static boolean isDoomTierNearby(ServerLevel level, Vec3 pos, double radius, int threshold) {
        return getMaxDoomTierNearby(level, pos, radius) >= threshold;
    }

    // === ICurioItem ===

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        // First equip one-time popup
        if (slotContext.entity() instanceof Player player && !player.level().isClientSide()) {
            if (!player.getPersistentData().getBoolean("SayukiGuMuFirstEquip")) {
                player.getPersistentData().putBoolean("SayukiGuMuFirstEquip", true);
                player.displayClientMessage(
                        Component.literal("麻溜点发我五块⁄(⁄ ⁄⁄ω⁄ ⁄ ⁄)⁄"), false);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        // curse effects removed via ModEventHandler tick
    }

    // === Tooltip ===

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.gu_mu.1"));

        List<String> curses = getCurses(stack);
        tooltip.add(Component.translatable("tooltip.sayuki.gu_mu.tier", curses.size()));

        if (!curses.isEmpty()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("tooltip.sayuki.gu_mu.curses_header"));
            // Horizontal layout: 5 curses per line
            for (int i = 0; i < curses.size(); i++) {
                int lineStart = i / 5 * 5;
                if (i == lineStart) {
                    MutableComponent line = Component.literal(" §7- ")
                            .append(Component.translatable("item.sayuki." + curses.get(lineStart)));
                    for (int j = lineStart + 1; j < Math.min(lineStart + 5, curses.size()); j++) {
                        line.append(", ").append(Component.translatable("item.sayuki." + curses.get(j)));
                    }
                    tooltip.add(line);
                }
            }
        }

        List<String> blights = getBlights(stack);
        if (!blights.isEmpty()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("tooltip.sayuki.gu_mu.blights_header"));
            // Actual blight icon entries are added client-side via
            // RenderTooltipEvent.GatherComponents and BlightIconTooltipData.
        }
    }
}
