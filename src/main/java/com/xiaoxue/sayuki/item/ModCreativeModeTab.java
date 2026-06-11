/**
 * Sayuki — Creative Mode Tabs
 * tab1: sayuki_tab — basic blocks/items (unchanged)
 * tab2: sayuki_sts_tab — character relics (icon: burning_blood)
 * tab3: sayuki_ancient_tab — Ancient Ones relics, starting with Vakuu (icon: whispering_earring)
 * tab4: sayuki_curse_tab — Tower Curse relics (icon: cursed_pearl)
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

@SuppressWarnings({ "unchecked", "rawtypes", "removal", "deprecation" })
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
                        // New relics
                        pOutput.accept(ModItems.CLAWS.get());
                        pOutput.accept(ModItems.CROSSBOW.get());
                        pOutput.accept(ModItems.IRON_CLUB.get());
                        pOutput.accept(ModItems.MEAT_CLEAVER.get());
                        pOutput.accept(ModItems.SAI.get());
                        pOutput.accept(ModItems.SPIKED_GAUNTLETS.get());
                        pOutput.accept(ModItems.TANXS_WHISTLE.get());
                        pOutput.accept(ModItems.THROWING_AXE.get());
                        pOutput.accept(ModItems.TRI_BOOMERANG.get());
                        pOutput.accept(ModItems.WAR_HAMMER.get());
                        // Beauty
                        pOutput.accept(ModItems.BEAUTIFUL_BRACELET.get());
                        pOutput.accept(ModItems.BLESSED_ANTLER.get());
                        pOutput.accept(ModItems.BRILLIANT_SCARF.get());
                        pOutput.accept(ModItems.DELICATE_FROND.get());
                        pOutput.accept(ModItems.DIAMOND_DIADEM.get());
                        pOutput.accept(ModItems.FUR_COAT.get());
                        pOutput.accept(ModItems.GLITTER.get());
                        pOutput.accept(ModItems.JEWELRY_BOX.get());
                        pOutput.accept(ModItems.LOOMING_FRUIT.get());
                        pOutput.accept(ModItems.SIGNET_RING.get());
                        // Tezcatlipoca
                        pOutput.accept(ModItems.BIG_HUG.get());
                        pOutput.accept(ModItems.STORYBOOK.get());
                        pOutput.accept(ModItems.BAKING_MITTENS.get());
                        pOutput.accept(ModItems.GOLDEN_COMPASS.get());
                        pOutput.accept(ModItems.GOLDEN_SEAL.get());
                        pOutput.accept(ModItems.PUMPKIN_CANDLE.get());
                        pOutput.accept(ModItems.HOT_COCOA.get());
                        pOutput.accept(ModItems.TOY_BOX.get());
                        pOutput.accept(ModItems.NUTRITIOUS_SOUP.get());
                        pOutput.accept(ModItems.YUMMY_COOKIE_IRONCLAD.get());
                        pOutput.accept(ModItems.YUMMY_COOKIE_SILENT.get());
                        pOutput.accept(ModItems.YUMMY_COOKIE_DEFECT.get());
                        pOutput.accept(ModItems.YUMMY_COOKIE_NECRO.get());
                        pOutput.accept(ModItems.YUMMY_COOKIE_REGENT.get());
                        // Pell
                        pOutput.accept(ModItems.PELL_LEGION.get());
                        pOutput.accept(ModItems.PELL_GROWTH.get());
                        pOutput.accept(ModItems.PELL_HORN.get());
                        pOutput.accept(ModItems.PELL_TEARS.get());
                        pOutput.accept(ModItems.PELL_FLESH.get());
                        pOutput.accept(ModItems.PELL_BLOOD.get());
                        pOutput.accept(ModItems.PELL_TOOTH.get());
                        pOutput.accept(ModItems.PELL_EYE.get());
                        pOutput.accept(ModItems.PELL_WING.get());
                        pOutput.accept(ModItems.PELL_CLAW.get());
                        // Orobas
                        pOutput.accept(ModItems.GLASS_EYE.get());
                        pOutput.accept(ModItems.RADIANT_PEARL.get());
                        pOutput.accept(ModItems.ELECTRIC_SHRYMP.get());
                        pOutput.accept(ModItems.DRIFTWOOD.get());
                        pOutput.accept(ModItems.ARCHAIC_TOOTH.get());
                        pOutput.accept(ModItems.SEA_GLASS.get());
                        pOutput.accept(ModItems.PRISMATIC_GEM.get());
                        pOutput.accept(ModItems.ALCHEMICAL_COFFER.get());
                        pOutput.accept(ModItems.TOUCH_OF_OROBAS.get());
                        pOutput.accept(ModItems.SAND_CASTLE.get());
                        // Neow
                        pOutput.accept(ModItems.ARCANE_SCROLL.get());
                        pOutput.accept(ModItems.SILVER_CRUCIBLE.get());
                        pOutput.accept(ModItems.HEFTY_TABLET.get());
                        pOutput.accept(ModItems.POMANDER.get());
                        pOutput.accept(ModItems.BOOMING_CONCH.get());
                        pOutput.accept(ModItems.GOLDEN_PEARL.get());
                        pOutput.accept(ModItems.PRECISE_SCISSORS.get());
                        pOutput.accept(ModItems.MASSIVE_SCROLL.get());
                        pOutput.accept(ModItems.LARGE_CAPSULE.get());
                        pOutput.accept(ModItems.SCROLL_BOXES.get());
                        pOutput.accept(ModItems.NEOWS_BONES.get());
                        pOutput.accept(ModItems.NEOWS_TALISMAN.get());
                        pOutput.accept(ModItems.NEOWS_TORMENT.get());
                        pOutput.accept(ModItems.LEAD_PAPERWEIGHT.get());
                        pOutput.accept(ModItems.LAVA_ROCK.get());
                        pOutput.accept(ModItems.LOST_COFFER.get());
                        pOutput.accept(ModItems.STONE_HUMIDIFIER.get());
                        pOutput.accept(ModItems.LEAFY_POULTICE.get());
                        pOutput.accept(ModItems.PRECARIOUS_SHEARS.get());
                        pOutput.accept(ModItems.SMALL_CAPSULE.get());
                        pOutput.accept(ModItems.NEW_LEAF.get());
                        pOutput.accept(ModItems.PHIAL_HOLSTER.get());
                        pOutput.accept(ModItems.NUTRITIOUS_OYSTER.get());
                        pOutput.accept(ModItems.WINGED_BOOTS.get());
                        pOutput.accept(ModItems.CURSED_PEARL.get());
                    }).build());

    // ===== Tab 4: 塔之诅咒 (icon: rotating curses) =====

    private static final RegistryObject<Item>[] CURSE_ICONS = new RegistryObject[]{
            ModItems.ASCENDERS_BANE, ModItems.BAD_LUCK, ModItems.CLUMSY, ModItems.CURSE_OF_THE_BELL,
            ModItems.DEBT, ModItems.DECAY, ModItems.DOUBT, ModItems.ENTHRALLED, ModItems.FOLLY,
            ModItems.GREED, ModItems.GUILTY, ModItems.INJURY, ModItems.NORMALITY, ModItems.POOR_SLEEP,
            ModItems.REGRET, ModItems.SHAME, ModItems.SPORE_MIND, ModItems.WRITHE
    };

    private static ItemStack getCurseTabIcon() {
        int index = (int) (System.currentTimeMillis() / 1500) % CURSE_ICONS.length;
        return new ItemStack(CURSE_ICONS[index].get());
    }

    public final static RegistryObject<CreativeModeTab> SAYUKI_CURSE_TAB =
            CREATIVE_MODE_TABS.register("sayuki_curse_tab", () -> CreativeModeTab.builder()
                    .icon(() -> getCurseTabIcon())
                    .title(Component.translatable("itemGroup.sayuki_curse_tab"))
                    .displayItems((pParameters, pOutput) -> {
                    
                        pOutput.accept(ModItems.ASCENDERS_BANE.get());
                        pOutput.accept(ModItems.BAD_LUCK.get());
                        pOutput.accept(ModItems.CLUMSY.get());
                        pOutput.accept(ModItems.CURSE_OF_THE_BELL.get());
                        pOutput.accept(ModItems.DEBT.get());
                        pOutput.accept(ModItems.DECAY.get());
                        pOutput.accept(ModItems.DOUBT.get());
                        pOutput.accept(ModItems.ENTHRALLED.get());
                        pOutput.accept(ModItems.FOLLY.get());
                        pOutput.accept(ModItems.GREED.get());
                        pOutput.accept(ModItems.GUILTY.get());
                        pOutput.accept(ModItems.INJURY.get());
                        pOutput.accept(ModItems.NORMALITY.get());
                        pOutput.accept(ModItems.POOR_SLEEP.get());
                        pOutput.accept(ModItems.REGRET.get());
                        pOutput.accept(ModItems.SHAME.get());
                        pOutput.accept(ModItems.SPORE_MIND.get());
                        pOutput.accept(ModItems.WRITHE.get());
                        pOutput.accept(ModItems.GU_MU.get());
                        // Desolate Plague (荒疫)
                        pOutput.accept(ModItems.ACCURSED.get());
                        pOutput.accept(ModItems.ANCIENT.get());
                        pOutput.accept(ModItems.DURIAN.get());
                        pOutput.accept(ModItems.HAUNTINGS.get());
                        pOutput.accept(ModItems.MAZE.get());
                        pOutput.accept(ModItems.MIMIC.get());
                        pOutput.accept(ModItems.MUZZLE.get());
                        pOutput.accept(ModItems.SCATTER.get());
                        pOutput.accept(ModItems.SHIELD.get());
                        pOutput.accept(ModItems.SPEAR.get());
                        pOutput.accept(ModItems.TROPHY.get());
                        pOutput.accept(ModItems.TWIST.get());
                        pOutput.accept(ModItems.VOID.get());
                    }).build());

    // ===== Tab 5: 咲雪随想：通用遗物 =====

    public final static RegistryObject<CreativeModeTab> SAYUKI_RELIC_TAB =
            CREATIVE_MODE_TABS.register("sayuki_relic_tab", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.AKABEKO.get()))
                    .title(Component.translatable("itemGroup.sayuki_relic_tab"))
                    .displayItems((pParameters, pOutput) -> {
pOutput.accept(ModItems.FAKEANCHOR.get());
                    pOutput.accept(ModItems.FAKEBLOODVIAL.get());
                    pOutput.accept(ModItems.FAKEHAPPYFLOWER.get());
                    pOutput.accept(ModItems.FAKELEESWAFFLE.get());
                    pOutput.accept(ModItems.FAKEMANGO.get());
                    pOutput.accept(ModItems.FAKEMERCHANTSRUG.get());
                    pOutput.accept(ModItems.FAKEORICHALCUM.get());
                    pOutput.accept(ModItems.FAKESNECKOEYE.get());
                    pOutput.accept(ModItems.FAKESTRIKEDUMMY.get());
                    pOutput.accept(ModItems.FAKEVENERABLETEASET.get());
                    pOutput.accept(ModItems.AKABEKO.get());
                    pOutput.accept(ModItems.AMETHYSTAUBERGINE.get());
                    pOutput.accept(ModItems.ANCHOR.get());
                    pOutput.accept(ModItems.ARTOFWAR.get());
                    pOutput.accept(ModItems.BAGOFMARBLES.get());
                    pOutput.accept(ModItems.BAGOFPREPARATION.get());
                    pOutput.accept(ModItems.BEATINGREMNANT.get());
                    pOutput.accept(ModItems.BELLOWS.get());
                    pOutput.accept(ModItems.BELTBUCKLE.get());
                    pOutput.accept(ModItems.BIGMUSHROOM.get());
                    pOutput.accept(ModItems.BINGBONG.get());
                    pOutput.accept(ModItems.BLOODVIAL.get());
                    pOutput.accept(ModItems.BONETEA.get());
                    pOutput.accept(ModItems.BOOKOFFIVERINGS.get());
                    pOutput.accept(ModItems.BOWLERHAT.get());
                    pOutput.accept(ModItems.BREAD.get());
                    pOutput.accept(ModItems.BRONZESCALES.get());
                    pOutput.accept(ModItems.BURNINGSTICKS.get());
                    pOutput.accept(ModItems.BYRDPIP.get());
                    pOutput.accept(ModItems.CANDELABRA.get());
                    pOutput.accept(ModItems.CAPTAINSWHEEL.get());
                    pOutput.accept(ModItems.CAULDRON.get());
                    pOutput.accept(ModItems.CENTENNIALPUZZLE.get());
                    pOutput.accept(ModItems.CHANDELIER.get());
                    pOutput.accept(ModItems.CHEMICALX.get());
                    pOutput.accept(ModItems.CHOSENCHEESE.get());
                    pOutput.accept(ModItems.CLOAKCLASP.get());
                    pOutput.accept(ModItems.DARKSTONEPERIAPT.get());
                    pOutput.accept(ModItems.DAUGHTEROFTHEWIND.get());
                    pOutput.accept(ModItems.DINGYRUG.get());
                    pOutput.accept(ModItems.DOLLYSMIRROR.get());
                    pOutput.accept(ModItems.DRAGONFRUIT.get());
                    pOutput.accept(ModItems.DREAMCATCHER.get());
                    pOutput.accept(ModItems.EMBERTEA.get());
                    pOutput.accept(ModItems.ETERNALFEATHER.get());
                    pOutput.accept(ModItems.FESTIVEPOPPER.get());
                    pOutput.accept(ModItems.FORGOTTENSOUL.get());
                    pOutput.accept(ModItems.FRAGRANTMUSHROOM.get());
                    pOutput.accept(ModItems.FRESNELLENS.get());
                    pOutput.accept(ModItems.FROZENEGG.get());
                    pOutput.accept(ModItems.GAMBLINGCHIP.get());
                    pOutput.accept(ModItems.GAMEPIECE.get());
                    pOutput.accept(ModItems.GHOSTSEED.get());
                    pOutput.accept(ModItems.GIRYA.get());
                    pOutput.accept(ModItems.GNARLEDHAMMER.get());
                    pOutput.accept(ModItems.GORGET.get());
                    pOutput.accept(ModItems.GREMLINHORN.get());
                    pOutput.accept(ModItems.HANDDRILL.get());
                    pOutput.accept(ModItems.HAPPYFLOWER.get());
                    pOutput.accept(ModItems.HISTORYCOURSE.get());
                    pOutput.accept(ModItems.HORNCLEAT.get());
                    pOutput.accept(ModItems.ICECREAM.get());
                    pOutput.accept(ModItems.INTIMIDATINGHELMET.get());
                    pOutput.accept(ModItems.JOSSPAPER.get());
                    pOutput.accept(ModItems.JUZUBRACELET.get());
                    pOutput.accept(ModItems.KIFUDA.get());
                    pOutput.accept(ModItems.KUNAI.get());
                    pOutput.accept(ModItems.KUSARIGAMA.get());
                    pOutput.accept(ModItems.LANTERN.get());
                    pOutput.accept(ModItems.LASTINGCANDY.get());
                    pOutput.accept(ModItems.LAVALAMP.get());
                    pOutput.accept(ModItems.LEESWAFFLE.get());
                    pOutput.accept(ModItems.LETTEROPENER.get());
                    pOutput.accept(ModItems.LIZARDTAIL.get());
                    pOutput.accept(ModItems.LOSTWISP.get());
                    pOutput.accept(ModItems.LUCKYFYSH.get());
                    pOutput.accept(ModItems.MANGO.get());
                    pOutput.accept(ModItems.MAWBANK.get());
                    pOutput.accept(ModItems.MEALTICKET.get());
                    pOutput.accept(ModItems.MEATONTHEBONE.get());
                    pOutput.accept(ModItems.MEMBERSHIPCARD.get());
                    pOutput.accept(ModItems.MERCURYHOURGLASS.get());
                    pOutput.accept(ModItems.MINIATURECANNON.get());
                    pOutput.accept(ModItems.MINIATURETENT.get());
                    pOutput.accept(ModItems.MOLTENEGG.get());
                    pOutput.accept(ModItems.MRSTRUGGLES.get());
                    pOutput.accept(ModItems.MUMMIFIEDHAND.get());
                    pOutput.accept(ModItems.MYSTICLIGHTER.get());
                    pOutput.accept(ModItems.NUNCHAKU.get());
                    pOutput.accept(ModItems.ODDLYSMOOTHSTONE.get());
                    pOutput.accept(ModItems.OLDCOIN.get());
                    pOutput.accept(ModItems.ORICHALCUM.get());
                    pOutput.accept(ModItems.ORNAMENTALFAN.get());
                    pOutput.accept(ModItems.ORRERY.get());
                    pOutput.accept(ModItems.PANTOGRAPH.get());
                    pOutput.accept(ModItems.PARRYINGSHIELD.get());
                    pOutput.accept(ModItems.PEAR.get());
                    pOutput.accept(ModItems.PENNIB.get());
                    pOutput.accept(ModItems.PENDULUM.get());
                    pOutput.accept(ModItems.PERMAFROST.get());
                    pOutput.accept(ModItems.PETRIFIEDTOAD.get());
                    pOutput.accept(ModItems.PLANISPHERE.get());
                    pOutput.accept(ModItems.POCKETWATCH.get());
                    pOutput.accept(ModItems.POLLINOUSCORE.get());
                    pOutput.accept(ModItems.POTIONBELT.get());
                    pOutput.accept(ModItems.PRAYERWHEEL.get());
                    pOutput.accept(ModItems.PUNCHDAGGER.get());
                    pOutput.accept(ModItems.RAINBOWRING.get());
                    pOutput.accept(ModItems.RAZORTOOTH.get());
                    pOutput.accept(ModItems.REDMASK.get());
                    pOutput.accept(ModItems.REGALPILLOW.get());
                    pOutput.accept(ModItems.REPTILETRINKET.get());
                    pOutput.accept(ModItems.RINGINGTRIANGLE.get());
                    pOutput.accept(ModItems.RIPPLEBASIN.get());
                    pOutput.accept(ModItems.ROYALPOISON.get());
                    pOutput.accept(ModItems.ROYALSTAMP.get());
                    pOutput.accept(ModItems.SCREAMINGFLAGON.get());
                    pOutput.accept(ModItems.SHOVEL.get());
                    pOutput.accept(ModItems.SHURIKEN.get());
                    pOutput.accept(ModItems.SLINGOFCOURAGE.get());
                    pOutput.accept(ModItems.SPARKLINGROUGE.get());
                    pOutput.accept(ModItems.STONECALENDAR.get());
                    pOutput.accept(ModItems.STONECRACKER.get());
                    pOutput.accept(ModItems.STRAWBERRY.get());
                    pOutput.accept(ModItems.STRIKEDUMMY.get());
                    pOutput.accept(ModItems.STURDYCLAMP.get());
                    pOutput.accept(ModItems.SWORDOFJADE.get());
                    pOutput.accept(ModItems.SWORDOFSTONE.get());
                    pOutput.accept(ModItems.TEAOFDISCOURTESY.get());
                    pOutput.accept(ModItems.THEABACUS.get());
                    pOutput.accept(ModItems.THEBOOT.get());
                    pOutput.accept(ModItems.THECOURIER.get());
                    pOutput.accept(ModItems.TINYMAILBOX.get());
                    pOutput.accept(ModItems.TOOLBOX.get());
                    pOutput.accept(ModItems.TOXICEGG.get());
                    pOutput.accept(ModItems.TUNGSTENROD.get());
                    pOutput.accept(ModItems.TUNINGFORK.get());
                    pOutput.accept(ModItems.UNCEASINGTOP.get());
                    pOutput.accept(ModItems.UNSETTLINGLAMP.get());
                    pOutput.accept(ModItems.VAJRA.get());
                    pOutput.accept(ModItems.VAMBRACE.get());
                    pOutput.accept(ModItems.VENERABLETEASET.get());
                    pOutput.accept(ModItems.VEXINGPUZZLEBOX.get());
                    pOutput.accept(ModItems.WARPAINT.get());
                    pOutput.accept(ModItems.WHETSTONE.get());
                    pOutput.accept(ModItems.WHITEBEASTSTATUE.get());
                    pOutput.accept(ModItems.WHITESTAR.get());
                    pOutput.accept(ModItems.WINGCHARM.get());
                    pOutput.accept(ModItems.WONGOCUSTOMERAPPRECIATIONBADGE.get());
                    pOutput.accept(ModItems.WONGOSMYSTERYTICKET.get());
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
