package com.xiaoxue.sayuki.recipe;

import com.xiaoxue.sayuki.Sayuki;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Sayuki.MOD_ID);

    public static final RegistryObject<RecipeSerializer<ChoiceParadoxRecipe>> CHOICES_PARADOX =
            RECIPES.register("crafting_choices_paradox",
                    () -> new SimpleCraftingRecipeSerializer<>(ChoiceParadoxRecipe::new));

    public static final RegistryObject<RecipeSerializer<PhilosophersStoneRecipe>> PHILOSOPHERS_STONE =
            RECIPES.register("crafting_philosophers_stone",
                    () -> new SimpleCraftingRecipeSerializer<>(PhilosophersStoneRecipe::new));

    public static final RegistryObject<RecipeSerializer<DinnerboneStickRecipe>> DINNERBONE_STICK =
            RECIPES.register("crafting_dinnerbone_stick",
                    () -> new SimpleCraftingRecipeSerializer<>(DinnerboneStickRecipe::new));

    public static final RegistryObject<RecipeSerializer<CurseStickwoodRecipe>> CURSE_STICKWOOD =
            RECIPES.register("crafting_curse_stickwood",
                    () -> new SimpleCraftingRecipeSerializer<>(CurseStickwoodRecipe::new));

    public static void register(IEventBus eventBus) {
        RECIPES.register(eventBus);
    }
}
