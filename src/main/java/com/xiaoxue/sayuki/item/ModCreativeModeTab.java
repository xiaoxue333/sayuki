/**
 * Sayuki — Creative Mode Tab (sayuki_tab)
 * Compat: Goety-2 and IronsSpellbooks — independent creative tabs, no conflict
 */
package com.xiaoxue.sayuki.item;

import com.xiaoxue.sayuki.Sayuki;
import com.xiaoxue.sayuki.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.List;

public class ModCreativeModeTab {
    public final static DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS=
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Sayuki.MOD_ID);

    private static final List<RegistryObject<Item>> STS_ITEMS = Arrays.asList(
            ModItems.WHISPERING_EARRING,
            ModItems.BOUND_PHYLACTERY,
            ModItems.PHYLACTERY_UNBOUND,
            ModItems.BURNING_BLOOD,
            ModItems.BLACK_BLOOD,
            ModItems.DIVINE_RIGHT,
            ModItems.DIVINE_DESTINY,
            ModItems.RING_OF_THE_SNAKE,
            ModItems.RING_OF_THE_DRAKE,
            ModItems.CRACKED_CORE,
            ModItems.INFUSED_CORE,
            ModItems.GOLD_PLATED_CABLES,
            ModItems.DATA_DISK,
            ModItems.SYMBIOTIC_VIRUS,
            ModItems.EMOTION_CHIP,
            ModItems.METRONOME,
            ModItems.RUNIC_CAPACITOR,
            ModItems.POWER_CELL,
            ModItems.BONE_FLUTE,
            ModItems.BOOK_REPAIR_KNIFE,
            ModItems.FUNERARY_MASK,
            ModItems.BIG_HAT,
            ModItems.BOOKMARK,
            ModItems.IVORY_TILE,
            ModItems.UNDYING_SIGIL,
            ModItems.FENCING_MANUAL,
            ModItems.REGALITE,
            ModItems.LUNAR_PASTRY,
            ModItems.ORANGE_DOUGH,
            ModItems.VITRUVIAN_MINION,
            ModItems.MINI_REGENT,
            ModItems.GALACTIC_DUST
    );

    public final static RegistryObject<CreativeModeTab> SAYUKI_TAB =
            CREATIVE_MODE_TABS.register("sayuki_tab", ()-> CreativeModeTab.builder()
                    .icon(()-> new ItemStack(ModItems.STELLAR_JADE.get()))
                    .title(Component.translatable("itemGroup.sayuki_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.STELLAR_JADE.get());
                        pOutput.accept(ModItems.RAW_STELLAR_JADE.get());
                        pOutput.accept(ModItems.STELLAR_JADE_GEM.get());
                        pOutput.accept(ModBlocks.STELLAR_JADE_ORE.get());
                        pOutput.accept(ModBlocks.STELLAR_JADE_BLOCK.get());
                        pOutput.accept(ModItems.VOODOO_RING.get());
                        pOutput.accept(ModItems.FRUSTA_DOMINATE.get());
                        pOutput.accept(ModItems.AZURE_SWORD.get());
                        pOutput.accept(ModItems.MAGENTA_SPEAR.get());
                        pOutput.accept(ModItems.HEART_GRENADE.get());
                        pOutput.accept(ModItems.HEAVEN_EAR_ORNAMENTS.get());
                        pOutput.accept(ModItems.CHERRY_EAR_ORNAMENTS.get());
                        pOutput.accept(ModItems.HEART_EAR_ORNAMENTS.get());
                    }).build());

    public final static RegistryObject<CreativeModeTab> SAYUKI_STS_TAB =
            CREATIVE_MODE_TABS.register("sayuki_sts_tab", ()-> CreativeModeTab.builder()
                    .icon(()-> new ItemStack(ModItems.WHISPERING_EARRING.get()))
                    .title(Component.translatable("itemGroup.sayuki_sts_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.WHISPERING_EARRING.get());
                        pOutput.accept(ModItems.BOUND_PHYLACTERY.get());
                        pOutput.accept(ModItems.PHYLACTERY_UNBOUND.get());
                        pOutput.accept(ModItems.BURNING_BLOOD.get());
                        pOutput.accept(ModItems.BLACK_BLOOD.get());
                        pOutput.accept(ModItems.DIVINE_RIGHT.get());
                        pOutput.accept(ModItems.DIVINE_DESTINY.get());
                        pOutput.accept(ModItems.RING_OF_THE_SNAKE.get());
                        pOutput.accept(ModItems.RING_OF_THE_DRAKE.get());
                        pOutput.accept(ModItems.CRACKED_CORE.get());
                        pOutput.accept(ModItems.INFUSED_CORE.get());
                        pOutput.accept(ModItems.GOLD_PLATED_CABLES.get());
                        pOutput.accept(ModItems.DATA_DISK.get());
                        pOutput.accept(ModItems.SYMBIOTIC_VIRUS.get());
                        pOutput.accept(ModItems.EMOTION_CHIP.get());
                        pOutput.accept(ModItems.METRONOME.get());
                        pOutput.accept(ModItems.RUNIC_CAPACITOR.get());
                        pOutput.accept(ModItems.POWER_CELL.get());
                        pOutput.accept(ModItems.BONE_FLUTE.get());
                        pOutput.accept(ModItems.BOOK_REPAIR_KNIFE.get());
                        pOutput.accept(ModItems.FUNERARY_MASK.get());
                        pOutput.accept(ModItems.BIG_HAT.get());
                        pOutput.accept(ModItems.BOOKMARK.get());
                        pOutput.accept(ModItems.IVORY_TILE.get());
                        pOutput.accept(ModItems.UNDYING_SIGIL.get());
                        pOutput.accept(ModItems.FENCING_MANUAL.get());
                        pOutput.accept(ModItems.REGALITE.get());
                        pOutput.accept(ModItems.LUNAR_PASTRY.get());
                        pOutput.accept(ModItems.ORANGE_DOUGH.get());
                        pOutput.accept(ModItems.VITRUVIAN_MINION.get());
                        pOutput.accept(ModItems.MINI_REGENT.get());
                        pOutput.accept(ModItems.GALACTIC_DUST.get());
                    }).build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
