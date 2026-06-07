/**
 * Sayuki — Creative Mode Tabs
 * tab1: sayuki_tab — basic blocks/items (unchanged)
 * tab2: sayuki_sts_tab — character relics (icon: burning_blood)
 * tab3: sayuki_ancient_tab — Ancient Ones relics, starting with Vakuu (icon: whispering_earring)
 */
package com.xiaoxue.sayuki.item;

import com.xiaoxue.sayuki.Sayuki;
import com.xiaoxue.sayuki.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTab {
    public final static DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Sayuki.MOD_ID);

    // ===== Tab 1: 咲雪随想 (basic blocks/items) =====

    public final static RegistryObject<CreativeModeTab> SAYUKI_TAB =
            CREATIVE_MODE_TABS.register("sayuki_tab", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.STELLAR_JADE.get()))
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

    // ===== Tab 2: 角色遗物列表 (5 roles × 9 relics = 45, icon: burning_blood) =====

    public final static RegistryObject<CreativeModeTab> SAYUKI_STS_TAB =
            CREATIVE_MODE_TABS.register("sayuki_sts_tab", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.BURNING_BLOOD.get()))
                    .title(Component.translatable("itemGroup.sayuki_sts_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        // Ironclad
                        pOutput.accept(ModItems.BURNING_BLOOD.get());
                        pOutput.accept(ModItems.BLACK_BLOOD.get());
                        pOutput.accept(ModItems.RED_SKULL.get());
                        pOutput.accept(ModItems.PAPER_PHROG.get());
                        pOutput.accept(ModItems.SELF_FORMING_CLAY.get());
                        pOutput.accept(ModItems.CHARONS_ASHES.get());
                        pOutput.accept(ModItems.DEMON_TONGUE.get());
                        pOutput.accept(ModItems.RUINED_HELMET.get());
                        pOutput.accept(ModItems.BRIMSTONE.get());
                        // Silent
                        pOutput.accept(ModItems.RING_OF_THE_SNAKE.get());
                        pOutput.accept(ModItems.RING_OF_THE_DRAKE.get());
                        pOutput.accept(ModItems.SNECKO_SKULL.get());
                        pOutput.accept(ModItems.TINGSHA.get());
                        pOutput.accept(ModItems.TWISTED_FUNNEL.get());
                        pOutput.accept(ModItems.HELICAL_DART.get());
                        pOutput.accept(ModItems.PAPER_KRANE.get());
                        pOutput.accept(ModItems.TOUGH_BANDAGES.get());
                        pOutput.accept(ModItems.NINJA_SCROLL.get());
                        // Regent
                        pOutput.accept(ModItems.DIVINE_RIGHT.get());
                        pOutput.accept(ModItems.DIVINE_DESTINY.get());
                        pOutput.accept(ModItems.FENCING_MANUAL.get());
                        pOutput.accept(ModItems.GALACTIC_DUST.get());
                        pOutput.accept(ModItems.REGALITE.get());
                        pOutput.accept(ModItems.LUNAR_PASTRY.get());
                        pOutput.accept(ModItems.MINI_REGENT.get());
                        pOutput.accept(ModItems.ORANGE_DOUGH.get());
                        pOutput.accept(ModItems.VITRUVIAN_MINION.get());
                        // Necrobinder
                        pOutput.accept(ModItems.BOUND_PHYLACTERY.get());
                        pOutput.accept(ModItems.PHYLACTERY_UNBOUND.get());
                        pOutput.accept(ModItems.BONE_FLUTE.get());
                        pOutput.accept(ModItems.BOOK_REPAIR_KNIFE.get());
                        pOutput.accept(ModItems.FUNERARY_MASK.get());
                        pOutput.accept(ModItems.BIG_HAT.get());
                        pOutput.accept(ModItems.BOOKMARK.get());
                        pOutput.accept(ModItems.IVORY_TILE.get());
                        pOutput.accept(ModItems.UNDYING_SIGIL.get());
                        // Defect
                        pOutput.accept(ModItems.CRACKED_CORE.get());
                        pOutput.accept(ModItems.INFUSED_CORE.get());
                        pOutput.accept(ModItems.GOLD_PLATED_CABLES.get());
                        pOutput.accept(ModItems.DATA_DISK.get());
                        pOutput.accept(ModItems.SYMBIOTIC_VIRUS.get());
                        pOutput.accept(ModItems.EMOTION_CHIP.get());
                        pOutput.accept(ModItems.METRONOME.get());
                        pOutput.accept(ModItems.RUNIC_CAPACITOR.get());
                        pOutput.accept(ModItems.POWER_CELL.get());
                    }).build());

    // ===== Tab 3: 先古之民 (Vakuu + DARV relics, icon: whispering_earring) =====

    public final static RegistryObject<CreativeModeTab> SAYUKI_ANCIENT_TAB =
            CREATIVE_MODE_TABS.register("sayuki_ancient_tab", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.WHISPERING_EARRING.get()))
                    .title(Component.translatable("itemGroup.sayuki_ancient_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        // Vakuu
                        pOutput.accept(ModItems.WHISPERING_EARRING.get());
                        pOutput.accept(ModItems.BLOOD_SOAKED_ROSE.get());
                        pOutput.accept(ModItems.CHOICES_PARADOX.get());
                        pOutput.accept(ModItems.DISTINGUISHED_CAPE.get());
                        pOutput.accept(ModItems.FIDDLE.get());
                        pOutput.accept(ModItems.JEWELED_MASK.get());
                        pOutput.accept(ModItems.LORDS_PARASOL.get());
                        pOutput.accept(ModItems.MUSIC_BOX.get());
                        pOutput.accept(ModItems.PRESERVED_FOG.get());
                        pOutput.accept(ModItems.SERE_TALON.get());
                        // DARV
                        pOutput.accept(ModItems.ASTROLABE.get());
                        pOutput.accept(ModItems.BLACK_STAR.get());
                        pOutput.accept(ModItems.CALLING_BELL.get());
                        pOutput.accept(ModItems.DUSTY_TOME.get());
                        pOutput.accept(ModItems.ECTOPLASM.get());
                        pOutput.accept(ModItems.EMPTY_CAGE.get());
                        pOutput.accept(ModItems.PANDORAS_BOX.get());
                        pOutput.accept(ModItems.PHILOSOPHERS_STONE.get());
                        pOutput.accept(ModItems.RUNIC_PYRAMID.get());
                        pOutput.accept(ModItems.SNECKO_EYE.get());
                        pOutput.accept(ModItems.SOZU.get());
                        pOutput.accept(ModItems.VELVET_CHOKER.get());
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
