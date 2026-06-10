/**
 * Sayuki — Tetra compat layer (non-hard dependency)
 * Uses reflection to avoid class-loading issues when Tetra is not installed.
 *
 * Tetra 1.20 class hierarchy:
 *   ItemModularHandheld (parent of all melee modular tools)
 *     ├── ModularBladedItem      (sword)
 *     ├── ModularDoubleHeadedItem (axe/hammer/etc.)
 *     └── ModularSingleHeadedItem (pickaxe/shovel/etc.)
 *   ModularBowItem       (bow, in .impl.bow sub-package)
 *   ModularCrossbowItemImpl (crossbow, in .impl.crossbow sub-package)
 */
package com.xiaoxue.sayuki.compat;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

public class TetraCompat {

    private static final boolean TETRA_LOADED = ModList.get().isLoaded("tetra");

    /** Parent class of all Tetra melee modular tools — catch-all for any hand-held Tetra weapon. */
    private static Class<?> modularHandheldClass = null;
    /** Tetra sword (blade + hilt + guard + pommel + fuller). */
    private static Class<?> modularBladedClass = null;
    /** Tetra double-headed tool (axe / hammer / clipper / etc.). */
    private static Class<?> modularDoubleHeadClass = null;
    /** Tetra single-headed tool (pickaxe / shovel / hoe). */
    private static Class<?> modularSingleHeadClass = null;
    /** Tetra modular bow. */
    private static Class<?> modularBowClass = null;
    /** Tetra modular crossbow. */
    private static Class<?> modularCrossbowClass = null;

    static {
        if (TETRA_LOADED) {
            try {
                modularHandheldClass = Class.forName("se.mickelus.tetra.items.modular.ItemModularHandheld");
            } catch (Exception ignored) {}
            try {
                modularBladedClass = Class.forName("se.mickelus.tetra.items.modular.impl.ModularBladedItem");
            } catch (Exception ignored) {}
            try {
                modularDoubleHeadClass = Class.forName("se.mickelus.tetra.items.modular.impl.ModularDoubleHeadedItem");
            } catch (Exception ignored) {}
            try {
                modularSingleHeadClass = Class.forName("se.mickelus.tetra.items.modular.impl.ModularSingleHeadedItem");
            } catch (Exception ignored) {}
            try {
                modularBowClass = Class.forName("se.mickelus.tetra.items.modular.impl.bow.ModularBowItem");
            } catch (Exception ignored) {}
            try {
                modularCrossbowClass = Class.forName("se.mickelus.tetra.items.modular.impl.crossbow.ModularCrossbowItemImpl");
            } catch (Exception ignored) {}
        }
    }

    public static boolean isLoaded() {
        return TETRA_LOADED;
    }

    // ===== Item-level checks =====

    /** True if the item is any Tetra modular melee weapon (sword/axe/pick/etc.). */
    public static boolean isMeleeItem(Item item) {
        return modularHandheldClass != null && modularHandheldClass.isInstance(item);
    }

    /** True if the item is a Tetra modular sword (ModularBladedItem). */
    public static boolean isSwordItem(Item item) {
        return modularBladedClass != null && modularBladedClass.isInstance(item);
    }

    /** True if the item is a Tetra double-headed tool (axe/hammer). */
    public static boolean isDoubleHeadItem(Item item) {
        return modularDoubleHeadClass != null && modularDoubleHeadClass.isInstance(item);
    }

    /** True if the item is a Tetra single-headed tool (pickaxe/shovel). */
    public static boolean isSingleHeadItem(Item item) {
        return modularSingleHeadClass != null && modularSingleHeadClass.isInstance(item);
    }

    /** True if the item is a Tetra modular bow. */
    public static boolean isBowItem(Item item) {
        return modularBowClass != null && modularBowClass.isInstance(item);
    }

    /** True if the item is a Tetra modular crossbow. */
    public static boolean isCrossbowItem(Item item) {
        return modularCrossbowClass != null && modularCrossbowClass.isInstance(item);
    }

    // ===== ItemStack-level convenience =====

    /** Any Tetra melee weapon (sword, axe, pickaxe, etc.) */
    public static boolean isMelee(ItemStack stack) {
        return !stack.isEmpty() && isMeleeItem(stack.getItem());
    }

    /** Tetra modular sword. */
    public static boolean isSword(ItemStack stack) {
        return !stack.isEmpty() && isSwordItem(stack.getItem());
    }

    /** Tetra double-headed tool — maps to axe behaviour (Throwing Axe, etc.) */
    public static boolean isAxe(ItemStack stack) {
        return !stack.isEmpty() && isDoubleHeadItem(stack.getItem());
    }

    /** Tetra modular bow. */
    public static boolean isBow(ItemStack stack) {
        return !stack.isEmpty() && isBowItem(stack.getItem());
    }

    /** Tetra modular crossbow. */
    public static boolean isCrossbow(ItemStack stack) {
        return !stack.isEmpty() && isCrossbowItem(stack.getItem());
    }
}
