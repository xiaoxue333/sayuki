package com.xiaoxue.sayuki.recipe;

import com.xiaoxue.sayuki.item.ModItems;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ChoiceParadoxRecipe extends CustomRecipe {

    private static final ItemStack[] RESULTS = new ItemStack[]{
            new ItemStack(ModItems.JEWELED_MASK.get()),          // (0,0)
            new ItemStack(ModItems.WHISPERING_EARRING.get()),    // (1,0)
            new ItemStack(ModItems.LORDS_PARASOL.get()),         // (2,0)
            new ItemStack(ModItems.FIDDLE.get()),                // (0,1)
            new ItemStack(ModItems.BLOOD_SOAKED_ROSE.get()),     // (1,1)
            new ItemStack(ModItems.PRESERVED_FOG.get()),         // (2,1)
            new ItemStack(ModItems.MUSIC_BOX.get()),             // (0,2)
            new ItemStack(ModItems.SERE_TALON.get()),            // (1,2)
            new ItemStack(ModItems.DISTINGUISHED_CAPE.get()),    // (2,2)
    };

    public ChoiceParadoxRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        int count = 0;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() != ModItems.CHOICES_PARADOX.get()) return false;
                count++;
            }
        }
        return count == 1;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        for (int i = 0; i < 9; i++) {
            if (!container.getItem(i).isEmpty()) {
                return RESULTS[i].copy();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.CHOICES_PARADOX.get();
    }
}
