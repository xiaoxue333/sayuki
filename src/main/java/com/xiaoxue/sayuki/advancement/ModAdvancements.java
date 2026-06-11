/**
 * Sayuki — Advancement triggers registry.
 * Provides centralized access to all custom criterion triggers used by Sayuki advancements.
 */
package com.xiaoxue.sayuki.advancement;

import com.xiaoxue.sayuki.Sayuki;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;

@SuppressWarnings("removal")
public class ModAdvancements {

    /* Triggers — register via CriteriaTriggers on common setup */

    /** Fires when the player equips a relic. Supports optional item filter. */
    public static final SayukiCriterionTrigger RELIC_EQUIPPED =
            new SayukiCriterionTrigger(loc("relic_equipped"));

    /** Fires when the player unequips a relic. */
    public static final SayukiCriterionTrigger RELIC_UNEQUIPPED =
            new SayukiCriterionTrigger(loc("relic_unequipped"));

    /** Fires when the player kills a boss. Supports optional item filter (held weapon). */
    public static final SayukiCriterionTrigger BOSS_KILLED =
            new SayukiCriterionTrigger(loc("boss_killed"));

    /** Fires when the player kills a boss while specific item conditions are met. */
    public static final SayukiCriterionTrigger BOSS_KILLED_WITH =
            new SayukiCriterionTrigger(loc("boss_killed_with"));

    /** Fires on any mod-specific event (generic). */
    public static final SayukiCriterionTrigger GENERIC_EVENT =
            new SayukiCriterionTrigger(loc("generic_event"));

    // ──── Registration ────

    public static void register() {
        CriteriaTriggers.register(RELIC_EQUIPPED);
        CriteriaTriggers.register(RELIC_UNEQUIPPED);
        CriteriaTriggers.register(BOSS_KILLED);
        CriteriaTriggers.register(BOSS_KILLED_WITH);
        CriteriaTriggers.register(GENERIC_EVENT);
    }

    // ──── Convenience helpers ────

    public static void triggerRelicEquipped(ServerPlayer player, @Nullable Item item) {
        RELIC_EQUIPPED.trigger(player, item);
    }

    public static void triggerRelicUnequipped(ServerPlayer player, @Nullable Item item) {
        RELIC_UNEQUIPPED.trigger(player, item);
    }

    public static void triggerBossKilled(ServerPlayer player, @Nullable Item heldItem) {
        BOSS_KILLED.trigger(player, heldItem);
        BOSS_KILLED_WITH.trigger(player, heldItem);
    }

    public static void triggerGeneric(ServerPlayer player, @Nullable Item item) {
        GENERIC_EVENT.trigger(player, item);
    }

    private static ResourceLocation loc(String name) {
        return new ResourceLocation(Sayuki.MOD_ID, name);
    }
}
