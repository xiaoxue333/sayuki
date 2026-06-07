package com.xiaoxue.sayuki.recipe;

import com.xiaoxue.sayuki.item.ModItems;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class PhilosophersStoneRecipe extends CustomRecipe {

    public PhilosophersStoneRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        boolean foundStone = false;
        boolean foundMineral = false;
        int total = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;
            total++;
            if (total > 2) return false;

            if (stack.getItem() == ModItems.PHILOSOPHERS_STONE.get()) {
                if (foundStone || stack.getDamageValue() >= stack.getMaxDamage()) return false;
                foundStone = true;
            } else if (isMineral(stack)) {
                if (foundMineral) return false;
                foundMineral = true;
            } else {
                return false;
            }
        }
        return foundStone && foundMineral;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && stack.getItem() != ModItems.PHILOSOPHERS_STONE.get()) {
                ItemStack result = stack.copy();
                result.setCount(2);
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.PHILOSOPHERS_STONE.get();
    }

    private static boolean isMineral(ItemStack stack) {
        return BuiltInRegistries.ITEM.getResourceKey(stack.getItem())
                .flatMap(key -> BuiltInRegistries.ITEM.getHolder(key))
                .map(holder -> holder.tags().anyMatch(PhilosophersStoneRecipe::isMineralTag))
                .orElse(false);
    }

    private static boolean isMineralTag(TagKey<Item> tag) {
        String path = tag.location().getPath();
        // Match forge mineral tags: ingots, gems, nuggets, dusts, ores, raw_materials, storage_blocks
        return tag.location().getNamespace().equals("forge") && (
                path.startsWith("ingots") ||
                path.startsWith("gems") ||
                path.startsWith("nuggets") ||
                path.startsWith("dusts") ||
                path.startsWith("ores") ||
                path.startsWith("raw_materials") ||
                path.startsWith("storage_blocks")
        );
    }
}
