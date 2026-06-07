/**
 * Sayuki — Config, writes to config/sayuki-common.toml
 * Compat: Goety-2(cfg:goety/) and IronsSpellbooks(cfg:irons_spellbooks-*.toml) — independent config files, no clash
 */
package com.xiaoxue.sayuki;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Sayuki.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // ===== Hardcoded relic constants (not configurable) =====
    public static final double SELF_FORMING_CLAY_BLOCK_PER_HIT = 3.0;
    public static final double CHARONS_ASHES_DAMAGE_PER_STACK = 3.0;
    public static final int BRIMSTONE_ATTACK_PER_STACK = 2;
    public static final int BRIMSTONE_TARGET_ATTACK_BONUS = 1;
    public static final int BRIMSTONE_TARGET_MAX_ATTACKS = 10;
    public static final double BOOK_REPAIR_KNIFE_HEAL_PER_CHARGE = 3.0;
    public static final int MINI_REGENT_ATTACK_PER_CAST = 1;
    public static final int ORANGE_DOUGH_SLOT_BONUS = 2;
    public static final int RUNIC_CAPACITOR_SLOT_BONUS = 3;
    public static final int POWER_CELL_SLOT_BONUS = 2;
    public static final double POWER_CELL_MANA_PERCENT = 0.2;
    public static final int METRONOME_LIGHTNING_THRESHOLD = 7;
    public static final double METRONOME_DAMAGE = 30.0;
    public static final double BONE_FLUTE_HEALTH_PER_HIT = 2.0;
    public static final double BONE_FLUTE_BLOCK_PER_HIT = 2.0;
    public static final double DATA_DISK_SPELL_POWER_BONUS = 0.1;

    // ===== Config spec definitions =====

    private static final ForgeConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER
            .comment("Whether to log the dirt block on common setup")
            .define("logDirtBlock", true);

    private static final ForgeConfigSpec.IntValue MAGIC_NUMBER = BUILDER
            .comment("A magic number")
            .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
            .comment("What you want the introduction message to be for the magic number")
            .define("magicNumberIntroduction", "The magic number is... ");

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), Config::validateItemName);

    // ===== Weapon configs =====
    private static final ForgeConfigSpec.IntValue MAGENTA_SPEAR_COOLDOWN_TICKS = BUILDER
            .comment("Cooldown in ticks for Magenta Spear thrust ability, Default: 70")
            .defineInRange("magentaSpearCooldownTicks", 70, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue FRUSTA_DAMAGE = BUILDER
            .comment("Damage dealt by Frusta Dominate sonic boom, Default: 18.0")
            .defineInRange("frustaDamage", 18.0D, 0.0D, Double.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue MAGENTA_SPEAR_DAMAGE = BUILDER
            .comment("Damage dealt by Magenta Spear thrust, Default: 8.0")
            .defineInRange("magentaSpearDamage", 8.0D, 0.0D, Double.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue AZURE_WATER_BEAM_DAMAGE = BUILDER
            .comment("Damage dealt by Azure Sword water beam, Default: 7.0")
            .defineInRange("azureWaterBeamDamage", 7.0D, 0.0D, Double.MAX_VALUE);

    // ===== VAKUU: Whispering Earring =====
    private static final ForgeConfigSpec.IntValue WHISPERING_IDLE_SECONDS = BUILDER
            .comment("Whispering Earring: max idle seconds without attacking before penalty triggers, Default: 7")
            .defineInRange("whisperingIdleSeconds", 7, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue WHISPERING_DAMAGE_THRESHOLD = BUILDER
            .comment("Whispering Earring: minimum total damage required within the idle window to avoid penalty, Default: 100.0")
            .defineInRange("whisperingDamageThreshold", 100.0D, 0.0D, Double.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue WHISPERING_SELF_DAMAGE_RATIO = BUILDER
            .comment("Whispering Earring: ratio of attack damage reflected back to self when penalty triggers, Default: 0.5")
            .defineInRange("whisperingSelfDamageRatio", 0.5D, 0.0D, 1.0D);

    // ===== Necrobinder: Phylactery =====
    private static final ForgeConfigSpec.IntValue BOUND_PHYLACTERY_HEALTH_GAIN_INTERVAL = BUILDER
            .comment("Bound Phylactery: interval in seconds between each absorption gain (+1), Default: 10")
            .defineInRange("boundPhylacteryHealthGainIntervalSeconds", 10, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue UNBOUND_PHYLACTERY_HEALTH_GAIN_INTERVAL = BUILDER
            .comment("Unbound Phylactery: interval in seconds between each absorption gain (+2), Default: 7")
            .defineInRange("unboundPhylacteryHealthGainIntervalSeconds", 7, 1, Integer.MAX_VALUE);

    // ===== Regent: Divine =====
    private static final ForgeConfigSpec.IntValue DIVINE_RIGHT_MANA_FLOOR = BUILDER
            .comment("Divine Right: flat max mana bonus, Default: 30")
            .defineInRange("divineRightManaFloor", 30, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue DIVINE_DESTINY_MANA_RATIO = BUILDER
            .comment("Divine Destiny: max mana bonus as ratio of current max mana (0.3 = 30%), Default: 0.3")
            .defineInRange("divineDestinyManaRatio", 0.3D, 0.0D, 1.0D);

    // ===== Defect: Cores =====
    private static final ForgeConfigSpec.IntValue CRACKED_CORE_COOLDOWN_SECONDS = BUILDER
            .comment("Cracked Core: cooldown in seconds between extra lightning strikes, Default: 3")
            .defineInRange("crackedCoreCooldownSeconds", 3, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue INFUSED_CORE_LIGHTNING_RADIUS = BUILDER
            .comment("Infused Core: radius (blocks) of the circular area around the attacker where lightning bolts search for hostile targets, Default: 7.0")
            .defineInRange("infusedCoreLightningRadius", 7.0D, 1.0D, 64.0D);

    // ===== Defect: Metronome (only CD configurable) =====
    private static final ForgeConfigSpec.IntValue METRONOME_COOLDOWN_SECONDS = BUILDER
            .comment("Metronome: cooldown in seconds between burst triggers, Default: 30")
            .defineInRange("metronomeCooldownSeconds", 30, 1, Integer.MAX_VALUE);

    // ===== Defect: Data Disk =====
    private static final ForgeConfigSpec.IntValue DATA_DISK_CORE_BONUS = BUILDER
            .comment("Data Disk: bonus damage added to core lightning and virus sonic boom per hit, Default: 1")
            .defineInRange("dataDiskCoreBonus", 1, 0, Integer.MAX_VALUE);

    // ===== Necrobinder: Funerary Mask =====
    private static final ForgeConfigSpec.DoubleValue FUNERARY_MASK_ATTACK_SPEED_BONUS = BUILDER
            .comment("Funerary Mask: attack speed multiplier when hitting the same target (1.0 = +0%), Default: 0.2 (+20%)")
            .defineInRange("funeraryMaskAttackSpeedBonus", 0.2D, 0.0D, 10.0D);

    // ===== Regent: Fencing Manual =====
    private static final ForgeConfigSpec.IntValue FENCING_MANUAL_COOLDOWN_SECONDS = BUILDER
            .comment("Fencing Manual: cooldown in seconds between each forge, Default: 15")
            .defineInRange("fencingManualCooldownSeconds", 15, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue LUNAR_PASTRY_MANA_BONUS = BUILDER
            .comment("Lunar Pastry: bonus mana added on each mana restoration event, Default: 1")
            .defineInRange("lunarPastryManaBonus", 1, 1, Integer.MAX_VALUE);

    // ===== Regent: Vitruvian Minion =====
    private static final ForgeConfigSpec.DoubleValue VITRUVIAN_MINION_DAMAGE_MULTIPLIER = BUILDER
            .comment("Vitruvian Minion: damage multiplier for all player-owned summons, Default: 2.0")
            .defineInRange("vitruvianMinionDamageMultiplier", 2.0D, 1.0D, 100.0D);

    private static final ForgeConfigSpec.DoubleValue VITRUVIAN_MINION_HEALTH_MULTIPLIER = BUILDER
            .comment("Vitruvian Minion: max health multiplier for all player-owned summons, Default: 2.0")
            .defineInRange("vitruvianMinionHealthMultiplier", 2.0D, 1.0D, 100.0D);

    // ===== Regent: Mini Regent (ATK hardcoded, CD configurable) =====
    private static final ForgeConfigSpec.IntValue MINI_REGENT_COOLDOWN_SECONDS = BUILDER
            .comment("Mini Regent: cooldown in seconds between mana consumption granting ATK, Default: 3")
            .defineInRange("miniRegentCooldownSeconds", 3, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue MINI_REGENT_IDLE_SECONDS = BUILDER
            .comment("Mini Regent: idle seconds without attacking before losing all accumulated ATK, Default: 10")
            .defineInRange("miniRegentIdleSeconds", 10, 1, Integer.MAX_VALUE);

    // ===== Ironclad: Red Skull =====
    private static final ForgeConfigSpec.DoubleValue RED_SKULL_ATTACK_BONUS = BUILDER
            .comment("Red Skull: bonus attack damage when HP is below 50%, Default: 3.0")
            .defineInRange("redSkullAttackBonus", 3.0D, 0.0D, Double.MAX_VALUE);

    // ===== Ironclad: Self-Forming Clay (CD only) =====
    private static final ForgeConfigSpec.IntValue SELF_FORMING_CLAY_COOLDOWN_TICKS = BUILDER
            .comment("Self-Forming Clay: cooldown in ticks between block gains, Default: 20 (1s)")
            .defineInRange("selfFormingClayCooldownTicks", 20, 0, Integer.MAX_VALUE);

    // ===== Ironclad: Demon Tongue =====
    private static final ForgeConfigSpec.IntValue DEMON_TONGUE_COOLDOWN_TICKS = BUILDER
            .comment("Demon Tongue: cooldown in ticks between heal-on-damage triggers, Default: 100 (5s)")
            .defineInRange("demonTongueCooldownTicks", 100, 0, Integer.MAX_VALUE);

    // ===== Ironclad: Brimstone (CD only configurable) =====
    private static final ForgeConfigSpec.IntValue BRIMSTONE_COOLDOWN_TICKS = BUILDER
            .comment("Brimstone: cooldown in ticks between ATK gain triggers, Default: 100 (5s)")
            .defineInRange("brimstoneCooldownTicks", 100, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue BRIMSTONE_IDLE_SECONDS = BUILDER
            .comment("Brimstone: idle seconds without attacking before all stacks are lost, Default: 10")
            .defineInRange("brimstoneIdleSeconds", 10, 1, Integer.MAX_VALUE);

    // ===== Silent: Ring search radius =====
    private static final ForgeConfigSpec.IntValue RING_SEARCH_RADIUS = BUILDER
            .comment("Ring of the Snake / Ring of the Drake: search radius (blocks) for next bounce target, Default: 12")
            .defineInRange("ringSearchRadius", 12, 1, 16);

    // ===== Silent: Helical Dart buff duration =====
    private static final ForgeConfigSpec.IntValue HELICAL_DART_BUFF_SECONDS = BUILDER
            .comment("Helical Dart: duration in seconds of the block bonus buff after 3 bounces, Default: 10")
            .defineInRange("helicalDartBuffSeconds", 10, 1, Integer.MAX_VALUE);

    // ===== Watcher: Fiddle =====
    private static final ForgeConfigSpec.DoubleValue FIDDLE_ATTACK_SPEED = BUILDER
            .comment("Fiddle: attack speed value when equipped (overrides all other AS modifiers), Default: 2.0")
            .defineInRange("fiddleAttackSpeed", 2.0D, 0.0D, Double.MAX_VALUE);

    // ===== Watcher: Jeweled Mask =====
    private static final ForgeConfigSpec.IntValue JEWELED_MASK_DURATION_SECONDS = BUILDER
            .comment("Jeweled Mask: duration in seconds of the random positive buff, Default: 30")
            .defineInRange("jeweledMaskDurationSeconds", 30, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue JEWELED_MASK_COOLDOWN_SECONDS = BUILDER
            .comment("Jeweled Mask: cooldown in seconds between buff expiration and next buff, Default: 30")
            .defineInRange("jeweledMaskCooldownSeconds", 30, 1, Integer.MAX_VALUE);

    // ===== Watcher: Preserved Fog =====
    private static final ForgeConfigSpec.DoubleValue PRESERVED_FOG_ATTACK_SPEED = BUILDER
            .comment("Preserved Fog: attack speed reduction when equipped, Default: -0.1")
            .defineInRange("preservedFogAttackSpeed", -0.1D, -Double.MAX_VALUE, Double.MAX_VALUE);

    // ===== Silent: Ninja Scroll (hardcoded +3 slots, no config) =====
    public static final int NINJA_SCROLL_SLOT_BONUS = 3;

    static final ForgeConfigSpec SPEC = BUILDER.build();

    // ===== Static fields populated from config =====
    public static boolean logDirtBlock;
    public static int magicNumber;
    public static String magicNumberIntroduction;
    public static Set<Item> items;
    public static int magentaSpearCooldownTicks;
    public static double frustaDamage;
    public static double magentaSpearDamage;
    public static double azureWaterBeamDamage;
    public static int whisperingIdleSeconds;
    public static double whisperingDamageThreshold;
    public static double whisperingSelfDamageRatio;
    public static int boundPhylacteryHealthGainIntervalSeconds;
    public static int unboundPhylacteryHealthGainIntervalSeconds;
    public static int divineRightManaFloor;
    public static double divineDestinyManaRatio;
    public static int crackedCoreCooldownSeconds;
    public static double infusedCoreLightningRadius;
    public static int metronomeCooldownSeconds;
    public static int dataDiskCoreBonus;
    public static double funeraryMaskAttackSpeedBonus;
    public static int fencingManualCooldownSeconds;
    public static int lunarPastryManaBonus;
    public static double vitruvianMinionDamageMultiplier;
    public static double vitruvianMinionHealthMultiplier;
    public static int miniRegentCooldownSeconds;
    public static int miniRegentIdleSeconds;
    public static double redSkullAttackBonus;
    public static int selfFormingClayCooldownTicks;
    public static int demonTongueCooldownTicks;
    public static int brimstoneCooldownTicks;
    public static int brimstoneIdleSeconds;
    public static int ringSearchRadius;
    public static int helicalDartBuffSeconds;
    public static double fiddleAttackSpeed;
    public static int jeweledMaskDurationSeconds;
    public static int jeweledMaskCooldownSeconds;
    public static double preservedFogAttackSpeed;

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(ResourceLocation.tryParse(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        logDirtBlock = LOG_DIRT_BLOCK.get();
        magicNumber = MAGIC_NUMBER.get();
        magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();

        items = ITEM_STRINGS.get().stream()
                .map(itemName -> ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(itemName)))
                .collect(Collectors.toSet());

        magentaSpearCooldownTicks = MAGENTA_SPEAR_COOLDOWN_TICKS.get();
        frustaDamage = FRUSTA_DAMAGE.get();
        magentaSpearDamage = MAGENTA_SPEAR_DAMAGE.get();
        azureWaterBeamDamage = AZURE_WATER_BEAM_DAMAGE.get();
        whisperingIdleSeconds = WHISPERING_IDLE_SECONDS.get();
        whisperingDamageThreshold = WHISPERING_DAMAGE_THRESHOLD.get();
        whisperingSelfDamageRatio = WHISPERING_SELF_DAMAGE_RATIO.get();
        boundPhylacteryHealthGainIntervalSeconds = BOUND_PHYLACTERY_HEALTH_GAIN_INTERVAL.get();
        unboundPhylacteryHealthGainIntervalSeconds = UNBOUND_PHYLACTERY_HEALTH_GAIN_INTERVAL.get();
        divineRightManaFloor = DIVINE_RIGHT_MANA_FLOOR.get();
        divineDestinyManaRatio = DIVINE_DESTINY_MANA_RATIO.get();
        crackedCoreCooldownSeconds = CRACKED_CORE_COOLDOWN_SECONDS.get();
        infusedCoreLightningRadius = INFUSED_CORE_LIGHTNING_RADIUS.get();
        metronomeCooldownSeconds = METRONOME_COOLDOWN_SECONDS.get();
        dataDiskCoreBonus = DATA_DISK_CORE_BONUS.get();
        funeraryMaskAttackSpeedBonus = FUNERARY_MASK_ATTACK_SPEED_BONUS.get();
        fencingManualCooldownSeconds = FENCING_MANUAL_COOLDOWN_SECONDS.get();
        lunarPastryManaBonus = LUNAR_PASTRY_MANA_BONUS.get();
        vitruvianMinionDamageMultiplier = VITRUVIAN_MINION_DAMAGE_MULTIPLIER.get();
        vitruvianMinionHealthMultiplier = VITRUVIAN_MINION_HEALTH_MULTIPLIER.get();
        miniRegentCooldownSeconds = MINI_REGENT_COOLDOWN_SECONDS.get();
        miniRegentIdleSeconds = MINI_REGENT_IDLE_SECONDS.get();
        redSkullAttackBonus = RED_SKULL_ATTACK_BONUS.get();
        selfFormingClayCooldownTicks = SELF_FORMING_CLAY_COOLDOWN_TICKS.get();
        demonTongueCooldownTicks = DEMON_TONGUE_COOLDOWN_TICKS.get();
        brimstoneCooldownTicks = BRIMSTONE_COOLDOWN_TICKS.get();
        brimstoneIdleSeconds = BRIMSTONE_IDLE_SECONDS.get();
        ringSearchRadius = RING_SEARCH_RADIUS.get();
        helicalDartBuffSeconds = HELICAL_DART_BUFF_SECONDS.get();
        fiddleAttackSpeed = FIDDLE_ATTACK_SPEED.get();
        jeweledMaskDurationSeconds = JEWELED_MASK_DURATION_SECONDS.get();
        jeweledMaskCooldownSeconds = JEWELED_MASK_COOLDOWN_SECONDS.get();
        preservedFogAttackSpeed = PRESERVED_FOG_ATTACK_SPEED.get();
    }
}
