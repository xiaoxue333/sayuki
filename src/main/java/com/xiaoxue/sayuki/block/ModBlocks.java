/**
 * Sayuki — Block registration (stellar_jade_ore, stellar_jade_block)
 * Compat: Goety-2 and IronsSpellbooks — uses sayuki: namespace blocks, no name clash
 */
package com.xiaoxue.sayuki.block;

import com.xiaoxue.sayuki.Sayuki;
import com.xiaoxue.sayuki.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Sayuki.MOD_ID);

    public static final RegistryObject<Block> STELLAR_JADE_ORE = registerBlock("stellar_jade_ore",
            () -> new StellarJadeOreBlock(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)
                    .strength(4.0F, 3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(StellarJadeOreBlock.STELLAR_JADE_ORE_SOUND)));

    public static final RegistryObject<Block> STELLAR_JADE_BLOCK = registerBlock("stellar_jade_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0F, 6.0F)
                    .requiresCorrectToolForDrops()));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}