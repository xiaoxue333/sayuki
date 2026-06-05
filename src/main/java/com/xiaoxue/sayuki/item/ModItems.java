/**
 * Sayuki — Item registration (DeferredRegister, MODID: sayuki)
 * Compat: Goety-2 and IronsSpellbooks — all use ForgeRegistries with separate namespace, no item name clash
 */
package com.xiaoxue.sayuki.item;

import com.xiaoxue.sayuki.Sayuki;
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

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
