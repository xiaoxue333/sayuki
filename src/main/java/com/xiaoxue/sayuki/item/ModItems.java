/**
 * Sayuki — Item registration (DeferredRegister, MODID: sayuki)
 * Compat: Goety-2 and IronsSpellbooks — all use ForgeRegistries with separate namespace, no item name clash
 */
package com.xiaoxue.sayuki.item;

import com.xiaoxue.sayuki.Sayuki;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Sayuki.MOD_ID);


    public static final RegistryObject<Item> STELLAR_JADE =
            ITEMS.register("stellar_jade", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAW_STELLAR_JADE =
            ITEMS.register("raw_stellar_jade", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> VOODOO_RING =
            ITEMS.register("voodoo_ring", () -> new VoodooRing(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> FRUSTA_DOMINATE =
            ITEMS.register("frusta_dominate", () -> new FrustaDominate(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> AZURE_SWORD =
            ITEMS.register("azure_sword", () -> new AzureSword(new Item.Properties().stacksTo(1).durability(65)));

    // PocketPistol removed — item retired

    public static final RegistryObject<Item> HEART_GRENADE =
            ITEMS.register("heart_grenade", () -> new HeartGrenade(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> CHERRY_EAR_ORNAMENTS =
            ITEMS.register("cherry_ear_ornaments", () -> new CherryEarOrnaments(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> HEART_EAR_ORNAMENTS =
            ITEMS.register("heart_ear_ornaments", () -> new HeartEarOrnaments(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> MAGENTA_SPEAR =
            ITEMS.register("magenta_spear", () -> new MagentaSpearItem(new Item.Properties().stacksTo(1).durability(350)));

    public static final RegistryObject<Item> STELLAR_JADE_GEM =
            ITEMS.register("stellar_jade_gem", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> WHISPERING_EARRING =
            ITEMS.register("whispering_earring", () -> new WhisperingEarring(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BOUND_PHYLACTERY =
            ITEMS.register("bound_phylactery", () -> new BoundPhylactery(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> PHYLACTERY_UNBOUND =
            ITEMS.register("phylactery_unbound", () -> new PhylacteryUnbound(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> HEAVEN_EAR_ORNAMENTS =
            ITEMS.register("heaven_ear_ornaments", () -> new HeavenEarOrnaments(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BURNING_BLOOD =
            ITEMS.register("burning_blood", () -> new BurningBlood(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BLACK_BLOOD =
            ITEMS.register("black_blood", () -> new BlackBlood(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> DIVINE_RIGHT =
            ITEMS.register("divine_right", () -> new DivineRight(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> DIVINE_DESTINY =
            ITEMS.register("divine_destiny", () -> new DivineDestiny(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> RING_OF_THE_SNAKE =
            ITEMS.register("ring_of_the_snake", () -> new RingOfTheSnake(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> RING_OF_THE_DRAKE =
            ITEMS.register("ring_of_the_drake", () -> new RingOfTheDrake(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> CRACKED_CORE =
            ITEMS.register("cracked_core", () -> new CrackedCore(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> INFUSED_CORE =
            ITEMS.register("infused_core", () -> new InfusedCore(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> GOLD_PLATED_CABLES =
            ITEMS.register("gold_plated_cables", () -> new GoldPlatedCables(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> DATA_DISK =
            ITEMS.register("data_disk", () -> new DataDisk(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SYMBIOTIC_VIRUS =
            ITEMS.register("symbiotic_virus", () -> new SymbioticVirus(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> EMOTION_CHIP =
            ITEMS.register("emotion_chip", () -> new EmotionChip(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> METRONOME =
            ITEMS.register("metronome", () -> new Metronome(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> RUNIC_CAPACITOR =
            ITEMS.register("runic_capacitor", () -> new RunicCapacitor(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> POWER_CELL =
            ITEMS.register("power_cell", () -> new PowerCell(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BONE_FLUTE =
            ITEMS.register("bone_flute", () -> new BoneFlute(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BOOK_REPAIR_KNIFE =
            ITEMS.register("book_repair_knife", () -> new BookRepairKnife(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> FUNERARY_MASK =
            ITEMS.register("funerary_mask", () -> new FuneraryMask(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BIG_HAT =
            ITEMS.register("big_hat", () -> new BigHat(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BOOKMARK =
            ITEMS.register("bookmark", () -> new Bookmark(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> IVORY_TILE =
            ITEMS.register("ivory_tile", () -> new IvoryTile(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> UNDYING_SIGIL =
            ITEMS.register("undying_sigil", () -> new UndyingSigil(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> FENCING_MANUAL =
            ITEMS.register("fencing_manual", () -> new FencingManual(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> REGALITE =
            ITEMS.register("regalite", () -> new Regalite(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> LUNAR_PASTRY =
            ITEMS.register("lunar_pastry", () -> new LunarPastry(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ORANGE_DOUGH =
            ITEMS.register("orange_dough", () -> new OrangeDough(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> VITRUVIAN_MINION =
            ITEMS.register("vitruvian_minion", () -> new VitruvianMinion(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> MINI_REGENT =
            ITEMS.register("mini_regent", () -> new MiniRegent(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> FORGED_SWORD =
            ITEMS.register("forged_sword", () -> new ForgedSword(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> GALACTIC_DUST =
            ITEMS.register("galactic_dust", () -> new GalacticDust(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> TWISTED_FUNNEL =
            ITEMS.register("twisted_funnel", () -> new TwistedFunnel(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SNECKO_SKULL =
            ITEMS.register("snecko_skull", () -> new SneckoSkull(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> NINJA_SCROLL =
            ITEMS.register("ninja_scroll", () -> new NinjaScroll(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> TINGSHA =
            ITEMS.register("tingsha", () -> new Tingsha(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> TOUGH_BANDAGES =
            ITEMS.register("tough_bandages", () -> new ToughBandages(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> HELICAL_DART =
            ITEMS.register("helical_dart", () -> new HelicalDart(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> PAPER_KRANE =
            ITEMS.register("paper_krane", () -> new PaperKrane(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> RED_SKULL =
            ITEMS.register("red_skull", () -> new RedSkull(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> PAPER_PHROG =
            ITEMS.register("paper_phrog", () -> new PaperPhrog(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SELF_FORMING_CLAY =
            ITEMS.register("self_forming_clay", () -> new SelfFormingClay(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> CHARONS_ASHES =
            ITEMS.register("charons_ashes", () -> new CharonsAshes(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> DEMON_TONGUE =
            ITEMS.register("demon_tongue", () -> new DemonTongue(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> RUINED_HELMET =
            ITEMS.register("ruined_helmet", () -> new RuinedHelmet(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BRIMSTONE =
            ITEMS.register("brimstone", () -> new Brimstone(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BLOOD_SOAKED_ROSE =
            ITEMS.register("blood_soaked_rose", () -> new BloodSoakedRose(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CHOICES_PARADOX =
            ITEMS.register("choices_paradox", () -> new ChoicesParadox(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DISTINGUISHED_CAPE =
            ITEMS.register("distinguished_cape", () -> new DistinguishedCape(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> FIDDLE =
            ITEMS.register("fiddle", () -> new Fiddle(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> JEWELED_MASK =
            ITEMS.register("jeweled_mask", () -> new JeweledMask(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> LORDS_PARASOL =
            ITEMS.register("lords_parasol", () -> new LordsParasol(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MUSIC_BOX =
            ITEMS.register("music_box", () -> new MusicBox(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PRESERVED_FOG =
            ITEMS.register("preserved_fog", () -> new PreservedFog(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SERE_TALON =
            ITEMS.register("sere_talon", () -> new SereTalon(new Item.Properties().stacksTo(1)));

    // ===== DARV (The Hoarder) relics =====
    public static final RegistryObject<Item> ASTROLABE =
            ITEMS.register("astrolabe", () -> new Astrolabe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLACK_STAR =
            ITEMS.register("black_star", () -> new BlackStar(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CALLING_BELL =
            ITEMS.register("calling_bell", () -> new CallingBell(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DUSTY_TOME =
            ITEMS.register("dusty_tome", () -> new DustyTome(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ECTOPLASM =
            ITEMS.register("ectoplasm", () -> new Ectoplasm(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> EMPTY_CAGE =
            ITEMS.register("empty_cage", () -> new EmptyCage(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PANDORAS_BOX =
            ITEMS.register("pandoras_box", () -> new PandorasBox(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PHILOSOPHERS_STONE =
            ITEMS.register("philosophers_stone", () -> new PhilosophersStone(new Item.Properties().durability(666)));
    public static final RegistryObject<Item> RUNIC_PYRAMID =
            ITEMS.register("runic_pyramid", () -> new RunicPyramid(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SNECKO_EYE =
            ITEMS.register("snecko_eye", () -> new SneckoEye(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SOZU =
            ITEMS.register("sozu", () -> new Sozu(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> VELVET_CHOKER =
            ITEMS.register("velvet_choker", () -> new VelvetChoker(new Item.Properties().stacksTo(1)));

    // ===== New relics (batch import) =====
    public static final RegistryObject<Item> CLAWS =
            ITEMS.register("claws", () -> new Claws(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CROSSBOW =
            ITEMS.register("crossbow", () -> new CrossbowRelic(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> IRON_CLUB =
            ITEMS.register("iron_club", () -> new IronClub(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MEAT_CLEAVER =
            ITEMS.register("meat_cleaver", () -> new MeatCleaver(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SAI =
            ITEMS.register("sai", () -> new Sai(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SPIKED_GAUNTLETS =
            ITEMS.register("spiked_gauntlets", () -> new SpikedGauntlets(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> TANXS_WHISTLE =
            ITEMS.register("tanxs_whistle", () -> new TanxsWhistle(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> THROWING_AXE =
            ITEMS.register("throwing_axe", () -> new ThrowingAxe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> TRI_BOOMERANG =
            ITEMS.register("tri_boomerang", () -> new TriBoomerang(new Item.Properties()));
    public static final RegistryObject<Item> WAR_HAMMER =
            ITEMS.register("war_hammer", () -> new WarHammer(new Item.Properties().stacksTo(1)));

    // ----- 遗物 — Beauty (尖塔最美丽的女人) -----

    public static final RegistryObject<Item> BEAUTIFUL_BRACELET =
            ITEMS.register("beautiful_bracelet", () -> new BeautifulBracelet(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLESSED_ANTLER =
            ITEMS.register("blessed_antler", () -> new BlessedAntler(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BRILLIANT_SCARF =
            ITEMS.register("brilliant_scarf", () -> new BrilliantScarf(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DELICATE_FROND =
            ITEMS.register("delicate_frond", () -> new DelicateFrond(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DIAMOND_DIADEM =
            ITEMS.register("diamond_diadem", () -> new DiamondDiadem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> FUR_COAT =
            ITEMS.register("fur_coat", () -> new FurCoat(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> GLITTER =
            ITEMS.register("glitter", () -> new Glitter(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> JEWELRY_BOX =
            ITEMS.register("jewelry_box", () -> new JewelryBox(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> LOOMING_FRUIT =
            ITEMS.register("looming_fruit", () -> new LoomingFruit(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SIGNET_RING =
            ITEMS.register("signet_ring", () -> new SignetRing(new Item.Properties().stacksTo(1)));

    // ===== Tezcatlipoca relics =====
    public static final RegistryObject<Item> BIG_HUG =
            ITEMS.register("big_hug", () -> new BigHug(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> STORYBOOK =
            ITEMS.register("storybook", () -> new Storybook(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BAKING_MITTENS =
            ITEMS.register("baking_mittens", () -> new BakingMittens(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> GOLDEN_COMPASS =
            ITEMS.register("golden_compass", () -> new GoldenCompass(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> GOLDEN_SEAL =
            ITEMS.register("golden_seal", () -> new GoldenSeal(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PUMPKIN_CANDLE =
            ITEMS.register("pumpkin_candle", () -> new PumpkinCandle(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HOT_COCOA =
            ITEMS.register("hot_cocoa", () -> new HotCocoa(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> TOY_BOX =
            ITEMS.register("toy_box", () -> new ToyBox(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> NUTRITIOUS_SOUP =
            ITEMS.register("nutritious_soup", () -> new NutritiousSoup(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> YUMMY_COOKIE_IRONCLAD =
            ITEMS.register("yummy_cookie_ironclad", () -> new YummyCookieIronclad(new Item.Properties()
                    .food(new FoodProperties.Builder().nutrition(10).saturationMod(1.0F).alwaysEat().build())));
    public static final RegistryObject<Item> YUMMY_COOKIE_SILENT =
            ITEMS.register("yummy_cookie_silent", () -> new YummyCookieSilent(new Item.Properties()
                    .food(new FoodProperties.Builder().nutrition(10).saturationMod(1.0F).alwaysEat().build())));
    public static final RegistryObject<Item> YUMMY_COOKIE_DEFECT =
            ITEMS.register("yummy_cookie_defect", () -> new YummyCookieDefect(new Item.Properties()
                    .food(new FoodProperties.Builder().nutrition(10).saturationMod(1.0F).alwaysEat().build())));
    public static final RegistryObject<Item> YUMMY_COOKIE_NECRO =
            ITEMS.register("yummy_cookie_necro", () -> new YummyCookieNecro(new Item.Properties()
                    .food(new FoodProperties.Builder().nutrition(10).saturationMod(1.0F).alwaysEat().build())));
    public static final RegistryObject<Item> YUMMY_COOKIE_REGENT =
            ITEMS.register("yummy_cookie_regent", () -> new YummyCookieRegent(new Item.Properties()
                    .food(new FoodProperties.Builder().nutrition(10).saturationMod(1.0F).alwaysEat().build())));

    // ===== Pell relics =====
    public static final RegistryObject<Item> PELL_LEGION =
            ITEMS.register("pell_legion", () -> new PellLegion(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PELL_GROWTH =
            ITEMS.register("pell_growth", () -> new PellGrowth(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PELL_HORN =
            ITEMS.register("pell_horn", () -> new PellHorn(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PELL_TEARS =
            ITEMS.register("pell_tears", () -> new PellTears(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PELL_FLESH =
            ITEMS.register("pell_flesh", () -> new PellFlesh(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PELL_BLOOD =
            ITEMS.register("pell_blood", () -> new PellBlood(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PELL_TOOTH =
            ITEMS.register("pell_tooth", () -> new PellTooth(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PELL_EYE =
            ITEMS.register("pell_eye", () -> new PellEye(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PELL_WING =
            ITEMS.register("pell_wing", () -> new PellWing(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PELL_CLAW =
            ITEMS.register("pell_claw", () -> new PellClaw(new Item.Properties().stacksTo(1)));

    // ===== Orobas relics =====
    public static final RegistryObject<Item> GLASS_EYE =
            ITEMS.register("glass_eye", () -> new GlassEye(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> RADIANT_PEARL =
            ITEMS.register("radiant_pearl", () -> new RadiantPearl(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ELECTRIC_SHRYMP =
            ITEMS.register("electric_shrymp", () -> new ElectricShrymp(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DRIFTWOOD =
            ITEMS.register("driftwood", () -> new Driftwood(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ARCHAIC_TOOTH =
            ITEMS.register("archaic_tooth", () -> new ArchaicTooth(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SEA_GLASS =
            ITEMS.register("sea_glass", () -> new SeaGlass(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PRISMATIC_GEM =
            ITEMS.register("prismatic_gem", () -> new PrismaticGem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ALCHEMICAL_COFFER =
            ITEMS.register("alchemical_coffer", () -> new AlchemicalCoffer(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> TOUCH_OF_OROBAS =
            ITEMS.register("touch_of_orobas", () -> new TouchOfOrobas(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SAND_CASTLE =
            ITEMS.register("sand_castle", () -> new SandCastle(new Item.Properties().stacksTo(1)));

    // ===== Neow relics =====
    public static final RegistryObject<Item> ARCANE_SCROLL =
            ITEMS.register("arcane_scroll", () -> new ArcaneScroll(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SILVER_CRUCIBLE =
            ITEMS.register("silver_crucible", () -> new SilverCrucible(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HEFTY_TABLET =
            ITEMS.register("hefty_tablet", () -> new HeftyTablet(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> POMANDER =
            ITEMS.register("pomander", () -> new Pomander(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BOOMING_CONCH =
            ITEMS.register("booming_conch", () -> new BoomingConch(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> GOLDEN_PEARL =
            ITEMS.register("golden_pearl", () -> new GoldenPearl(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PRECISE_SCISSORS =
            ITEMS.register("precise_scissors", () -> new PreciseScissors(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MASSIVE_SCROLL =
            ITEMS.register("massive_scroll", () -> new MassiveScroll(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> LARGE_CAPSULE =
            ITEMS.register("large_capsule", () -> new LargeCapsule(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SCROLL_BOXES =
            ITEMS.register("scroll_boxes", () -> new ScrollBoxes(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> NEOWS_BONES =
            ITEMS.register("neows_bones", () -> new NeowsBones(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> NEOWS_TALISMAN =
            ITEMS.register("neows_talisman", () -> new NeowsTalisman(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> NEOWS_TORMENT =
            ITEMS.register("neows_torment", () -> new NeowsTorment(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> LEAD_PAPERWEIGHT =
            ITEMS.register("lead_paperweight", () -> new LeadPaperweight(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> LAVA_ROCK =
            ITEMS.register("lava_rock", () -> new LavaRock(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> LOST_COFFER =
            ITEMS.register("lost_coffer", () -> new LostCoffer(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> STONE_HUMIDIFIER =
            ITEMS.register("stone_humidifier", () -> new StoneHumidifier(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> LEAFY_POULTICE =
            ITEMS.register("leafy_poultice", () -> new LeafyPoultice(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PRECARIOUS_SHEARS =
            ITEMS.register("precarious_shears", () -> new PrecariousShears(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SMALL_CAPSULE =
            ITEMS.register("small_capsule", () -> new SmallCapsule(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> NEW_LEAF =
            ITEMS.register("new_leaf", () -> new NewLeaf(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PHIAL_HOLSTER =
            ITEMS.register("phial_holster", () -> new PhialHolster(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> NUTRITIOUS_OYSTER =
            ITEMS.register("nutritious_oyster", () -> new NutritiousOyster(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> WINGED_BOOTS =
            ITEMS.register("winged_boots", () -> new WingedBoots(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CURSED_PEARL =
            ITEMS.register("cursed_pearl", () -> new CursedPearl(new Item.Properties().stacksTo(1)));

    // ===== Curse of the Tower (18 curses) =====
    public static final RegistryObject<Item> ASCENDERS_BANE =
            ITEMS.register("ascenders_bane", () -> new AscendersBane(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BAD_LUCK =
            ITEMS.register("bad_luck", () -> new BadLuck(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CLUMSY =
            ITEMS.register("clumsy", () -> new Clumsy(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CURSE_OF_THE_BELL =
            ITEMS.register("curse_of_the_bell", () -> new CurseOfTheBell(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DEBT =
            ITEMS.register("debt", () -> new Debt(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DECAY =
            ITEMS.register("decay", () -> new Decay(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DOUBT =
            ITEMS.register("doubt", () -> new Doubt(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ENTHRALLED =
            ITEMS.register("enthralled", () -> new Enthralled(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> FOLLY =
            ITEMS.register("folly", () -> new Folly(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> GREED =
            ITEMS.register("greed", () -> new Greed(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> GUILTY =
            ITEMS.register("guilty", () -> new Guilty(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> INJURY =
            ITEMS.register("injury", () -> new Injury(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> NORMALITY =
            ITEMS.register("normality", () -> new Normality(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> POOR_SLEEP =
            ITEMS.register("poor_sleep", () -> new PoorSleep(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> REGRET =
            ITEMS.register("regret", () -> new Regret(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SHAME =
            ITEMS.register("shame", () -> new Shame(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SPORE_MIND =
            ITEMS.register("spore_mind", () -> new SporeMind(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> WRITHE =
            ITEMS.register("writhe", () -> new Writhe(new Item.Properties().stacksTo(1)));

    // ===== Desolate Plague (荒疫 — blight items) =====
    public static final RegistryObject<Item> ACCURSED =
            ITEMS.register("accursed", () -> new Accursed(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ANCIENT =
            ITEMS.register("ancient", () -> new Ancient(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DURIAN =
            ITEMS.register("durian", () -> new Durian(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HAUNTINGS =
            ITEMS.register("hauntings", () -> new Hauntings(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MAZE =
            ITEMS.register("maze", () -> new Maze(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MIMIC =
            ITEMS.register("mimic", () -> new Mimic(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MUZZLE =
            ITEMS.register("muzzle", () -> new Muzzle(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SCATTER =
            ITEMS.register("scatter", () -> new Scatter(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SHIELD =
            ITEMS.register("shield", () -> new Shield(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SPEAR =
            ITEMS.register("spear", () -> new Spear(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> TROPHY =
            ITEMS.register("trophy", () -> new Trophy(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> TWIST =
            ITEMS.register("twist", () -> new Twist(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> VOID =
            ITEMS.register("void", () -> new Void(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> GU_MU =
            ITEMS.register("gu_mu", () -> new GuMu(new Item.Properties()));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
