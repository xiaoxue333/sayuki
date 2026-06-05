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

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = Sayuki.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER
            .comment("Whether to log the dirt block on common setup")
            .define("logDirtBlock", true);

    private static final ForgeConfigSpec.IntValue MAGIC_NUMBER = BUILDER
            .comment("A magic number")
            .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
            .comment("What you want the introduction message to be for the magic number")
            .define("magicNumberIntroduction", "The magic number is... ");

    // a list of strings that are treated as resource locations for items
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), Config::validateItemName);

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

    private static final ForgeConfigSpec.IntValue WHISPERING_IDLE_SECONDS = BUILDER
            .comment("Whispering Earring: max idle seconds without attacking before penalty triggers, Default: 7")
            .defineInRange("whisperingIdleSeconds", 7, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue WHISPERING_DAMAGE_THRESHOLD = BUILDER
            .comment("Whispering Earring: minimum total damage required within the idle window to avoid penalty, Default: 100.0")
            .defineInRange("whisperingDamageThreshold", 100.0D, 0.0D, Double.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue WHISPERING_SELF_DAMAGE_RATIO = BUILDER
            .comment("Whispering Earring: ratio of attack damage reflected back to self when penalty triggers, Default: 0.5")
            .defineInRange("whisperingSelfDamageRatio", 0.5D, 0.0D, 1.0D);

    private static final ForgeConfigSpec.IntValue BOUND_PHYLACTERY_HEALTH_GAIN_INTERVAL = BUILDER
            .comment("Bound Phylactery: interval in seconds between each absorption gain (+1), Default: 10")
            .defineInRange("boundPhylacteryHealthGainIntervalSeconds", 10, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue UNBOUND_PHYLACTERY_HEALTH_GAIN_INTERVAL = BUILDER
            .comment("Unbound Phylactery: interval in seconds between each absorption gain (+2), Default: 7")
            .defineInRange("unboundPhylacteryHealthGainIntervalSeconds", 7, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue DIVINE_RIGHT_MANA_FLOOR = BUILDER
            .comment("Divine Right: flat max mana bonus, Default: 30")
            .defineInRange("divineRightManaFloor", 30, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue DIVINE_DESTINY_MANA_RATIO = BUILDER
            .comment("Divine Destiny: max mana bonus as ratio of current max mana (0.3 = 30%), Default: 0.3")
            .defineInRange("divineDestinyManaRatio", 0.3D, 0.0D, 1.0D);

    private static final ForgeConfigSpec.IntValue CRACKED_CORE_COOLDOWN_SECONDS = BUILDER
            .comment("Cracked Core: cooldown in seconds between extra lightning strikes, Default: 3")
            .defineInRange("crackedCoreCooldownSeconds", 3, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue INFUSED_CORE_LIGHTNING_RADIUS = BUILDER
            .comment("Infused Core: radius (blocks) of the circular area around the attacker where lightning bolts search for hostile targets, Default: 7.0")
            .defineInRange("infusedCoreLightningRadius", 7.0D, 1.0D, 64.0D);

    private static final ForgeConfigSpec.IntValue METRONOME_LIGHTNING_THRESHOLD = BUILDER
            .comment("Metronome: number of lightning bolts that must be summoned before triggering the burst, Default: 7")
            .defineInRange("metronomeLightningThreshold", 7, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue METRONOME_DAMAGE = BUILDER
            .comment("Metronome: damage dealt to all hostiles when the burst triggers, Default: 30.0")
            .defineInRange("metronomeDamage", 30.0D, 0.0D, Double.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue METRONOME_COOLDOWN_SECONDS = BUILDER
            .comment("Metronome: cooldown in seconds between burst triggers, Default: 30")
            .defineInRange("metronomeCooldownSeconds", 30, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue RUNIC_CAPACITOR_SLOT_BONUS = BUILDER
            .comment("Runic Capacitor: number of additional relic slots granted when equipped, Default: 3")
            .defineInRange("runicCapacitorSlotBonus", 3, 1, 64);

    private static final ForgeConfigSpec.IntValue POWER_CELL_SLOT_BONUS = BUILDER
            .comment("Power Cell: number of additional relic slots granted when equipped, Default: 2")
            .defineInRange("powerCellSlotBonus", 2, 1, 64);

    private static final ForgeConfigSpec.DoubleValue POWER_CELL_MANA_PERCENT = BUILDER
            .comment("Power Cell: percentage of current max mana added as bonus (0.0 ~ 1.0), Default: 0.2 (20%)")
            .defineInRange("powerCellManaPercent", 0.2D, 0.0D, 10.0D);

    private static final ForgeConfigSpec.DoubleValue BONE_FLUTE_HEALTH_PER_HIT = BUILDER
            .comment("Bone Flute: absorption health gained per melee hit, Default: 2.0")
            .defineInRange("boneFluteHealthPerHit", 2.0D, 0.0D, Double.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue BOOK_REPAIR_KNIFE_HEAL_PER_CHARGE = BUILDER
            .comment("Book Repair Knife: amount of healing needed for one Doom charge, Default: 3.0")
            .defineInRange("bookRepairKnifeHealPerCharge", 3.0D, 0.0D, Double.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue FUNERARY_MASK_ATTACK_SPEED_BONUS = BUILDER
            .comment("Funerary Mask: attack speed multiplier when hitting the same target (1.0 = +0%), Default: 0.2 (+20%)")
            .defineInRange("funeraryMaskAttackSpeedBonus", 0.2D, 0.0D, 10.0D);

    private static final ForgeConfigSpec.IntValue FENCING_MANUAL_COOLDOWN_SECONDS = BUILDER
            .comment("Fencing Manual: cooldown in seconds between each forge, Default: 15")
            .defineInRange("fencingManualCooldownSeconds", 15, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue LUNAR_PASTRY_MANA_BONUS = BUILDER
            .comment("Lunar Pastry: bonus mana added on each mana restoration event, Default: 1")
            .defineInRange("lunarPastryManaBonus", 1, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue ORANGE_DOUGH_SLOT_BONUS = BUILDER
            .comment("Orange Dough: number of additional relic slots granted when equipped, Default: 2")
            .defineInRange("orangeDoughSlotBonus", 2, 1, 64);

    private static final ForgeConfigSpec.DoubleValue VITRUVIAN_MINION_DAMAGE_MULTIPLIER = BUILDER
            .comment("Vitruvian Minion: damage multiplier for all player-owned summons, Default: 2.0")
            .defineInRange("vitruvianMinionDamageMultiplier", 2.0D, 1.0D, 100.0D);

    private static final ForgeConfigSpec.DoubleValue VITRUVIAN_MINION_HEALTH_MULTIPLIER = BUILDER
            .comment("Vitruvian Minion: max health multiplier for all player-owned summons, Default: 2.0")
            .defineInRange("vitruvianMinionHealthMultiplier", 2.0D, 1.0D, 100.0D);

    private static final ForgeConfigSpec.IntValue MINI_REGENT_ATTACK_PER_CAST = BUILDER
            .comment("Mini Regent: attack damage gained per mana consumption (each cast counts as one, not per point), Default: 1")
            .defineInRange("miniRegentAttackPerCast", 1, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue MINI_REGENT_COOLDOWN_SECONDS = BUILDER
            .comment("Mini Regent: cooldown in seconds between mana consumption granting ATK, Default: 3")
            .defineInRange("miniRegentCooldownSeconds", 3, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue MINI_REGENT_IDLE_SECONDS = BUILDER
            .comment("Mini Regent: idle seconds without attacking before losing all accumulated ATK, Default: 10")
            .defineInRange("miniRegentIdleSeconds", 10, 1, Integer.MAX_VALUE);

    // === Sulfur Gauntlet ===
    // Sulfur Gauntlet — removed.

    static final ForgeConfigSpec SPEC = BUILDER.build();

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
    public static int metronomeLightningThreshold;
    public static double metronomeDamage;
    public static int metronomeCooldownSeconds;
    public static int runicCapacitorSlotBonus;
    public static int powerCellSlotBonus;
    public static double powerCellManaPercent;
    public static double boneFluteHealthPerHit;
    public static double bookRepairKnifeHealPerCharge;
    public static double funeraryMaskAttackSpeedBonus;
    public static int fencingManualCooldownSeconds;
    public static int lunarPastryManaBonus;
    public static int orangeDoughSlotBonus;
    public static double vitruvianMinionDamageMultiplier;
    public static double vitruvianMinionHealthMultiplier;
    public static int miniRegentAttackPerCast;
    public static int miniRegentCooldownSeconds;
    public static int miniRegentIdleSeconds;
    // Sulfur Gauntlet — removed.

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
        metronomeLightningThreshold = METRONOME_LIGHTNING_THRESHOLD.get();
        metronomeDamage = METRONOME_DAMAGE.get();
        metronomeCooldownSeconds = METRONOME_COOLDOWN_SECONDS.get();
        runicCapacitorSlotBonus = RUNIC_CAPACITOR_SLOT_BONUS.get();
        powerCellSlotBonus = POWER_CELL_SLOT_BONUS.get();
        powerCellManaPercent = POWER_CELL_MANA_PERCENT.get();
        boneFluteHealthPerHit = BONE_FLUTE_HEALTH_PER_HIT.get();
        bookRepairKnifeHealPerCharge = BOOK_REPAIR_KNIFE_HEAL_PER_CHARGE.get();
        funeraryMaskAttackSpeedBonus = FUNERARY_MASK_ATTACK_SPEED_BONUS.get();
        fencingManualCooldownSeconds = FENCING_MANUAL_COOLDOWN_SECONDS.get();
        lunarPastryManaBonus = LUNAR_PASTRY_MANA_BONUS.get();
        orangeDoughSlotBonus = ORANGE_DOUGH_SLOT_BONUS.get();
        vitruvianMinionDamageMultiplier = VITRUVIAN_MINION_DAMAGE_MULTIPLIER.get();
        vitruvianMinionHealthMultiplier = VITRUVIAN_MINION_HEALTH_MULTIPLIER.get();
        miniRegentAttackPerCast = MINI_REGENT_ATTACK_PER_CAST.get();
        miniRegentCooldownSeconds = MINI_REGENT_COOLDOWN_SECONDS.get();
        miniRegentIdleSeconds = MINI_REGENT_IDLE_SECONDS.get();
    }
}
