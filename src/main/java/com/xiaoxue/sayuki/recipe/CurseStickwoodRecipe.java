package com.xiaoxue.sayuki.recipe;

import com.xiaoxue.sayuki.item.GuMu;
import com.xiaoxue.sayuki.item.ModItems;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class CurseStickwoodRecipe extends CustomRecipe {

    public CurseStickwoodRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack curseStack = ItemStack.EMPTY;
        ItemStack stickwoodStack = ItemStack.EMPTY;
        int itemCount = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                itemCount++;
                if (GuMu.isCurseItem(stack.getItem())) {
                    if (!curseStack.isEmpty()) return false;
                    curseStack = stack;
                } else if (stack.getItem() == ModItems.GU_MU.get()) {
                    if (!stickwoodStack.isEmpty()) return false;
                    stickwoodStack = stack;
                } else {
                    return false; // unknown item
                }
            }
        }

        if (itemCount != 2 || curseStack.isEmpty() || stickwoodStack.isEmpty()) return false;

        // prevent duplicate curse
        ResourceLocation curseId = BuiltInRegistries.ITEM.getKey(curseStack.getItem());
        return !GuMu.getCurses(stickwoodStack).contains(curseId.getPath());
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack curseStack = ItemStack.EMPTY;
        ItemStack stickwoodStack = ItemStack.EMPTY;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                if (GuMu.isCurseItem(stack.getItem())) {
                    curseStack = stack;
                } else if (stack.getItem() == ModItems.GU_MU.get()) {
                    stickwoodStack = stack.copy();
                }
            }
        }

        if (stickwoodStack.isEmpty() || curseStack.isEmpty()) return ItemStack.EMPTY;

        ResourceLocation curseId = BuiltInRegistries.ITEM.getKey(curseStack.getItem());
        GuMu.addCurse(stickwoodStack, curseId.getPath());
        return stickwoodStack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.CURSE_STICKWOOD.get();
    }
}
