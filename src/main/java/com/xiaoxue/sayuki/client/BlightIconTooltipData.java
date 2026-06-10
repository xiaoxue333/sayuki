package com.xiaoxue.sayuki.client;

import com.xiaoxue.sayuki.item.ModItems;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

/**
 * Data carrier for a single blight tooltip entry with icon.
 * Used by RenderTooltipEvent.GatherComponents to inject icon tooltips.
 */
public record BlightIconTooltipData(String blightId, int count) implements TooltipComponent {

    public ItemStack icon() {
        return switch (blightId) {
            case "spear"   -> new ItemStack(ModItems.SPEAR.get());
            case "shield"  -> new ItemStack(ModItems.SHIELD.get());
            case "mimic"   -> new ItemStack(ModItems.MIMIC.get());
            case "maze"    -> new ItemStack(ModItems.MAZE.get());
            case "muzzle"  -> new ItemStack(ModItems.MUZZLE.get());
            case "trophy"  -> new ItemStack(ModItems.TROPHY.get());
            case "ancient" -> new ItemStack(ModItems.ANCIENT.get());
            case "hauntings" -> new ItemStack(ModItems.HAUNTINGS.get());
            case "durian"  -> new ItemStack(ModItems.DURIAN.get());
            case "accursed" -> new ItemStack(ModItems.ACCURSED.get());
            case "scatter" -> new ItemStack(ModItems.SCATTER.get());
            case "twist"   -> new ItemStack(ModItems.TWIST.get());
            case "void"    -> new ItemStack(ModItems.VOID.get());
            default        -> ItemStack.EMPTY;
        };
    }
}
