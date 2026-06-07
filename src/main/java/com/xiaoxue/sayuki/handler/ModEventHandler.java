/**
 * Sayuki — Forge EVENT_BUS handlers: RightClickItem, LivingHurt, CurioChange, MobEffectEvent.Expired
 * Compat: Goety-2 — independent event subscriber, no shared handler conflict
 * Compat: IronsSpellbooks — independent event subscriber; ISSB may also intercept LivingHurt
 */
package com.xiaoxue.sayuki.handler;

import com.xiaoxue.sayuki.Config;
import com.xiaoxue.sayuki.compat.GoetyCompat;
import com.xiaoxue.sayuki.compat.IronSpellsCompat;
import com.xiaoxue.sayuki.damage.ModDamageTypes;
import com.xiaoxue.sayuki.effect.ModEffects;
import com.xiaoxue.sayuki.item.AzureSword;
import com.xiaoxue.sayuki.item.FrustaDominate;
import com.xiaoxue.sayuki.item.MagentaSpearItem;
import com.xiaoxue.sayuki.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = com.xiaoxue.sayuki.Sayuki.MOD_ID)
public class ModEventHandler {

    private static final UUID CHERRY_EAR_ORNAMENTS_REACH_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private static final UUID HEART_EAR_ORNAMENTS_MAX_HEALTH_UUID = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901");
    private static final UUID HEART_EAR_ORNAMENTS_ATTACK_SPEED_UUID = UUID.fromString("c3d4e5f6-a7b8-9012-cdef-123456789012");
    private static final UUID VOODOO_NAMESPACE_UUID = UUID.fromString("d4e5f6a7-b8c9-0123-defa-bcdef1234567");
    private static final UUID WHISPERING_EARRING_ATTACK_UUID = UUID.fromString("e5f6a7b8-c9d0-1234-efab-cdef12345678");
    private static final UUID DIVINE_RIGHT_MAX_MANA_UUID = UUID.fromString("b9c0d1e2-f3a4-5678-bcde-f123456789ab");
    private static final UUID DIVINE_DESTINY_MAX_MANA_UUID = UUID.fromString("c0d1e2f3-a4b5-6789-cdef-123456789abc");
    private static final UUID DATA_DISK_ALL_SPELL_POWER_UUID = UUID.fromString("d1e2f3a4-b5c6-7890-defa-bcdef1234567");
    private static final UUID POWER_CELL_MAX_MANA_UUID = UUID.fromString("f3a4b5c6-d7e8-9012-fabc-def123456789");
    private static final UUID FUNERARY_MASK_ATTACK_SPEED_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private static final UUID DIVINE_RIGHT_FORGED_SWORD_AS_UUID = UUID.fromString("d5e6f7a8-b9c0-1234-defa-bcdef1234568");
    private static final UUID DIVINE_DESTINY_FORGED_SWORD_AS_UUID = UUID.fromString("e6f7a8b9-c0d1-2345-efab-cdef12345679");
    private static final UUID MINI_REGENT_ATTACK_UUID = UUID.fromString("b4c5d6e7-f8a9-0123-abcd-ef1234567890");
    private static final UUID RED_SKULL_ATTACK_UUID = UUID.fromString("c5d6e7f8-a9b0-1234-bcde-f12345678901");
    private static final UUID DISTINGUISHED_CAPE_MAX_HEALTH_UUID = UUID.fromString("f9a0b1c2-d3e4-5678-fabc-def123456790");
    private static final UUID FIDDLE_ATTACK_SPEED_UUID = UUID.fromString("a0b1c2d3-e4f5-6789-abcd-ef1234567891");
    private static final UUID BLOOD_SOAKED_ROSE_ATTACK_SPEED_UUID = UUID.fromString("b1c2d3e4-f5a6-7890-bcde-f12345678902");
    private static final UUID BLOOD_SOAKED_ROSE_ATTACK_UUID = UUID.fromString("c2d3e4f5-a6b7-8901-cdef-123456789023");
    private static final UUID PRESERVED_FOG_ATTACK_SPEED_UUID = UUID.fromString("d3e4f5a6-b7c8-9012-defa-bcdef1234567");
    private static final UUID RUINED_HELMET_UUID = UUID.fromString("d6e7f8a9-b0c1-2345-cdef-123456789012");
    private static final UUID BRIMSTONE_ATTACK_UUID = UUID.fromString("e7f8a9b0-c1d2-3456-defa-bcdef1234567");
    private static final UUID BRIMSTONE_TARGET_TAG = UUID.fromString("f8a9b0c1-d2e3-4567-efab-cdef12345678");
    private static final int ABILITY_XP_COST = 7;

    private static final String PKEY_WHISPERING_EQUIPPED = "SayukiWearWhispering";
    private static final String PKEY_WHISPERING_WEAPON_DMG = "SayukiWhisperingWeaponDmg";
    private static final String PKEY_WHISPERING_WINDOW_START = "SayukiWhisperingWindowStart";
    private static final String PKEY_WHISPERING_WINDOW_DMG = "SayukiWhisperingWindowDamage";
    private static final String PKEY_BOUND_HP_ACCUM = "SayukiBoundPhylacteryHp";
    private static final String PKEY_UNBOUND_HP_ACCUM = "SayukiUnboundPhylacteryHp";
    private static final String PKEY_HEAVEN_EAR_ORNAMENTS = "SayukiHeavenEarOrnaments";
    private static final String PKEY_BLACK_BLOOD_EMPOWERED = "SayukiBlackBloodEmpowered";
    private static final String PKEY_BOUNCE_COUNT = "sayukiBounceCount";
    private static final String PKEY_CRACKED_CORE_COOLDOWN = "SayukiCrackedCoreCooldown";
    private static final String PKEY_SYMBIOTIC_VIRUS_COOLDOWN = "SayukiSymbioticVirusCooldown";
    private static final String PKEY_METRONOME_COUNTER = "SayukiMetronomeCounter";
    private static final String PKEY_METRONOME_COOLDOWN = "SayukiMetronomeCooldown";
    private static final String PKEY_BONE_FLUTE_HP_ACCUM = "SayukiBoneFluteHp";
    private static final String PKEY_BOOK_REPAIR_KNIFE_HEAL_ACCUM = "SayukiBookRepairKnifeHeal";
    private static final String PKEY_BOOK_REPAIR_KNIFE_CHARGES = "SayukiBookRepairKnifeCharges";
    private static final String PKEY_BOOK_REPAIR_KNIFE_LAST_ABSORPTION = "SayukiBookRepairKnifeLastAbsorption";
    private static final String PKEY_FUNERARY_MASK_LAST_TARGET = "SayukiFuneraryMaskLastTarget";
    private static final String PKEY_FUNERARY_MASK_LAST_HIT_TIME = "SayukiFuneraryMaskLastHitTime";
    private static final String PKEY_BOOKMARK_CHARGES = "SayukiBookmarkCharges";
    private static final String PKEY_BOOKMARK_LAST_SOULS = "SayukiBookmarkLastSouls";
    private static final String PKEY_BOOKMARK_EQUIPPED = "SayukiBookmarkEquipped";
    private static final String PKEY_IVORY_TILE_REMAINDER = "SayukiIvoryTileRemainder";
    private static final String PKEY_IVORY_TILE_LAST_SOULS = "SayukiIvoryTileLastSouls";
    private static final String PKEY_IVORY_TILE_EQUIPPED = "SayukiIvoryTileEquipped";
    private static final String PKEY_FENCING_MANUAL_COOLDOWN = "SayukiFencingManualCooldown";
    private static final String PKEY_GALACTIC_DUST_REMAINDER = "SayukiGalacticDustRemainder";
    private static final String PKEY_GALACTIC_DUST_LAST_MANA = "SayukiGalacticDustLastMana";
    private static final String PKEY_GALACTIC_DUST_EQUIPPED = "SayukiGalacticDustEquipped";
    private static final String PKEY_GALACTIC_DUST_BLOCK = "SayukiGalacticDustBlock";
    private static final String PKEY_HELICAL_DART_EQUIPPED = "SayukiHelicalDartEquipped";
    private static final String PKEY_HELICAL_DART_BOUNCES = "SayukiHelicalDartBounces";
    private static final String PKEY_HELICAL_DART_BUFF_END = "SayukiHelicalDartBuffEnd";
    private static final String PKEY_SELF_FORMING_CLAY_COOLDOWN = "SayukiSelfFormingClayCooldown";
    private static final String PKEY_CHARONS_ASHES_STACKS = "SayukiCharonsAshesStacks";
    private static final String PKEY_CHARONS_ASHES_LAST_ITEM = "SayukiCharonsAshesLastItem";
    private static final String PKEY_CHARONS_ASHES_LAST_COUNT = "SayukiCharonsAshesLastCount";
    private static final String PKEY_DEMON_TONGUE_COOLDOWN = "SayukiDemonTongueCooldown";
    private static final String PKEY_BRIMSTONE_STACKS = "SayukiBrimstoneStacks";
    private static final String PKEY_BRIMSTONE_LAST_STACK = "SayukiBrimstoneLastStack";
    private static final String PKEY_BRIMSTONE_LAST_ATTACK = "SayukiBrimstoneLastAttack";
    private static final String PKEY_BRIMSTONE_TARGET_ATK = "SayukiBrimstoneTargetAtk";
    private static final String PKEY_BRIMSTONE_TARGET_ATTACKS = "SayukiBrimstoneTargetAttacks";
    private static final String PKEY_LUNAR_PASTRY_EQUIPPED = "SayukiLunarPastryEquipped";
    private static final String PKEY_LUNAR_PASTRY_LAST_MANA = "SayukiLunarPastryLastMana";
    private static final String PKEY_MINI_REGENT_EQUIPPED = "SayukiMiniRegentEquipped";
    private static final String PKEY_MINI_REGENT_ATTACK_BONUS = "SayukiMiniRegentAttackBonus";
    private static final String PKEY_MINI_REGENT_LAST_CAST = "SayukiMiniRegentLastCast";
    private static final String PKEY_MINI_REGENT_LAST_MANA = "SayukiMiniRegentLastMana";
    private static final String PKEY_MINI_REGENT_LAST_ATTACK = "SayukiMiniRegentLastAttack";
    private static final String PKEY_VITRUVIAN_MINION_EQUIPPED = "SayukiVitruvianMinionEquipped";
    private static final String PKEY_VITRUVIAN_BUFFED = "SayukiVitruvianBuffed";

    // Music Box: disc playback & double-hit
    private static final String PKEY_MUSIC_BOX_PLAYING = "SayukiMusicBoxPlaying";
    private static final String PKEY_MUSIC_BOX_TRIGGER_TICK = "SayukiMusicBoxTriggerTick";
    private static final String PKEY_MUSIC_BOX_CHARGE = "SayukiMusicBoxCharge";
    private static final String PKEY_MUSIC_BOX_DOUBLE_HIT = "SayukiMusicBoxDoubleHit";

    // Distinguished Cape: damage cap charges
    private static final String PKEY_DISTINGUISHED_CAPE_CHARGES = "SayukiDistinguishedCapeCharges";
    private static final String PKEY_DISTINGUISHED_CAPE_COOLDOWN = "SayukiDistinguishedCapeCooldown";

    // Fiddle: attack speed override
    private static final String PKEY_FIDDLE_EQUIPPED = "SayukiFiddleEquipped";

    // Blood-Soaked Rose: weapon damage sync (same logic as Whispering Earring)
    private static final String PKEY_BLOOD_SOAKED_ROSE_EQUIPPED = "SayukiBloodSoakedRoseEquipped";
    private static final String PKEY_BLOOD_SOAKED_ROSE_WEAPON_DMG = "SayukiBloodSoakedRoseWeaponDmg";

    // Jeweled Mask: random buff cycle
    private static final String PKEY_JEWELED_MASK_ACTIVE = "SayukiJeweledMaskActive";
    private static final String PKEY_JEWELED_MASK_NEXT_TRIGGER = "SayukiJeweledMaskNextTrigger";
    private static final String PKEY_JEWELED_MASK_CURRENT_EFFECT = "SayukiJeweledMaskCurrentEffect";

    // Sere Talon: permanent random debuffs + buffs
    private static final String PKEY_SERE_TALON_EQUIPPED = "SayukiSereTalonEquipped";
    private static final String PKEY_SERE_TALON_EFFECTS = "SayukiSereTalonEffects";
    private static final int SERE_TALON_NEGATIVE_COUNT = 2;
    private static final int SERE_TALON_POSITIVE_COUNT = 3;

    // Preserved Fog: relic slot bonus (hardcoded +3)
    public static final int PRESERVED_FOG_SLOT_BONUS = 3;

    /** Guard flag to prevent recursive lightning damage from core items. */
    private static boolean applyingCoreLightning = false;

    /** Check if player has Gold-Plated Cables equipped in curios relic slot. */
    private static boolean hasGoldPlatedCables(Player player) {
        return CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(handler -> handler.findFirstCurio(
                        stack -> stack.getItem() == ModItems.GOLD_PLATED_CABLES.get()))
                .isPresent();
    }

    // === RightClickItem ===

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = player.getItemInHand(event.getHand());

        if (stack.getItem() == ModItems.FRUSTA_DOMINATE.get()) {
            if (player.level().isClientSide()) return;
            if (player.totalExperience < ABILITY_XP_COST) return;
            FrustaDominate.performSonicBoom(player.level(), player);
            player.giveExperiencePoints(-ABILITY_XP_COST);
            return;
        }

        if (stack.getItem() == ModItems.AZURE_SWORD.get()) {
            if (player.level().isClientSide()) return;
            if (player.totalExperience < ABILITY_XP_COST) return;
            AzureSword.performWaterBeam(player.level(), player);
            player.giveExperiencePoints(-ABILITY_XP_COST);
            return;
        }

        if (stack.getItem() == ModItems.MAGENTA_SPEAR.get()) {
            if (player.getCooldowns().isOnCooldown(stack.getItem())) return;

            stack.getOrCreateTag().putLong("SayukiSpearThrustTick", player.level().getGameTime());

            if (player.level().isClientSide()) return;

            MagentaSpearItem.performRapidThrust(player.level(), player);
            player.getCooldowns().addCooldown(stack.getItem(), Config.magentaSpearCooldownTicks);
            return;
        }
    }

    // === LivingEquipmentChange: Fiddle strips non-fiddle AS modifiers ===

    @SubscribeEvent
    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;
        if (entity.getPersistentData().getBoolean(PKEY_FIDDLE_EQUIPPED)) {
            stripNonFiddleAttackSpeed(entity);
        }
    }

    // === LivingHurt: Distinguished Cape — cap damage to ≤1 when charges active ===

    @SubscribeEvent
    public static void onLivingHurtDistinguishedCape(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        // Check cape equipped
        var cape = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.DISTINGUISHED_CAPE.get()));
        if (cape.isEmpty()) return;

        int charges = player.getPersistentData().getInt(PKEY_DISTINGUISHED_CAPE_CHARGES);
        if (charges > 0 && event.getAmount() > 1.0F) {
            event.setAmount(1.0F);
            player.getPersistentData().putInt(PKEY_DISTINGUISHED_CAPE_CHARGES, charges - 1);
        }
    }

    // === LivingHurt: Weak Power — attacker deals -25% damage ===

    @SubscribeEvent
    public static void onLivingHurtWeakPower(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker
                && attacker.hasEffect(ModEffects.WEAK_POWER.get())) {
            event.setAmount(event.getAmount() * 0.75F);
        }
    }

    // === LivingHurt: Doom Power — instant death if health <= amplifier ===

    @SubscribeEvent
    public static void onLivingHurtDoomPower(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();
        var effect = victim.getEffect(ModEffects.DOOM_POWER.get());
        if (effect != null) {
            int level = effect.getAmplifier() + 1; // level I = 1, level II = 2, ...
            if (victim.getHealth() <= level) {
                event.setAmount(Float.MAX_VALUE);
            }
        }
    }

    // === LivingHurt: Undying Sigil — -50% damage from Doom-afflicted attackers ===

    @SubscribeEvent
    public static void onLivingHurtUndyingSigil(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity le ? le : null;
        if (attacker == null) return;
        if (!attacker.hasEffect(ModEffects.DOOM_POWER.get())) return;

        var sigil = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.UNDYING_SIGIL.get()));
        if (sigil.isPresent()) {
            event.setAmount(event.getAmount() * 0.5F);
        }
    }

    // === LivingHurt: Doom relics (Book Repair Knife, Big Hat) — any player damage source ===

    @SubscribeEvent
    public static void onLivingHurtDoomRelics(LivingHurtEvent event) {
        Player player = resolvePlayerSource(event.getSource());
        if (player == null) return;
        if (event.getEntity() == player) return;

        // ---- Book Repair Knife: consume charge → apply Doom (level = absorption) ----
        int charges = player.getPersistentData().getInt(PKEY_BOOK_REPAIR_KNIFE_CHARGES);
        if (charges > 0) {
            player.getPersistentData().putInt(PKEY_BOOK_REPAIR_KNIFE_CHARGES, charges - 1);
            int doomLevel = (int) player.getAbsorptionAmount();
            if (doomLevel > 0) {
                LivingEntity target = event.getEntity();
                var existing = target.getEffect(ModEffects.DOOM_POWER.get());
                int newLevel = (existing != null) ? existing.getAmplifier() + 1 + doomLevel : doomLevel - 1;
                target.addEffect(new MobEffectInstance(ModEffects.DOOM_POWER.get(), -1, newLevel,
                        false, false, true));
                incrementBookmarkCharge(player);
            }
        }

        // ---- Big Hat: +1~10 Doom on targets with existing Doom ----
        var bigHat = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.BIG_HAT.get()));
        if (bigHat.isPresent()) {
            LivingEntity target = event.getEntity();
            var existing = target.getEffect(ModEffects.DOOM_POWER.get());
            if (existing != null) {
                int bonus = player.getRandom().nextInt(10) + 1;
                int newLevel = existing.getAmplifier() + bonus;
                target.addEffect(new MobEffectInstance(ModEffects.DOOM_POWER.get(), -1, newLevel,
                        false, false, true));
                incrementBookmarkCharge(player);
            }
        }
    }

    /** Increment Bookmark Doom-applied charge counter. */
    private static void incrementBookmarkCharge(Player player) {
        if (!player.getPersistentData().getBoolean(PKEY_BOOKMARK_EQUIPPED)) return;
        int bookmarkCharges = player.getPersistentData().getInt(PKEY_BOOKMARK_CHARGES);
        bookmarkCharges++;
        player.getPersistentData().putInt(PKEY_BOOKMARK_CHARGES, bookmarkCharges);
    }

    // === AttackEntity: Fencing Manual — forge/upgrade sword on melee hit ===

    @SubscribeEvent
    public static void onAttackFencingManual(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (!(event.getTarget() instanceof LivingEntity)) return;

        var manual = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.FENCING_MANUAL.get()));
        if (manual.isEmpty()) return;

        long now = player.level().getGameTime();
        int cdTicks = Config.fencingManualCooldownSeconds * 20;
        long cooldownEnd = player.getPersistentData().getLong(PKEY_FENCING_MANUAL_COOLDOWN);
        if (now < cooldownEnd) return;
        player.getPersistentData().putLong(PKEY_FENCING_MANUAL_COOLDOWN, now + cdTicks);
        player.getCooldowns().addCooldown(ModItems.FENCING_MANUAL.get(), cdTicks);

        // Find existing forged sword or create new one
        ItemStack sword = ItemStack.EMPTY;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s.getItem() == ModItems.FORGED_SWORD.get()) {
                sword = s;
                break;
            }
        }
        if (sword.isEmpty()) {
            sword = new ItemStack(ModItems.FORGED_SWORD.get());
            sword.getOrCreateTag().putBoolean("Unbreakable", true);
            player.addItem(sword);
        }

        // Read forge level from sword, increment, write back
        int forgeLevel = sword.getOrCreateTag().getInt("ForgeLevel") + 1;
        sword.getOrCreateTag().putInt("ForgeLevel", forgeLevel);
        sword.setHoverName(net.minecraft.network.chat.Component.literal("§e君王之剑 +" + forgeLevel));

        // Regalite: +1 XP per forge
        var regalite = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.REGALITE.get()));
        if (regalite.isPresent()) {
            player.giveExperiencePoints(1);
        }
    }

    // === AttackEntity: Mini Regent — reset idle timer ===

    @SubscribeEvent
    public static void onAttackMiniRegent(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (!(event.getTarget() instanceof LivingEntity)) return;
        if (!player.getPersistentData().getBoolean(PKEY_MINI_REGENT_EQUIPPED)) return;
        player.getPersistentData().putLong(PKEY_MINI_REGENT_LAST_ATTACK, player.level().getGameTime());
    }

    // === LivingHurt: Vitruvian Minion — summon damage xN ===

    @SubscribeEvent
    public static void onLivingHurtVitruvianMinion(LivingHurtEvent event) {
        Player owner = resolveOwner(event.getSource().getEntity());
        if (owner == null) return;
        if (!hasVitruvianMinion(owner)) return;
        event.setAmount((float) (event.getAmount() * Config.vitruvianMinionDamageMultiplier));
    }

    // === LivingHurt (original) ===

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (event.getEntity() == player) return; // skip self-damage recursion

        // Azure Sword double damage
        ItemStack weapon = player.getMainHandItem();
        if (weapon.getItem() == ModItems.AZURE_SWORD.get()) {
            LivingEntity target = event.getEntity();
            if (target.getPersistentData().getBoolean(AzureSword.MARK_DOUBLE_DAMAGE)) {
                target.getPersistentData().remove(AzureSword.MARK_DOUBLE_DAMAGE);
                event.setAmount(event.getAmount() * 2.0F);
            }
        }

        // Whispering Earring
        if (player.getPersistentData().getBoolean(PKEY_WHISPERING_EQUIPPED)) {
            syncWhisperingWeaponModifier(player);
            handleWhisperingBuffs(event, player);
        }

        // Blood-Soaked Rose: sync weapon damage modifier
        if (player.getPersistentData().getBoolean(PKEY_BLOOD_SOAKED_ROSE_EQUIPPED)) {
            syncBloodSoakedRoseWeaponModifier(player);
        }

        // Vulnerable Power: targets with the effect take +50% damage
        // Paper Phrog: +25% bonus against Vulnerable targets (total 1.75x)
        float vulnMult = 1.0F;
        if (event.getEntity().hasEffect(ModEffects.VULNERABLE_POWER.get())) {
            vulnMult = 1.5F;
            var phrog = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                    handler.findFirstCurio(stack -> stack.getItem() == ModItems.PAPER_PHROG.get()));
            if (phrog.isPresent()) {
                vulnMult = 1.75F;
            }
        }
        if (vulnMult > 1.0F) {
            event.setAmount(event.getAmount() * vulnMult);
        }

        // Black Blood empowered: next attack applies Vulnerable Power (duration stacking)
        if (player.getPersistentData().getBoolean(PKEY_BLACK_BLOOD_EMPOWERED)) {
            player.getPersistentData().remove(PKEY_BLACK_BLOOD_EMPOWERED);
            LivingEntity target = event.getEntity();
            var existing = target.getEffect(ModEffects.VULNERABLE_POWER.get());
            int newDuration = (existing != null) ? existing.getDuration() + 7 * 20 : 7 * 20;
            target.addEffect(new MobEffectInstance(ModEffects.VULNERABLE_POWER.get(), newDuration, 0,
                    false, false, true));
        }

        // ---- Charon's Ashes: consume stacks as bonus damage ----
        int ashesStacks = player.getPersistentData().getInt(PKEY_CHARONS_ASHES_STACKS);
        if (ashesStacks > 0) {
            var ashes = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                    handler.findFirstCurio(stack -> stack.getItem() == ModItems.CHARONS_ASHES.get()));
            if (ashes.isPresent()) {
                event.setAmount(event.getAmount() + (float) (ashesStacks * Config.CHARONS_ASHES_DAMAGE_PER_STACK));
                player.getPersistentData().putInt(PKEY_CHARONS_ASHES_STACKS, 0);
            }
        }

        // ---- Brimstone: on-attack ATK stacking + target ATK bonus ----
        var brimstone = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.BRIMSTONE.get()));
        if (brimstone.isPresent()) {
            long now = player.level().getGameTime();
            long lastStack = player.getPersistentData().getLong(PKEY_BRIMSTONE_LAST_STACK);
            if (now - lastStack >= Config.brimstoneCooldownTicks) {
                player.getPersistentData().putLong(PKEY_BRIMSTONE_LAST_STACK, now);
                int stacks = player.getPersistentData().getInt(PKEY_BRIMSTONE_STACKS) + 1;
                player.getPersistentData().putInt(PKEY_BRIMSTONE_STACKS, stacks);
                applyBrimstoneAttackModifier(player);
                LivingEntity target = event.getEntity();
                applyBrimstoneTargetAttackModifier(target);
            }
            player.getPersistentData().putLong(PKEY_BRIMSTONE_LAST_ATTACK, now);
        }

        // ---- Bone Flute: absorption per melee hit ----
        var boneFlute = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.BONE_FLUTE.get()));
        if (boneFlute.isPresent()) {
            double increment = Config.BONE_FLUTE_HEALTH_PER_HIT;
            double current = player.getPersistentData().getDouble(PKEY_BONE_FLUTE_HP_ACCUM);
            player.getPersistentData().putDouble(PKEY_BONE_FLUTE_HP_ACCUM, current + increment);
            player.setAbsorptionAmount(player.getAbsorptionAmount() + (float) increment);
            // Bone Flute: also gain +2 block per melee hit
            int block = player.getPersistentData().getInt(PKEY_GALACTIC_DUST_BLOCK);
            player.getPersistentData().putInt(PKEY_GALACTIC_DUST_BLOCK, block + (int) Config.BONE_FLUTE_BLOCK_PER_HIT);
        }

        // ---- Funerary Mask: same-target attack speed ----
        var funeraryMask = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.FUNERARY_MASK.get()));
        if (funeraryMask.isPresent()) {
            LivingEntity target = event.getEntity();
            long now = player.level().getGameTime();
            long lastHitTime = player.getPersistentData().getLong(PKEY_FUNERARY_MASK_LAST_HIT_TIME);
            int lastTarget = player.getPersistentData().getInt(PKEY_FUNERARY_MASK_LAST_TARGET);

            if (lastTarget == target.getId() && (now - lastHitTime) <= 60) {
                applyFuneraryMaskAttackSpeed(player);
            } else {
                removeFuneraryMaskAttackSpeed(player);
            }
            player.getPersistentData().putInt(PKEY_FUNERARY_MASK_LAST_TARGET, target.getId());
            player.getPersistentData().putLong(PKEY_FUNERARY_MASK_LAST_HIT_TIME, now);
        }

        // ---- Symbiotic Virus: sonic boom on melee hit ----
        var symbioticVirus = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.SYMBIOTIC_VIRUS.get()));
        if (symbioticVirus.isPresent()) {
            long now = player.level().getGameTime();
            long cooldownTicks = getCoreCooldownTicks(player);
            if (now - player.getPersistentData().getLong(PKEY_SYMBIOTIC_VIRUS_COOLDOWN) >= cooldownTicks) {
                player.getPersistentData().putLong(PKEY_SYMBIOTIC_VIRUS_COOLDOWN, now);
                performVirusSonicBoom(event.getEntity(), player);
            }
        }

        // ---- Projectile damage modifier: Ring of the Snake / Ring of the Drake ----
        // Bouncing for AbstractArrow is handled in onProjectileImpact
        if (event.getSource().getDirectEntity() instanceof Projectile projectile) {

            boolean hasSnake = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                    handler.findFirstCurio(stack -> stack.getItem() == ModItems.RING_OF_THE_SNAKE.get())).isPresent();
            boolean hasDrake = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                    handler.findFirstCurio(stack -> stack.getItem() == ModItems.RING_OF_THE_DRAKE.get())).isPresent();

            if (hasSnake || hasDrake) {
                int bounceCount = projectile.getPersistentData().getInt(PKEY_BOUNCE_COUNT);
                double retention = hasDrake ? 0.75 : 0.5;

                double multiplier = Math.pow(retention, bounceCount);
                event.setAmount((float) (event.getAmount() * multiplier));

                if (hasDrake) {
                    LivingEntity target = event.getEntity();
                    var existing = target.getEffect(ModEffects.WEAK_POWER.get());
                    int newDuration = (existing != null) ? existing.getDuration() + 3 * 20 : 3 * 20;
                    target.addEffect(new MobEffectInstance(ModEffects.WEAK_POWER.get(), newDuration, 0,
                            false, false, true));
                }
            }
        }

        // ---- Music Box: consume charge → mark target for double-hit ----
        if (player.getPersistentData().getBoolean(PKEY_MUSIC_BOX_CHARGE)) {
            player.getPersistentData().putBoolean(PKEY_MUSIC_BOX_CHARGE, false);
            LivingEntity target = event.getEntity();
            target.getPersistentData().putBoolean(PKEY_MUSIC_BOX_DOUBLE_HIT, true);
            target.getPersistentData().putFloat("SayukiMusicBoxDmg", event.getAmount());
            target.getPersistentData().putString("SayukiMusicBoxOwner", player.getStringUUID());
        }
    }

    // === ProjectileImpact: Ring of the Snake / Ring of the Drake — redirect AbstractArrow ===

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (!(event.getRayTraceResult() instanceof EntityHitResult ehr)) return;
        if (!(ehr.getEntity() instanceof LivingEntity target)) return;
        if (!(target instanceof Enemy)) return;

        Projectile projectile = event.getProjectile();
        if (!(projectile.getOwner() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (!(projectile instanceof AbstractArrow arrow)) return;

        // Helical Dart: track projectile hits independently of rings
        if (player.getPersistentData().getBoolean(PKEY_HELICAL_DART_EQUIPPED)) {
            int hits = player.getPersistentData().getInt(PKEY_HELICAL_DART_BOUNCES) + 1;
            if (hits >= 3) {
                player.getPersistentData().putInt(PKEY_HELICAL_DART_BOUNCES, 0);
                player.getPersistentData().putLong(PKEY_HELICAL_DART_BUFF_END,
                        player.level().getGameTime() + Config.helicalDartBuffSeconds * 20L);
            } else {
                player.getPersistentData().putInt(PKEY_HELICAL_DART_BOUNCES, hits);
            }
        }

        boolean hasSnake = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.RING_OF_THE_SNAKE.get())).isPresent();
        boolean hasDrake = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.RING_OF_THE_DRAKE.get())).isPresent();
        if (!hasSnake && !hasDrake) return;

        int bounceCount = projectile.getPersistentData().getInt(PKEY_BOUNCE_COUNT);
        int maxBounces = hasDrake ? 6 : 2;
        double retention = hasDrake ? 0.75 : 0.5;

        event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);

        float velocity = (float) arrow.getDeltaMovement().length();
        int rawDamage = (int) Math.ceil(Math.max(Math.min((double) velocity * arrow.getBaseDamage(), 2.147483647E9D), 0.0D));
        if (arrow.isCritArrow()) {
            rawDamage += arrow.level().random.nextInt(rawDamage / 2 + 2);
        }
        double multiplier = Math.pow(retention, bounceCount);
        float finalDamage = (float) (rawDamage * multiplier);

        Entity owner = arrow.getOwner();
        DamageSource source = arrow.level().damageSources().arrow(arrow, owner != null ? owner : arrow);

        target.hurt(source, finalDamage);

        if (hasDrake) {
            var existing = target.getEffect(ModEffects.WEAK_POWER.get());
            int newDuration = (existing != null) ? existing.getDuration() + 3 * 20 : 3 * 20;
            target.addEffect(new MobEffectInstance(ModEffects.WEAK_POWER.get(), newDuration, 0,
                    false, false, true));
        }

        if (bounceCount < maxBounces) {
            Vec3 hitPos = target.position();
            float searchRadius = (float) Config.ringSearchRadius;
            AABB searchBox = new AABB(hitPos.subtract(searchRadius, searchRadius, searchRadius), 
                    hitPos.add(searchRadius, searchRadius, searchRadius));

            LivingEntity nextTarget = target.level().getEntitiesOfClass(Mob.class, searchBox,
                    mob -> mob instanceof Enemy && mob != target && mob.isAlive())
                    .stream()
                    .min((a, b) -> Double.compare(
                            a.distanceToSqr(target),
                            b.distanceToSqr(target)))
                    .orElse(null);

            if (nextTarget != null) {
                Vec3 startPos = target.getEyePosition().add(0, -0.3, 0);
                arrow.setPos(startPos.x, startPos.y, startPos.z);
                Vec3 dir = nextTarget.getEyePosition().subtract(arrow.position()).normalize();
                arrow.setDeltaMovement(dir.scale(1.5));
                arrow.getPersistentData().putInt(PKEY_BOUNCE_COUNT, bounceCount + 1);
                arrow.hasImpulse = true;
            }
        }
    }

    // === LivingHurt: Galactic Dust — reduce damage by accumulated block ===

    @SubscribeEvent
    public static void onLivingHurtGalacticDust(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        int block = player.getPersistentData().getInt(PKEY_GALACTIC_DUST_BLOCK);
        if (block <= 0) return;
        float amount = event.getAmount();
        if (amount <= block) {
            player.getPersistentData().putInt(PKEY_GALACTIC_DUST_BLOCK, block - (int) Math.ceil(amount));
            event.setCanceled(true);
        } else {
            player.getPersistentData().putInt(PKEY_GALACTIC_DUST_BLOCK, 0);
            event.setAmount(amount - block);
        }
    }

    // === LivingHurt: Paper Krane — -40% from WEAK_POWER attackers, -25% from vanilla Weakness ===

    @SubscribeEvent
    public static void onLivingHurtPaperKrane(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity le ? le : null;
        if (attacker == null) return;

        boolean hasKrane = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.PAPER_KRANE.get())).isPresent();
        if (!hasKrane) return;

        if (attacker.hasEffect(ModEffects.WEAK_POWER.get())) {
            event.setAmount(event.getAmount() * 0.6F);
        } else if (attacker.hasEffect(net.minecraft.world.effect.MobEffects.WEAKNESS)) {
            event.setAmount(event.getAmount() * 0.75F);
        }
    }

    // === LivingDamage: Self-Forming Clay — gain block when actually losing health ===

    @SubscribeEvent
    public static void onLivingDamageSelfFormingClay(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        var clay = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.SELF_FORMING_CLAY.get()));
        if (clay.isEmpty()) return;
        long now = player.level().getGameTime();
        long lastTrigger = player.getPersistentData().getLong(PKEY_SELF_FORMING_CLAY_COOLDOWN);
        if (now - lastTrigger < Config.selfFormingClayCooldownTicks) return;
        player.getPersistentData().putLong(PKEY_SELF_FORMING_CLAY_COOLDOWN, now);
        int current = player.getPersistentData().getInt(PKEY_GALACTIC_DUST_BLOCK);
        player.getPersistentData().putInt(PKEY_GALACTIC_DUST_BLOCK, current + (int) Config.SELF_FORMING_CLAY_BLOCK_PER_HIT);
    }

    // === LivingDamage: Demon Tongue — heal back the damage amount ===

    @SubscribeEvent
    public static void onLivingDamageDemonTongue(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        var tongue = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.DEMON_TONGUE.get()));
        if (tongue.isEmpty()) return;
        long now = player.level().getGameTime();
        long lastTrigger = player.getPersistentData().getLong(PKEY_DEMON_TONGUE_COOLDOWN);
        if (now - lastTrigger < Config.demonTongueCooldownTicks) return;
        player.getPersistentData().putLong(PKEY_DEMON_TONGUE_COOLDOWN, now);
        player.heal(event.getAmount());
    }

    // === PlayerDestroyItem: Charon's Ashes — durability break / totem consumption ===

    @SubscribeEvent
    public static void onPlayerDestroyItemCharonsAshes(PlayerDestroyItemEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        var ashes = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.CHARONS_ASHES.get()));
        if (ashes.isEmpty()) return;
        int stacks = player.getPersistentData().getInt(PKEY_CHARONS_ASHES_STACKS);
        player.getPersistentData().putInt(PKEY_CHARONS_ASHES_STACKS, stacks + 1);
    }

    // === LivingHurt: Brimstone target — count attacks, remove modifier after limit ===

    @SubscribeEvent
    public static void onLivingHurtBrimstoneTarget(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;
        if (attacker.level().isClientSide()) return;
        if (attacker.getAttribute(Attributes.ATTACK_DAMAGE) == null) return;
        if (attacker.getAttribute(Attributes.ATTACK_DAMAGE).getModifier(BRIMSTONE_TARGET_TAG) == null) return;
        var data = attacker.getPersistentData();
        int attacks = data.getInt(PKEY_BRIMSTONE_TARGET_ATTACKS) + 1;
        if (attacks >= Config.BRIMSTONE_TARGET_MAX_ATTACKS) {
            attacker.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(BRIMSTONE_TARGET_TAG);
            data.remove(PKEY_BRIMSTONE_TARGET_ATK);
            data.remove(PKEY_BRIMSTONE_TARGET_ATTACKS);
        } else {
            data.putInt(PKEY_BRIMSTONE_TARGET_ATTACKS, attacks);
        }
    }

    // === LivingEntityUseItemEvent.Finish: Charon's Ashes — food/potion usage ===

    @SubscribeEvent
    public static void onItemUseFinishCharonsAshes(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        var ashes = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.CHARONS_ASHES.get()));
        if (ashes.isEmpty()) return;
        int stacks = player.getPersistentData().getInt(PKEY_CHARONS_ASHES_STACKS);
        player.getPersistentData().putInt(PKEY_CHARONS_ASHES_STACKS, stacks + 1);
    }

    // === LivingHurt: Twisted Funnel — apply poison_power on projectile hit ===

    @SubscribeEvent
    public static void onLivingHurtTwistedFunnel(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Enemy)) return;
        if (!(event.getSource().getDirectEntity() instanceof Projectile projectile)) return;
        if (!(projectile.getOwner() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        var funnel = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.TWISTED_FUNNEL.get()));
        if (funnel.isEmpty()) return;

        boolean hasSnecko = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.SNECKO_SKULL.get())).isPresent();

        int stacksToAdd = hasSnecko ? 5 : 4;
        LivingEntity target = event.getEntity();
        int existingAmplifier = -1;
        var existing = target.getEffect(ModEffects.POISON_POWER.get());
        if (existing != null) {
            existingAmplifier = existing.getAmplifier();
        }

        int totalLayers = (existingAmplifier >= 0 ? existingAmplifier + 1 : 0) + stacksToAdd;
        int newAmplifier = totalLayers - 2;

        target.hurt(target.damageSources().magic(), totalLayers);

        if (newAmplifier >= 0) {
            target.addEffect(new MobEffectInstance(ModEffects.POISON_POWER.get(), -1, newAmplifier,
                    false, false, true));
        } else {
            target.removeEffect(ModEffects.POISON_POWER.get());
        }
    }

    // === LivingHurt: Tingsha — +3 damage on projectile hit ===

    @SubscribeEvent
    public static void onLivingHurtTingsha(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Enemy)) return;
        if (!(event.getSource().getDirectEntity() instanceof Projectile projectile)) return;
        if (!(projectile.getOwner() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        boolean hasTingsha = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.TINGSHA.get())).isPresent();
        if (!hasTingsha) return;

        event.setAmount(event.getAmount() + 3);
    }

    // === LivingHurt: Tough Bandages — +3 block on projectile hit ===

    @SubscribeEvent
    public static void onLivingHurtToughBandages(LivingHurtEvent event) {
        if (!(event.getSource().getDirectEntity() instanceof Projectile projectile)) return;
        if (!(projectile.getOwner() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        boolean hasToughBandages = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.TOUGH_BANDAGES.get())).isPresent();
        if (!hasToughBandages) return;

        int block = player.getPersistentData().getInt(PKEY_GALACTIC_DUST_BLOCK) + 3;
        long now = player.level().getGameTime();
        if (now < player.getPersistentData().getLong(PKEY_HELICAL_DART_BUFF_END)) {
            block++;
        }
        player.getPersistentData().putInt(PKEY_GALACTIC_DUST_BLOCK, block);
    }

    // === LivingHurt: Cracked Core — summons lightning bolt on target (shared cooldown) ===

    @SubscribeEvent
    public static void onLivingHurtCrackedCore(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        var crackedCore = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.CRACKED_CORE.get()));
        if (crackedCore.isEmpty()) return;

        // Configurable cooldown (shared with Infused Core / Emotion Chip), reduced 20% by Power Cell
        long cooldownTicks = getCoreCooldownTicks(player);
        long now = player.level().getGameTime();
        if (now - player.getPersistentData().getLong(PKEY_CRACKED_CORE_COOLDOWN) < cooldownTicks) return;
        player.getPersistentData().putLong(PKEY_CRACKED_CORE_COOLDOWN, now);

        summonLightningOnTarget(event.getEntity(), player);
    }

    // === LivingHurt: Infused Core — summons area lightning bolts around self (shared cooldown) ===

    @SubscribeEvent
    public static void onLivingHurtInfusedCore(LivingHurtEvent event) {
        if (applyingCoreLightning) return;
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        var infusedCore = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.INFUSED_CORE.get()));
        if (infusedCore.isEmpty()) return;

        long cooldownTicks = getCoreCooldownTicks(player);
        long now = player.level().getGameTime();
        if (now - player.getPersistentData().getLong(PKEY_CRACKED_CORE_COOLDOWN) < cooldownTicks) return;
        player.getPersistentData().putLong(PKEY_CRACKED_CORE_COOLDOWN, now);

        summonAreaLightningBolts(player, player);
    }

    // === LivingHurt: Emotion Chip — when hurt, triggers Cracked/Infused Core effect ===

    @SubscribeEvent
    public static void onLivingHurtEmotionChip(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        var emotionChip = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.EMOTION_CHIP.get()));
        if (emotionChip.isEmpty()) return;

        // Shared cooldown with Cracked/Infused Core, reduced 20% by Power Cell
        long cooldownTicks = getCoreCooldownTicks(player);
        long now = player.level().getGameTime();

        // Symbiotic Virus + Emotion Chip: delayed sonic boom on attacker (independent of core)
        var symbioticVirus = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.SYMBIOTIC_VIRUS.get()));
        if (symbioticVirus.isPresent() && event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (now - player.getPersistentData().getLong(PKEY_SYMBIOTIC_VIRUS_COOLDOWN) >= cooldownTicks) {
                player.getPersistentData().putLong(PKEY_SYMBIOTIC_VIRUS_COOLDOWN, now);
                performVirusSonicBoom(attacker, player);
            }
        }

        // Check which core is equipped (mutually exclusive, but check both)
        var crackedCore = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.CRACKED_CORE.get()));
        var infusedCore = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.INFUSED_CORE.get()));

        if (crackedCore.isEmpty() && infusedCore.isEmpty()) return;

        if (now - player.getPersistentData().getLong(PKEY_CRACKED_CORE_COOLDOWN) < cooldownTicks) return;
        player.getPersistentData().putLong(PKEY_CRACKED_CORE_COOLDOWN, now);

        if (crackedCore.isPresent()) {
            // Cracked Core: lightning on attacker
            if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                summonLightningOnTarget(attacker, player);
            }
        } else {
            // Infused Core: area lightning bolts around player
            summonAreaLightningBolts(player, player);
        }
    }

    // === LivingAttackEvent: Infused Core — lightning immunity ===

    @SubscribeEvent
    public static void onLivingAttackInfusedCore(net.minecraftforge.event.entity.living.LivingAttackEvent event) {
        // Defense: wearer immune to lightning
        if (event.getEntity() instanceof Player player) {
            var handler = CuriosApi.getCuriosInventory(player).resolve();
            if (handler.isPresent()) {
                var infused = handler.get().findFirstCurio(
                        stack -> stack.getItem() == ModItems.INFUSED_CORE.get());
                if (infused.isPresent() && event.getSource().is(DamageTypeTags.IS_LIGHTNING)) {
                    event.setCanceled(true);
                }
            }
        }
    }

    // === LivingHurt: Heaven Earring (wearer is attacked) ===

    @SubscribeEvent
    public static void onLivingHurtHeavenEarOrnaments(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (player.hasEffect(ModEffects.SILENCE.get())) return;

        boolean hasHeavenEarOrnaments = CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(handler -> handler.findFirstCurio(
                        stack -> stack.getItem() == ModItems.HEAVEN_EAR_ORNAMENTS.get()))
                .isPresent();
        if (!hasHeavenEarOrnaments) return;

        if (event.getSource().getEntity() instanceof Mob attacker) {
            attacker.setNoAi(true);
            attacker.addEffect(new MobEffectInstance(ModEffects.HEAVEN_DOOR.get(),
                    60, 0, false, false, true)); // 3s
            player.addEffect(new MobEffectInstance(ModEffects.SILENCE.get(),
                    1200, 0, false, false, true)); // 60s cooldown
        }
    }

    // === CurioChange ===

    @SubscribeEvent
    public static void onCurioChange(CurioChangeEvent event) {
        LivingEntity entity = event.getEntity();
        ItemStack from = event.getFrom();
        ItemStack to = event.getTo();

        if (from.getItem() == ModItems.CHERRY_EAR_ORNAMENTS.get()) {
            removeReachModifier(entity, CHERRY_EAR_ORNAMENTS_REACH_UUID);
        }
        if (to.getItem() == ModItems.CHERRY_EAR_ORNAMENTS.get()) {
            applyReachModifier(entity, CHERRY_EAR_ORNAMENTS_REACH_UUID, 2.0);
        }

        if (from.getItem() == ModItems.HEART_EAR_ORNAMENTS.get()) {
            removeMaxHealthModifier(entity, HEART_EAR_ORNAMENTS_MAX_HEALTH_UUID);
            removeAttackSpeedModifier(entity, HEART_EAR_ORNAMENTS_ATTACK_SPEED_UUID);
        }
        if (to.getItem() == ModItems.HEART_EAR_ORNAMENTS.get()) {
            applyMaxHealthModifier(entity, HEART_EAR_ORNAMENTS_MAX_HEALTH_UUID, 10.0);
            applyAttackSpeedModifier(entity, HEART_EAR_ORNAMENTS_ATTACK_SPEED_UUID, 0.5);
        }

        if (from.getItem() == ModItems.VOODOO_RING.get()) {
            removeAllAttributeBoost(entity, VOODOO_NAMESPACE_UUID);
        }
        if (to.getItem() == ModItems.VOODOO_RING.get()) {
            applyAllAttributeBoost(entity, VOODOO_NAMESPACE_UUID, 0.05);
        }

        if (from.getItem() == ModItems.WHISPERING_EARRING.get()) {
            entity.removeEffect(ModEffects.COMBAT.get());
            entity.removeEffect(ModEffects.WHISPER.get());
            removeWhisperingAttackModifier(entity);
            clearWhisperingData(entity);
        }
        if (to.getItem() == ModItems.WHISPERING_EARRING.get()) {
            var data = entity.getPersistentData();
            data.putBoolean(PKEY_WHISPERING_EQUIPPED, true);
            data.putLong(PKEY_WHISPERING_WINDOW_START, 0);
            data.putDouble(PKEY_WHISPERING_WINDOW_DMG, 0);
            if (entity instanceof Player player) {
                double weaponDmg = getMainHandWeaponAttackDamage(player);
                if (weaponDmg > 0) {
                    applyWhisperingAttackModifier(entity, weaponDmg);
                }
                data.putDouble(PKEY_WHISPERING_WEAPON_DMG, weaponDmg);
            }
            // Start combat buff on equip
            entity.addEffect(new MobEffectInstance(ModEffects.COMBAT.get(),
                    Config.whisperingIdleSeconds * 20, 0, false, false, true));
        }

        if (from.getItem() == ModItems.BOUND_PHYLACTERY.get()) {
            entity.getPersistentData().remove(PKEY_BOUND_HP_ACCUM);
        }
        if (to.getItem() == ModItems.BOUND_PHYLACTERY.get()) {
            entity.getPersistentData().putDouble(PKEY_BOUND_HP_ACCUM, 0);
        }

        if (from.getItem() == ModItems.PHYLACTERY_UNBOUND.get()) {
            entity.getPersistentData().remove(PKEY_UNBOUND_HP_ACCUM);
        }
        if (to.getItem() == ModItems.PHYLACTERY_UNBOUND.get()) {
            entity.getPersistentData().putDouble(PKEY_UNBOUND_HP_ACCUM, 0);
        }

        if (from.getItem() == ModItems.HEAVEN_EAR_ORNAMENTS.get()) {
            entity.removeEffect(ModEffects.SILENCE.get());
            entity.getPersistentData().remove(PKEY_HEAVEN_EAR_ORNAMENTS);
        }
        if (to.getItem() == ModItems.HEAVEN_EAR_ORNAMENTS.get()) {
            entity.getPersistentData().putBoolean(PKEY_HEAVEN_EAR_ORNAMENTS, true);
        }

        // ---- Divine attribute modifiers ----
        if (from.getItem() == ModItems.DIVINE_RIGHT.get()) {
            removeIronSpellsMaxManaModifier(entity, DIVINE_RIGHT_MAX_MANA_UUID);
            removeAttackSpeedModifier(entity, DIVINE_RIGHT_FORGED_SWORD_AS_UUID);
        }
        if (to.getItem() == ModItems.DIVINE_RIGHT.get() && IronSpellsCompat.isLoaded()) {
            applyIronSpellsMaxManaModifier(entity, DIVINE_RIGHT_MAX_MANA_UUID, Config.divineRightManaFloor);
        }
        if (from.getItem() == ModItems.DIVINE_DESTINY.get()) {
            removeIronSpellsMaxManaModifier(entity, DIVINE_DESTINY_MAX_MANA_UUID);
            removeAttackSpeedModifier(entity, DIVINE_DESTINY_FORGED_SWORD_AS_UUID);
        }
        if (to.getItem() == ModItems.DIVINE_DESTINY.get() && IronSpellsCompat.isLoaded() && entity instanceof Player player) {
            float maxMana = IronSpellsCompat.getMaxMana(player);
            if (maxMana > 0) {
                applyIronSpellsMaxManaModifier(entity, DIVINE_DESTINY_MAX_MANA_UUID,
                        (int) (maxMana * Config.divineDestinyManaRatio));
            }
        }

        // ---- Data Disk: all spell power +0.1 (incl. addons) ----
        if (from.getItem() == ModItems.DATA_DISK.get()) {
            IronSpellsCompat.removeAllSpellPowerBonus(entity, DATA_DISK_ALL_SPELL_POWER_UUID);
        }
        if (to.getItem() == ModItems.DATA_DISK.get() && IronSpellsCompat.isLoaded()) {
            IronSpellsCompat.applyAllSpellPowerBonus(entity, DATA_DISK_ALL_SPELL_POWER_UUID);
        }

        // ---- Symbiotic Virus: no more spell power (sonic boom in LivingHurt) ----

        // ---- Power Cell: %-based max mana ----
        if (from.getItem() == ModItems.POWER_CELL.get()) {
            removeIronSpellsMaxManaModifier(entity, POWER_CELL_MAX_MANA_UUID);
        }
        if (to.getItem() == ModItems.POWER_CELL.get() && IronSpellsCompat.isLoaded() && entity instanceof Player player) {
            float maxMana = IronSpellsCompat.getMaxMana(player);
            if (maxMana > 0) {
                applyIronSpellsMaxManaModifier(entity, POWER_CELL_MAX_MANA_UUID,
                        (int) (maxMana * Config.POWER_CELL_MANA_PERCENT));
            }
        }

        // ---- Bone Flute: persistent HP accumulation ----
        if (from.getItem() == ModItems.BONE_FLUTE.get()) {
            entity.getPersistentData().remove(PKEY_BONE_FLUTE_HP_ACCUM);
        }
        if (to.getItem() == ModItems.BONE_FLUTE.get()) {
            entity.getPersistentData().putDouble(PKEY_BONE_FLUTE_HP_ACCUM, 0);
        }

        // ---- Book Repair Knife: absorption gain tracking ----
        if (from.getItem() == ModItems.BOOK_REPAIR_KNIFE.get()) {
            entity.getPersistentData().remove(PKEY_BOOK_REPAIR_KNIFE_HEAL_ACCUM);
            entity.getPersistentData().remove(PKEY_BOOK_REPAIR_KNIFE_CHARGES);
            entity.getPersistentData().remove(PKEY_BOOK_REPAIR_KNIFE_LAST_ABSORPTION);
        }
        if (to.getItem() == ModItems.BOOK_REPAIR_KNIFE.get()) {
            entity.getPersistentData().putDouble(PKEY_BOOK_REPAIR_KNIFE_HEAL_ACCUM, 0);
            entity.getPersistentData().putInt(PKEY_BOOK_REPAIR_KNIFE_CHARGES, 0);
            entity.getPersistentData().putFloat(PKEY_BOOK_REPAIR_KNIFE_LAST_ABSORPTION, 0);
        }

        // ---- Funerary Mask: tracking data ----
        if (from.getItem() == ModItems.FUNERARY_MASK.get()) {
            entity.getPersistentData().remove(PKEY_FUNERARY_MASK_LAST_TARGET);
            entity.getPersistentData().remove(PKEY_FUNERARY_MASK_LAST_HIT_TIME);
            removeFuneraryMaskAttackSpeed(entity);
        }

        // ---- Bookmark: charges tracking ----
        if (from.getItem() == ModItems.BOOKMARK.get()) {
            entity.getPersistentData().putBoolean(PKEY_BOOKMARK_EQUIPPED, false);
            entity.getPersistentData().remove(PKEY_BOOKMARK_CHARGES);
            entity.getPersistentData().remove(PKEY_BOOKMARK_LAST_SOULS);
        }
        if (to.getItem() == ModItems.BOOKMARK.get()) {
            entity.getPersistentData().putBoolean(PKEY_BOOKMARK_EQUIPPED, true);
            entity.getPersistentData().putInt(PKEY_BOOKMARK_CHARGES, 0);
            entity.getPersistentData().putInt(PKEY_BOOKMARK_LAST_SOULS, -1);
        }

        // ---- Ivory Tile: remainder / last souls ----
        if (from.getItem() == ModItems.IVORY_TILE.get()) {
            entity.getPersistentData().putBoolean(PKEY_IVORY_TILE_EQUIPPED, false);
            entity.getPersistentData().remove(PKEY_IVORY_TILE_REMAINDER);
            entity.getPersistentData().remove(PKEY_IVORY_TILE_LAST_SOULS);
        }
        if (to.getItem() == ModItems.IVORY_TILE.get()) {
            entity.getPersistentData().putBoolean(PKEY_IVORY_TILE_EQUIPPED, true);
            entity.getPersistentData().putDouble(PKEY_IVORY_TILE_REMAINDER, 0);
            entity.getPersistentData().putInt(PKEY_IVORY_TILE_LAST_SOULS, -1);
        }

        // ---- Galactic Dust: remainder / last mana ----
        if (from.getItem() == ModItems.GALACTIC_DUST.get()) {
            entity.getPersistentData().putBoolean(PKEY_GALACTIC_DUST_EQUIPPED, false);
            entity.getPersistentData().remove(PKEY_GALACTIC_DUST_REMAINDER);
            entity.getPersistentData().remove(PKEY_GALACTIC_DUST_LAST_MANA);
            entity.getPersistentData().remove(PKEY_GALACTIC_DUST_BLOCK);
        }
        if (to.getItem() == ModItems.GALACTIC_DUST.get()) {
            entity.getPersistentData().putBoolean(PKEY_GALACTIC_DUST_EQUIPPED, true);
            entity.getPersistentData().putDouble(PKEY_GALACTIC_DUST_REMAINDER, 0);
            entity.getPersistentData().putFloat(PKEY_GALACTIC_DUST_LAST_MANA, -1);
            entity.getPersistentData().putInt(PKEY_GALACTIC_DUST_BLOCK, 0);
        }

        // ---- Red Skull: attack modifier cleanup ----
        if (from.getItem() == ModItems.RED_SKULL.get()) {
            removeRedSkullAttackModifier(entity);
        }

        // ---- Helical Dart: equip / bounce / buff ----
        if (from.getItem() == ModItems.HELICAL_DART.get()) {
            entity.getPersistentData().putBoolean(PKEY_HELICAL_DART_EQUIPPED, false);
            entity.getPersistentData().remove(PKEY_HELICAL_DART_BOUNCES);
            entity.getPersistentData().remove(PKEY_HELICAL_DART_BUFF_END);
        }
        if (to.getItem() == ModItems.HELICAL_DART.get()) {
            entity.getPersistentData().putBoolean(PKEY_HELICAL_DART_EQUIPPED, true);
            entity.getPersistentData().putInt(PKEY_HELICAL_DART_BOUNCES, 0);
        }

        // ---- Lunar Pastry: equip state ----
        if (from.getItem() == ModItems.LUNAR_PASTRY.get()) {
            entity.getPersistentData().putBoolean(PKEY_LUNAR_PASTRY_EQUIPPED, false);
            entity.getPersistentData().remove(PKEY_LUNAR_PASTRY_LAST_MANA);
        }
        if (to.getItem() == ModItems.LUNAR_PASTRY.get()) {
            entity.getPersistentData().putBoolean(PKEY_LUNAR_PASTRY_EQUIPPED, true);
            entity.getPersistentData().putFloat(PKEY_LUNAR_PASTRY_LAST_MANA, -1);
        }

        // ---- Music Box: playback state / charge ----
        if (from.getItem() == ModItems.MUSIC_BOX.get()) {
            entity.getPersistentData().remove(PKEY_MUSIC_BOX_PLAYING);
            entity.getPersistentData().remove(PKEY_MUSIC_BOX_TRIGGER_TICK);
            entity.getPersistentData().remove(PKEY_MUSIC_BOX_CHARGE);
        }
        if (to.getItem() == ModItems.MUSIC_BOX.get()) {
            entity.getPersistentData().putBoolean(PKEY_MUSIC_BOX_PLAYING, false);
            entity.getPersistentData().putLong(PKEY_MUSIC_BOX_TRIGGER_TICK, 0);
            entity.getPersistentData().putBoolean(PKEY_MUSIC_BOX_CHARGE, false);
        }

        // ---- Distinguished Cape: -9 max health attribute + damage cap charges ----
        if (from.getItem() == ModItems.DISTINGUISHED_CAPE.get()) {
            removeMaxHealthModifier(entity, DISTINGUISHED_CAPE_MAX_HEALTH_UUID);
            entity.getPersistentData().remove(PKEY_DISTINGUISHED_CAPE_CHARGES);
            entity.getPersistentData().remove(PKEY_DISTINGUISHED_CAPE_COOLDOWN);
        }
        if (to.getItem() == ModItems.DISTINGUISHED_CAPE.get()) {
            applyMaxHealthModifier(entity, DISTINGUISHED_CAPE_MAX_HEALTH_UUID, -9.0);
            entity.getPersistentData().putInt(PKEY_DISTINGUISHED_CAPE_CHARGES, 0);
            entity.getPersistentData().putLong(PKEY_DISTINGUISHED_CAPE_COOLDOWN, 0);
        }

        // ---- Fiddle: +2 attack speed, strip all other AS modifiers ----
        if (from.getItem() == ModItems.FIDDLE.get()) {
            removeAttackSpeedModifier(entity, FIDDLE_ATTACK_SPEED_UUID);
            entity.getPersistentData().putBoolean(PKEY_FIDDLE_EQUIPPED, false);
        }
        if (to.getItem() == ModItems.FIDDLE.get()) {
            applyAttackSpeedModifier(entity, FIDDLE_ATTACK_SPEED_UUID, Config.fiddleAttackSpeed);
            entity.getPersistentData().putBoolean(PKEY_FIDDLE_EQUIPPED, true);
        }

        // ---- Blood-Soaked Rose: -50% attack speed, +weapon base damage ----
        if (from.getItem() == ModItems.BLOOD_SOAKED_ROSE.get()) {
            removeAttackSpeedModifier(entity, BLOOD_SOAKED_ROSE_ATTACK_SPEED_UUID);
            removeAttackDamageModifier(entity, BLOOD_SOAKED_ROSE_ATTACK_UUID);
            clearBloodSoakedRoseData(entity);
        }
        if (to.getItem() == ModItems.BLOOD_SOAKED_ROSE.get()) {
            var data = entity.getPersistentData();
            data.putBoolean(PKEY_BLOOD_SOAKED_ROSE_EQUIPPED, true);
            applyAttackSpeedModifier(entity, BLOOD_SOAKED_ROSE_ATTACK_SPEED_UUID, -0.5);
            if (entity instanceof Player player) {
                double weaponDmg = getMainHandWeaponAttackDamage(player);
                if (weaponDmg > 0) {
                    applyBloodSoakedRoseAttackModifier(entity, weaponDmg);
                }
                data.putDouble(PKEY_BLOOD_SOAKED_ROSE_WEAPON_DMG, weaponDmg);
            }
        }

        // ---- Jeweled Mask: random positive buff cycle ----
        if (from.getItem() == ModItems.JEWELED_MASK.get()) {
            // Remove current buff before clearing data
            String effectId = entity.getPersistentData().getString(PKEY_JEWELED_MASK_CURRENT_EFFECT);
            if (!effectId.isEmpty()) {
                var effect = ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.tryParse(effectId));
                if (effect != null) {
                    entity.removeEffect(effect);
                }
            }
            entity.getPersistentData().remove(PKEY_JEWELED_MASK_ACTIVE);
            entity.getPersistentData().remove(PKEY_JEWELED_MASK_NEXT_TRIGGER);
            entity.getPersistentData().remove(PKEY_JEWELED_MASK_CURRENT_EFFECT);
        }
        if (to.getItem() == ModItems.JEWELED_MASK.get()) {
            entity.getPersistentData().putBoolean(PKEY_JEWELED_MASK_ACTIVE, true);
            entity.getPersistentData().putLong(PKEY_JEWELED_MASK_NEXT_TRIGGER, entity.level().getGameTime());
            entity.getPersistentData().putString(PKEY_JEWELED_MASK_CURRENT_EFFECT, "");
        }

        // ---- Sere Talon: 2 random debuffs + 3 random buffs, permanent while equipped ----
        if (from.getItem() == ModItems.SERE_TALON.get()) {
            clearSereTalonEffects(entity);
        }
        if (to.getItem() == ModItems.SERE_TALON.get()) {
            entity.getPersistentData().putBoolean(PKEY_SERE_TALON_EQUIPPED, true);
            rollAndApplySereTalonEffects(entity);
        }

        // ---- Preserved Fog: attack speed reduction ----
        if (from.getItem() == ModItems.PRESERVED_FOG.get()) {
            removeAttackSpeedModifier(entity, PRESERVED_FOG_ATTACK_SPEED_UUID);
        }
        if (to.getItem() == ModItems.PRESERVED_FOG.get()) {
            applyAttackSpeedModifier(entity, PRESERVED_FOG_ATTACK_SPEED_UUID, Config.preservedFogAttackSpeed);
        }

        // ---- Mini Regent: equip state / accumulated ATK ----
        if (from.getItem() == ModItems.MINI_REGENT.get()) {
            entity.getPersistentData().putBoolean(PKEY_MINI_REGENT_EQUIPPED, false);
            entity.getPersistentData().remove(PKEY_MINI_REGENT_ATTACK_BONUS);
            entity.getPersistentData().remove(PKEY_MINI_REGENT_LAST_CAST);
            entity.getPersistentData().remove(PKEY_MINI_REGENT_LAST_MANA);
            entity.getPersistentData().remove(PKEY_MINI_REGENT_LAST_ATTACK);
            removeMiniRegentAttackModifier(entity);
        }
        if (to.getItem() == ModItems.MINI_REGENT.get()) {
            entity.getPersistentData().putBoolean(PKEY_MINI_REGENT_EQUIPPED, true);
            entity.getPersistentData().putInt(PKEY_MINI_REGENT_ATTACK_BONUS, 0);
            entity.getPersistentData().putLong(PKEY_MINI_REGENT_LAST_CAST, 0);
            entity.getPersistentData().putFloat(PKEY_MINI_REGENT_LAST_MANA, -1);
            entity.getPersistentData().putLong(PKEY_MINI_REGENT_LAST_ATTACK, entity.level().getGameTime());
        }

        // ---- Vitruvian Minion: equip state ----
        if (from.getItem() == ModItems.VITRUVIAN_MINION.get()) {
            entity.getPersistentData().putBoolean(PKEY_VITRUVIAN_MINION_EQUIPPED, false);
        }
        if (to.getItem() == ModItems.VITRUVIAN_MINION.get()) {
            entity.getPersistentData().putBoolean(PKEY_VITRUVIAN_MINION_EQUIPPED, true);
        }

        // ---- Self-Forming Clay: cooldown cleanup ----
        if (from.getItem() == ModItems.SELF_FORMING_CLAY.get()) {
            entity.getPersistentData().remove(PKEY_SELF_FORMING_CLAY_COOLDOWN);
        }

        // ---- Charon's Ashes: stacks cleanup ----
        if (from.getItem() == ModItems.CHARONS_ASHES.get()) {
            entity.getPersistentData().remove(PKEY_CHARONS_ASHES_STACKS);
            entity.getPersistentData().remove(PKEY_CHARONS_ASHES_LAST_ITEM);
            entity.getPersistentData().remove(PKEY_CHARONS_ASHES_LAST_COUNT);
        }

        // ---- Demon Tongue: cooldown cleanup ----
        if (from.getItem() == ModItems.DEMON_TONGUE.get()) {
            entity.getPersistentData().remove(PKEY_DEMON_TONGUE_COOLDOWN);
        }

        // ---- Ruined Helmet: modifier cleanup ----
        if (from.getItem() == ModItems.RUINED_HELMET.get()) {
            removeRuinedHelmetModifier(entity);
        }

        // ---- Brimstone: stacks / modifier cleanup ----
        if (from.getItem() == ModItems.BRIMSTONE.get()) {
            entity.getPersistentData().remove(PKEY_BRIMSTONE_STACKS);
            entity.getPersistentData().remove(PKEY_BRIMSTONE_LAST_STACK);
            entity.getPersistentData().remove(PKEY_BRIMSTONE_LAST_ATTACK);
            removeBrimstoneAttackModifier(entity);
        }

        // ---- Fiddle: strip any non-fiddle AS modifiers on any curio change ----
        if (entity.getPersistentData().getBoolean(PKEY_FIDDLE_EQUIPPED)) {
            stripNonFiddleAttackSpeed(entity);
        }
    }

    // === MobEffectEvent.Expired: combat → whisper, heaven door → restore AI ===

    @SubscribeEvent
    public static void onMobEffectExpired(MobEffectEvent.Expired event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;

        // Heaven Door expired — restore AI
        if (event.getEffectInstance() != null
                && event.getEffectInstance().getEffect() == ModEffects.HEAVEN_DOOR.get()) {
            if (entity instanceof Mob mob) {
                mob.setNoAi(false);
            }
            return;
        }

        // Combat → Whisper (whispering earring)
        if (!entity.getPersistentData().getBoolean(PKEY_WHISPERING_EQUIPPED)) return;

        if (event.getEffectInstance() != null
                && event.getEffectInstance().getEffect() == ModEffects.COMBAT.get()) {
            // Combat expired → apply Whisper (infinite)
            entity.addEffect(new MobEffectInstance(ModEffects.WHISPER.get(),
                    -1, 0, false, false, true));
        }
    }

    // === PlayerTick: Goety soul regen + Phylactery health growth ===

    private static int goetySoulRegenTick = 0;
    private static int phylacteryHpTick = 0;
    private static int burningBloodTick = 0;
    private static int blackBloodTick = 0;
    private static int divineDestinyManaTick = 0;
    private static int funeraryMaskSoulTick = 0;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide()) return;

        // Check which phylactery is equipped
        var bound = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.BOUND_PHYLACTERY.get()));
        var unbound = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.PHYLACTERY_UNBOUND.get()));

        // If both somehow equipped, neither provides effects
        if (bound.isPresent() && unbound.isPresent()) {
            goetySoulRegenTick = 0;
            phylacteryHpTick = 0;
            return;
        }

        // ---- Goety soul regen ----
        if (bound.isPresent() || unbound.isPresent()) {
            if (GoetyCompat.canStoreSoulEnergy(player)) {
                goetySoulRegenTick++;
                if (goetySoulRegenTick >= 20) {
                    goetySoulRegenTick = 0;
                    if (bound.isPresent()) {
                        GoetyCompat.addSoulEnergy(player, 1);
                    } else {
                        GoetyCompat.addSoulEnergyPercent(player, 0.02);
                    }
                }
            } else {
                goetySoulRegenTick = 0;
            }
        } else {
            goetySoulRegenTick = 0;
        }

        // ---- Absorption growth ----
        if (bound.isPresent() || unbound.isPresent()) {
            int intervalTicks = (bound.isPresent() ? Config.boundPhylacteryHealthGainIntervalSeconds : Config.unboundPhylacteryHealthGainIntervalSeconds) * 20;
            phylacteryHpTick++;
            if (phylacteryHpTick >= intervalTicks) {
                phylacteryHpTick = 0;
                if (bound.isPresent()) {
                    growPhylacteryAbsorption(player, PKEY_BOUND_HP_ACCUM, 1.0F);
                } else {
                    growPhylacteryAbsorption(player, PKEY_UNBOUND_HP_ACCUM, 2.0F);
                }
            }
        } else {
            phylacteryHpTick = 0;
        }

        // ---- Burning Blood ----
        var burningBlood = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.BURNING_BLOOD.get()));
        if (burningBlood.isPresent()) {
            burningBloodTick++;
            if (burningBloodTick >= 120) {
                burningBloodTick = 0;
                if (player.getHealth() < player.getMaxHealth()) {
                    player.heal(6.0F);
                }
            }
        } else {
            burningBloodTick = 0;
        }

        // ---- Black Blood ----
        var blackBlood = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.BLACK_BLOOD.get()));
        if (blackBlood.isPresent()) {
            blackBloodTick++;
            if (blackBloodTick >= 120) {
                blackBloodTick = 0;
                if (player.getHealth() < player.getMaxHealth()) {
                    player.heal(12.0F);
                    player.getPersistentData().putBoolean(PKEY_BLACK_BLOOD_EMPOWERED, true);
                }
            }
        } else {
            blackBloodTick = 0;
        }

        // ---- Charon's Ashes: track main-hand item count decrease for projectile consumption ----
        var ashes = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.CHARONS_ASHES.get()));
        if (ashes.isPresent()) {
            var mainHand = player.getMainHandItem();
            String currentItem = mainHand.isEmpty() ? "" : ForgeRegistries.ITEMS.getKey(mainHand.getItem()).toString();
            int currentCount = mainHand.getCount();
            String lastItem = player.getPersistentData().getString(PKEY_CHARONS_ASHES_LAST_ITEM);
            int lastCount = player.getPersistentData().getInt(PKEY_CHARONS_ASHES_LAST_COUNT);
            if (!lastItem.isEmpty() && lastItem.equals(currentItem) && currentCount < lastCount) {
                int diff = lastCount - currentCount;
                int stacks = player.getPersistentData().getInt(PKEY_CHARONS_ASHES_STACKS);
                player.getPersistentData().putInt(PKEY_CHARONS_ASHES_STACKS, stacks + diff);
            }
            player.getPersistentData().putString(PKEY_CHARONS_ASHES_LAST_ITEM, currentItem);
            player.getPersistentData().putInt(PKEY_CHARONS_ASHES_LAST_COUNT, currentCount);
        } else {
            player.getPersistentData().remove(PKEY_CHARONS_ASHES_LAST_ITEM);
            player.getPersistentData().remove(PKEY_CHARONS_ASHES_LAST_COUNT);
        }

        // ---- Red Skull: attack modifier when HP below 50% ----
        var redSkull = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.RED_SKULL.get()));
        if (redSkull.isPresent() && player.getHealth() < player.getMaxHealth() * 0.5F) {
            applyRedSkullAttackModifier(player);
        } else {
            removeRedSkullAttackModifier(player);
        }

        // ---- Ruined Helmet: double first ADDITION attack modifier ----
        var ruinedHelmet = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.RUINED_HELMET.get()));
        if (ruinedHelmet.isPresent()) {
            applyRuinedHelmetModifier(player);
        } else {
            removeRuinedHelmetModifier(player);
        }

        // ---- Brimstone: reset stacks after idle ----
        var brimstoneTick = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.BRIMSTONE.get()));
        if (brimstoneTick.isPresent()) {
            long now = player.level().getGameTime();
            long lastAttack = player.getPersistentData().getLong(PKEY_BRIMSTONE_LAST_ATTACK);
            if (now - lastAttack >= Config.brimstoneIdleSeconds * 20L) {
                player.getPersistentData().putInt(PKEY_BRIMSTONE_STACKS, 0);
                removeBrimstoneAttackModifier(player);
            }
        } else {
            player.getPersistentData().putInt(PKEY_BRIMSTONE_STACKS, 0);
            removeBrimstoneAttackModifier(player);
        }

        // ---- Divine Right: periodic max mana modifier re-application ----
        // ---- Divine Destiny: mana regen +1/s, max mana% boost via attribute ----
        if (IronSpellsCompat.isLoaded()) {
            var divineRight = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                    handler.findFirstCurio(stack -> stack.getItem() == ModItems.DIVINE_RIGHT.get()));
            if (divineRight.isPresent()) {
                applyIronSpellsMaxManaModifier(player, DIVINE_RIGHT_MAX_MANA_UUID, Config.divineRightManaFloor);
            }

            var divineDestiny = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                    handler.findFirstCurio(stack -> stack.getItem() == ModItems.DIVINE_DESTINY.get()));
            if (divineDestiny.isPresent()) {
                float maxMana = IronSpellsCompat.getMaxMana(player);
                float currentMana = IronSpellsCompat.getMana(player);
                if (currentMana >= 0 && currentMana < maxMana) {
                    divineDestinyManaTick++;
                    if (divineDestinyManaTick >= 20) {
                        divineDestinyManaTick = 0;
                        IronSpellsCompat.setMana(player, Math.min(currentMana + 1.0F, maxMana));
                    }
                } else {
                    divineDestinyManaTick = 0;
                }
            } else {
                divineDestinyManaTick = 0;
            }
        } else {
            divineDestinyManaTick = 0;
        }

        // ---- Data Disk: re-apply lightning spell power each tick ----
        if (IronSpellsCompat.isLoaded()) {
            var dataDisk = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                    handler.findFirstCurio(stack -> stack.getItem() == ModItems.DATA_DISK.get()));
            if (dataDisk.isPresent()) {
                IronSpellsCompat.applyAllSpellPowerBonus(player, DATA_DISK_ALL_SPELL_POWER_UUID);
            }
        }

        // ---- Symbiotic Virus: no more spell power (handled in LivingHurt) ----

        // ---- Power Cell: re-apply %-based max mana each tick ----
        if (IronSpellsCompat.isLoaded()) {
            var powerCell = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                    handler.findFirstCurio(stack -> stack.getItem() == ModItems.POWER_CELL.get()));
            if (powerCell.isPresent()) {
                float maxMana = IronSpellsCompat.getMaxMana(player);
                if (maxMana > 0) {
                    applyIronSpellsMaxManaModifier(player, POWER_CELL_MAX_MANA_UUID,
                            (int) (maxMana * Config.POWER_CELL_MANA_PERCENT));
                }
            }
        }

        // ---- Divine Right / Divine Destiny: Forged Sword attack speed bonus ----
        if (player.getMainHandItem().getItem() == ModItems.FORGED_SWORD.get()) {
            var divineRight = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                    handler.findFirstCurio(stack -> stack.getItem() == ModItems.DIVINE_RIGHT.get()));
            if (divineRight.isPresent()) {
                applyAttackSpeedModifier(player, DIVINE_RIGHT_FORGED_SWORD_AS_UUID, 0.3);
            } else {
                removeAttackSpeedModifier(player, DIVINE_RIGHT_FORGED_SWORD_AS_UUID);
            }
            var divineDestiny = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                    handler.findFirstCurio(stack -> stack.getItem() == ModItems.DIVINE_DESTINY.get()));
            if (divineDestiny.isPresent()) {
                applyAttackSpeedModifier(player, DIVINE_DESTINY_FORGED_SWORD_AS_UUID, 0.6);
            } else {
                removeAttackSpeedModifier(player, DIVINE_DESTINY_FORGED_SWORD_AS_UUID);
            }
        } else {
            removeAttackSpeedModifier(player, DIVINE_RIGHT_FORGED_SWORD_AS_UUID);
            removeAttackSpeedModifier(player, DIVINE_DESTINY_FORGED_SWORD_AS_UUID);
        }

        // ---- Funerary Mask: Goety soul regen + attack speed cleanup ----
        var funeraryMask = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.FUNERARY_MASK.get()));
        if (funeraryMask.isPresent()) {
            // Goety soul regen every 3s
            if (GoetyCompat.canStoreSoulEnergy(player)) {
                funeraryMaskSoulTick++;
                if (funeraryMaskSoulTick >= 60) {
                    funeraryMaskSoulTick = 0;
                    GoetyCompat.addSoulEnergy(player, 2);
                }
            } else {
                funeraryMaskSoulTick = 0;
            }

            // Remove attack speed mod if last hit was > 3s ago
            long lastHitTime = player.getPersistentData().getLong(PKEY_FUNERARY_MASK_LAST_HIT_TIME);
            if (player.level().getGameTime() - lastHitTime > 60) {
                removeFuneraryMaskAttackSpeed(player);
            }
        } else {
            funeraryMaskSoulTick = 0;
        }

        // ---- Music Box: play random disc from inventory → grant double-hit charge on finish ----
        var musicBox = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.MUSIC_BOX.get()));
        if (musicBox.isPresent()) {
            long gameTime = player.level().getGameTime();
            boolean playing = player.getPersistentData().getBoolean(PKEY_MUSIC_BOX_PLAYING);
            long triggerTick = player.getPersistentData().getLong(PKEY_MUSIC_BOX_TRIGGER_TICK);

            if (!playing) {
                // Pick a random record from all registered RecordItems in the modpack
                var registryRecords = new java.util.ArrayList<RecordItem>();
                for (var entry : ForgeRegistries.ITEMS.getEntries()) {
                    if (entry.getValue() instanceof RecordItem rec) {
                        registryRecords.add(rec);
                    }
                }
                if (!registryRecords.isEmpty()) {
                    RecordItem record = registryRecords.get(player.getRandom().nextInt(registryRecords.size()));
                    player.getPersistentData().putBoolean(PKEY_MUSIC_BOX_PLAYING, true);
                    player.getPersistentData().putLong(PKEY_MUSIC_BOX_TRIGGER_TICK, gameTime + 3600);
                    if (player.level() instanceof ServerLevel serverLevel) {
                        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                                record.getSound(), SoundSource.RECORDS, 1.0F, 1.0F);
                    }
                }
            } else if (gameTime >= triggerTick) {
                // Disc playback finished → grant one charge
                player.getPersistentData().putBoolean(PKEY_MUSIC_BOX_PLAYING, false);
                player.getPersistentData().putLong(PKEY_MUSIC_BOX_TRIGGER_TICK, 0);
                player.getPersistentData().putBoolean(PKEY_MUSIC_BOX_CHARGE, true);
            }
        }

        // ---- Bookmark: detect Goety soul consumption and refund 10% ----
        int bookmarkCharges = player.getPersistentData().getInt(PKEY_BOOKMARK_CHARGES);
        if (bookmarkCharges > 0 && GoetyCompat.isLoaded()) {
            int currentSouls = GoetyCompat.getSouls(player);
            int lastSouls = player.getPersistentData().getInt(PKEY_BOOKMARK_LAST_SOULS);
            if (lastSouls >= 0 && currentSouls >= 0 && currentSouls < lastSouls) {
                int consumed = lastSouls - currentSouls;
                int refund = Math.max(1, (int) (consumed * 0.1));
                GoetyCompat.addSoulEnergy(player, refund);
                bookmarkCharges--;
                player.getPersistentData().putInt(PKEY_BOOKMARK_CHARGES, bookmarkCharges);
            }
            player.getPersistentData().putInt(PKEY_BOOKMARK_LAST_SOULS, currentSouls);
        }

        // ---- Ivory Tile: every 3 soul consumed → refund 1 ----
        if (player.getPersistentData().getBoolean(PKEY_IVORY_TILE_EQUIPPED) && GoetyCompat.isLoaded()) {
            int currentSouls = GoetyCompat.getSouls(player);
            int lastSouls = player.getPersistentData().getInt(PKEY_IVORY_TILE_LAST_SOULS);
            if (lastSouls >= 0 && currentSouls >= 0 && currentSouls < lastSouls) {
                double remainder = player.getPersistentData().getDouble(PKEY_IVORY_TILE_REMAINDER);
                int consumed = lastSouls - currentSouls;
                double total = remainder + consumed;
                int refund = (int) (total / 3);
                if (refund > 0) {
                    GoetyCompat.addSoulEnergy(player, refund);
                    player.getPersistentData().putDouble(PKEY_IVORY_TILE_REMAINDER, total - refund * 3);
                } else {
                    player.getPersistentData().putDouble(PKEY_IVORY_TILE_REMAINDER, total);
                }
            }
            player.getPersistentData().putInt(PKEY_IVORY_TILE_LAST_SOULS, currentSouls);
        }

        // ---- Galactic Dust: 10 mana consumed → +1 block (格挡) ----
        // "格挡" = block, a shield-like counter that absorbs incoming damage before it reaches health.
        // Each block point cancels 1 point of incoming damage. Excess block beyond the hit persists.
        // Damage <= block → fully negated, remaining block carried over. Damage > block → block wiped, remainder dealt.
        // Block is consumed in the corresponding LivingHurtEvent handler below.
        if (player.getPersistentData().getBoolean(PKEY_GALACTIC_DUST_EQUIPPED) && IronSpellsCompat.isLoaded()) {
            float currentMana = IronSpellsCompat.getMana(player);
            float lastMana = player.getPersistentData().getFloat(PKEY_GALACTIC_DUST_LAST_MANA);
            if (lastMana >= 0 && currentMana >= 0 && currentMana < lastMana) {
                double remainder = player.getPersistentData().getDouble(PKEY_GALACTIC_DUST_REMAINDER);
                float consumed = lastMana - currentMana;
                double total = remainder + consumed;
                int gain = (int) (total / 10.0);
                if (gain > 0) {
                    int block = player.getPersistentData().getInt(PKEY_GALACTIC_DUST_BLOCK) + gain;
                    long now = player.level().getGameTime();
                    if (now < player.getPersistentData().getLong(PKEY_HELICAL_DART_BUFF_END)) {
                        block++;
                    }
                    player.getPersistentData().putInt(PKEY_GALACTIC_DUST_BLOCK, block);
                    player.getPersistentData().putDouble(PKEY_GALACTIC_DUST_REMAINDER, total - gain * 10.0);
                } else {
                    player.getPersistentData().putDouble(PKEY_GALACTIC_DUST_REMAINDER, total);
                }
            }
            player.getPersistentData().putFloat(PKEY_GALACTIC_DUST_LAST_MANA, currentMana);
        }

        // ---- Lunar Pastry: mana recovery bonus ----
        if (player.getPersistentData().getBoolean(PKEY_LUNAR_PASTRY_EQUIPPED) && IronSpellsCompat.isLoaded()) {
            float currentMana = IronSpellsCompat.getMana(player);
            float lastMana = player.getPersistentData().getFloat(PKEY_LUNAR_PASTRY_LAST_MANA);
            if (lastMana >= 0 && currentMana > lastMana) {
                int bonus = Config.lunarPastryManaBonus;
                IronSpellsCompat.setMana(player, currentMana + bonus);
                player.getPersistentData().putFloat(PKEY_LUNAR_PASTRY_LAST_MANA, currentMana + bonus);
            } else {
                player.getPersistentData().putFloat(PKEY_LUNAR_PASTRY_LAST_MANA, currentMana);
            }
        }

        // ---- Mini Regent: mana consumption → ATK gain + idle reset ----
        if (player.getPersistentData().getBoolean(PKEY_MINI_REGENT_EQUIPPED) && IronSpellsCompat.isLoaded()) {
            long now = player.level().getGameTime();
            float currentMana = IronSpellsCompat.getMana(player);
            float lastMana = player.getPersistentData().getFloat(PKEY_MINI_REGENT_LAST_MANA);

            // 检测法力消耗（次数而非量）
            if (lastMana >= 0 && currentMana < lastMana) {
                long lastCast = player.getPersistentData().getLong(PKEY_MINI_REGENT_LAST_CAST);
                int cdTicks = Config.miniRegentCooldownSeconds * 20;
                if (now - lastCast >= cdTicks) {
                    int bonus = player.getPersistentData().getInt(PKEY_MINI_REGENT_ATTACK_BONUS) + Config.MINI_REGENT_ATTACK_PER_CAST;
                    player.getPersistentData().putInt(PKEY_MINI_REGENT_ATTACK_BONUS, bonus);
                    player.getPersistentData().putLong(PKEY_MINI_REGENT_LAST_CAST, now);
                    applyMiniRegentAttackModifier(player, bonus);
                }
            }
            player.getPersistentData().putFloat(PKEY_MINI_REGENT_LAST_MANA, currentMana);

            // 空闲检测：超过 idle 秒无攻击行为则移除攻击力加成
            long lastAttack = player.getPersistentData().getLong(PKEY_MINI_REGENT_LAST_ATTACK);
            int idleTicks = Config.miniRegentIdleSeconds * 20;
            if (now - lastAttack >= idleTicks) {
                player.getPersistentData().putInt(PKEY_MINI_REGENT_ATTACK_BONUS, 0);
                removeMiniRegentAttackModifier(player);
            }
        }

        // ---- Book Repair Knife: track absorption gains → Doom charges ----
        var knife = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.BOOK_REPAIR_KNIFE.get()));
        if (knife.isPresent()) {
            float currentAbsorption = player.getAbsorptionAmount();
            float lastAbsorption = player.getPersistentData().getFloat(PKEY_BOOK_REPAIR_KNIFE_LAST_ABSORPTION);
            float gain = currentAbsorption - lastAbsorption;
            if (gain > 0) {
                double accum = player.getPersistentData().getDouble(PKEY_BOOK_REPAIR_KNIFE_HEAL_ACCUM);
                int charges = player.getPersistentData().getInt(PKEY_BOOK_REPAIR_KNIFE_CHARGES);
                double threshold = Config.BOOK_REPAIR_KNIFE_HEAL_PER_CHARGE;
                accum += gain;
                while (accum >= threshold) {
                    accum -= threshold;
                    charges++;
                }
                player.getPersistentData().putDouble(PKEY_BOOK_REPAIR_KNIFE_HEAL_ACCUM, accum);
                player.getPersistentData().putInt(PKEY_BOOK_REPAIR_KNIFE_CHARGES, charges);
            }
            player.getPersistentData().putFloat(PKEY_BOOK_REPAIR_KNIFE_LAST_ABSORPTION, currentAbsorption);
        }

        // ---- Lords Parasol: zero all villager trade costs when equipped ----
        var lordsParasol = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.LORDS_PARASOL.get()));
        if (lordsParasol.isPresent() && player.containerMenu instanceof MerchantMenu) {
            try {
                MerchantMenu mm = (MerchantMenu) player.containerMenu;
                Field traderField = MerchantMenu.class.getDeclaredField("trader");
                traderField.setAccessible(true);
                var trader = (net.minecraft.world.item.trading.Merchant) traderField.get(mm);

                Field costAField = MerchantOffer.class.getDeclaredField("costA");
                costAField.setAccessible(true);
                Field costBField = MerchantOffer.class.getDeclaredField("costB");
                costBField.setAccessible(true);
                for (MerchantOffer offer : trader.getOffers()) {
                    costAField.set(offer, ItemStack.EMPTY);
                    costBField.set(offer, ItemStack.EMPTY);
                }
            } catch (ReflectiveOperationException ignored) {
            }
        }

        // ---- Lords Parasol: offhand grants slow falling ----
        if (player.getOffhandItem().getItem() == ModItems.LORDS_PARASOL.get()) {
            if (!player.hasEffect(MobEffects.SLOW_FALLING) || player.getEffect(MobEffects.SLOW_FALLING).getDuration() < 100) {
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200, 0, false, true));
            }
        }

        // ---- Distinguished Cape: refresh 3 damage-cap charges every 15s ----
        var cape = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.DISTINGUISHED_CAPE.get()));
        if (cape.isPresent()) {
            long now = player.level().getGameTime();
            long nextRefresh = player.getPersistentData().getLong(PKEY_DISTINGUISHED_CAPE_COOLDOWN);
            if (now >= nextRefresh) {
                player.getPersistentData().putInt(PKEY_DISTINGUISHED_CAPE_CHARGES, 3);
                player.getPersistentData().putLong(PKEY_DISTINGUISHED_CAPE_COOLDOWN, now + 300);
            }
        }

        // ---- Jeweled Mask: random positive buff cycle ----
        if (player.getPersistentData().getBoolean(PKEY_JEWELED_MASK_ACTIVE)) {
            long now = player.level().getGameTime();
            long nextTrigger = player.getPersistentData().getLong(PKEY_JEWELED_MASK_NEXT_TRIGGER);
            if (now >= nextTrigger) {
                // Remove previous buff
                String prevEffect = player.getPersistentData().getString(PKEY_JEWELED_MASK_CURRENT_EFFECT);
                if (!prevEffect.isEmpty()) {
                    var effect = ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.tryParse(prevEffect));
                    if (effect != null) {
                        player.removeEffect(effect);
                    }
                }
                // Pick random beneficial effect
                var beneficial = new java.util.ArrayList<MobEffect>();
                for (var entry : ForgeRegistries.MOB_EFFECTS.getEntries()) {
                    if (entry.getValue().isBeneficial()) {
                        beneficial.add(entry.getValue());
                    }
                }
                if (!beneficial.isEmpty()) {
                    MobEffect chosen = beneficial.get(player.getRandom().nextInt(beneficial.size()));
                    int durationTicks = Config.jeweledMaskDurationSeconds * 20;
                    player.addEffect(new MobEffectInstance(chosen, durationTicks, 0, false, true));
                    player.getPersistentData().putString(PKEY_JEWELED_MASK_CURRENT_EFFECT,
                            ForgeRegistries.MOB_EFFECTS.getKey(chosen).toString());
                    int cooldownTicks = Config.jeweledMaskCooldownSeconds * 20;
                    player.getPersistentData().putLong(PKEY_JEWELED_MASK_NEXT_TRIGGER, now + durationTicks + cooldownTicks);
                }
            }
        }

        // ---- Sere Talon: periodically re-apply effects if cleared (e.g. milk) ----
        if (player.getPersistentData().getBoolean(PKEY_SERE_TALON_EQUIPPED)) {
            long now = player.level().getGameTime();
            if (now % 100 == 0) { // every 5 seconds
                var data = player.getPersistentData();
                String chosen = data.getString(PKEY_SERE_TALON_EFFECTS);
                if (!chosen.isEmpty()) {
                    for (String id : chosen.split(",")) {
                        var effect = ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.tryParse(id));
                        if (effect != null && !player.hasEffect(effect)) {
                            player.addEffect(new MobEffectInstance(effect, 72000, 0, false, true));
                        }
                    }
                }
            }
        }
        if (player.getPersistentData().getBoolean(PKEY_VITRUVIAN_MINION_EQUIPPED)) {
            long now = player.level().getGameTime();
            if (now % 40 == 0) {
                double healthMult = Config.vitruvianMinionHealthMultiplier;
                double radius = 64.0;
                AABB box = new AABB(player.getX() - radius, player.getY() - radius, player.getZ() - radius,
                        player.getX() + radius, player.getY() + radius, player.getZ() + radius);
                var nearby = player.level().getEntitiesOfClass(LivingEntity.class, box,
                        e -> e.isAlive() && !e.getPersistentData().getBoolean(PKEY_VITRUVIAN_BUFFED));
                for (LivingEntity entity : nearby) {
                    Player summonOwner = resolveOwner(entity);
                    if (summonOwner == player) {
                        buffMinionHealth(entity, healthMult);
                    }
                }
            }
        }
    }

    // === Attribute modifier helpers ===

    private static void applyReachModifier(LivingEntity entity, UUID uuid, double amount) {
        var attr = entity.getAttribute(ForgeMod.ENTITY_REACH.get());
        if (attr != null) {
            attr.removeModifier(uuid);
            attr.addTransientModifier(new AttributeModifier(uuid, "ReachModifier", amount, AttributeModifier.Operation.ADDITION));
        }
    }

    private static void removeReachModifier(LivingEntity entity, UUID uuid) {
        var attr = entity.getAttribute(ForgeMod.ENTITY_REACH.get());
        if (attr != null) {
            attr.removeModifier(uuid);
        }
    }

    private static void applyMaxHealthModifier(LivingEntity entity, UUID uuid, double amount) {
        var attr = entity.getAttribute(Attributes.MAX_HEALTH);
        if (attr != null) {
            attr.removeModifier(uuid);
            attr.addTransientModifier(new AttributeModifier(uuid, "MaxHealthModifier", amount, AttributeModifier.Operation.ADDITION));
            entity.setHealth(Math.min(entity.getMaxHealth(), entity.getHealth() + (float) amount));
        }
    }

    private static void removeMaxHealthModifier(LivingEntity entity, UUID uuid) {
        var attr = entity.getAttribute(Attributes.MAX_HEALTH);
        if (attr != null) {
            attr.removeModifier(uuid);
        }
    }

    private static void applyAttackSpeedModifier(LivingEntity entity, UUID uuid, double amount) {
        var attr = entity.getAttribute(Attributes.ATTACK_SPEED);
        if (attr != null) {
            attr.removeModifier(uuid);
            attr.addTransientModifier(new AttributeModifier(uuid, "AttackSpeedModifier", amount, AttributeModifier.Operation.ADDITION));
        }
    }

    private static void removeAttackSpeedModifier(LivingEntity entity, UUID uuid) {
        var attr = entity.getAttribute(Attributes.ATTACK_SPEED);
        if (attr != null) {
            attr.removeModifier(uuid);
        }
    }

    private static void applyFuneraryMaskAttackSpeed(LivingEntity entity) {
        applyAttackSpeedModifier(entity, FUNERARY_MASK_ATTACK_SPEED_UUID, Config.funeraryMaskAttackSpeedBonus);
    }

    private static void removeFuneraryMaskAttackSpeed(LivingEntity entity) {
        removeAttackSpeedModifier(entity, FUNERARY_MASK_ATTACK_SPEED_UUID);
    }

    /** Strip all ATTACK_SPEED modifiers except the Fiddle's own. */
    private static void stripNonFiddleAttackSpeed(LivingEntity entity) {
        var attr = entity.getAttribute(Attributes.ATTACK_SPEED);
        if (attr != null) {
            var toRemove = attr.getModifiers().stream()
                    .filter(m -> !m.getId().equals(FIDDLE_ATTACK_SPEED_UUID))
                    .toList();
            toRemove.forEach(attr::removeModifier);
        }
    }

    private static void applyRedSkullAttackModifier(LivingEntity entity) {
        var attr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attr != null) {
            if (attr.getModifier(RED_SKULL_ATTACK_UUID) != null
                    && Math.abs(attr.getModifier(RED_SKULL_ATTACK_UUID).getAmount() - Config.redSkullAttackBonus) < 0.001) return;
            attr.removeModifier(RED_SKULL_ATTACK_UUID);
            attr.addTransientModifier(new AttributeModifier(RED_SKULL_ATTACK_UUID,
                    "RedSkullAttackMod", Config.redSkullAttackBonus, AttributeModifier.Operation.ADDITION));
        }
    }

    private static void removeRedSkullAttackModifier(LivingEntity entity) {
        var attr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attr != null) {
            attr.removeModifier(RED_SKULL_ATTACK_UUID);
        }
    }

    private static void applyRuinedHelmetModifier(LivingEntity entity) {
        var attr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attr == null) return;
        // Find first ADDITION modifier (skip base)
        double firstAddition = 0;
        for (AttributeModifier mod : attr.getModifiers()) {
            if (mod.getOperation() == AttributeModifier.Operation.ADDITION && mod.getId() != RUINED_HELMET_UUID) {
                firstAddition = mod.getAmount();
                break;
            }
        }
        if (firstAddition <= 0) {
            removeRuinedHelmetModifier(entity);
            return;
        }
        if (attr.getModifier(RUINED_HELMET_UUID) != null
                && Math.abs(attr.getModifier(RUINED_HELMET_UUID).getAmount() - firstAddition) < 0.001) return;
        attr.removeModifier(RUINED_HELMET_UUID);
        attr.addTransientModifier(new AttributeModifier(RUINED_HELMET_UUID,
                "RuinedHelmetMod", firstAddition, AttributeModifier.Operation.ADDITION));
    }

    private static void removeRuinedHelmetModifier(LivingEntity entity) {
        var attr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attr != null) {
            attr.removeModifier(RUINED_HELMET_UUID);
        }
    }

    private static void applyBrimstoneAttackModifier(LivingEntity entity) {
        var attr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attr == null) return;
        var data = entity.getPersistentData();
        int stacks = data.getInt(PKEY_BRIMSTONE_STACKS);
        double total = stacks * Config.BRIMSTONE_ATTACK_PER_STACK;
        if (total <= 0) {
            removeBrimstoneAttackModifier(entity);
            return;
        }
        if (attr.getModifier(BRIMSTONE_ATTACK_UUID) != null
                && Math.abs(attr.getModifier(BRIMSTONE_ATTACK_UUID).getAmount() - total) < 0.001) return;
        attr.removeModifier(BRIMSTONE_ATTACK_UUID);
        attr.addTransientModifier(new AttributeModifier(BRIMSTONE_ATTACK_UUID,
                "BrimstoneAttackMod", total, AttributeModifier.Operation.ADDITION));
    }

    private static void removeBrimstoneAttackModifier(LivingEntity entity) {
        var attr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attr != null) {
            attr.removeModifier(BRIMSTONE_ATTACK_UUID);
        }
    }

    private static void applyBrimstoneTargetAttackModifier(LivingEntity target) {
        var attr = target.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attr == null) return;
        var data = target.getPersistentData();
        int applied = data.getInt(PKEY_BRIMSTONE_TARGET_ATK) + Config.BRIMSTONE_TARGET_ATTACK_BONUS;
        data.putInt(PKEY_BRIMSTONE_TARGET_ATK, applied);
        data.putInt(PKEY_BRIMSTONE_TARGET_ATTACKS, 0);
        attr.removeModifier(BRIMSTONE_TARGET_TAG);
        attr.addTransientModifier(new AttributeModifier(BRIMSTONE_TARGET_TAG,
                "BrimstoneTargetAtk", applied, AttributeModifier.Operation.ADDITION));
    }

    /** Resolve the Player responsible for a damage source (direct or via projectile/pet). */
    @javax.annotation.Nullable
    private static Player resolvePlayerSource(DamageSource source) {
        if (source.getEntity() instanceof Player player) return player;
        if (source.getEntity() instanceof Projectile projectile && projectile.getOwner() instanceof Player player) return player;
        return null;
    }

    private static void applyAllAttributeBoost(LivingEntity entity, UUID namespaceUuid, double multiplier) {
        float prevMaxHealth = entity.getMaxHealth();
        ForgeRegistries.ATTRIBUTES.getEntries().forEach(entry -> {
            Attribute attribute = entry.getValue();
            var instance = entity.getAttribute(attribute);
            if (instance != null) {
                UUID attrUuid = UUID.nameUUIDFromBytes((namespaceUuid + entry.getKey().location().toString()).getBytes());
                instance.removeModifier(attrUuid);
                instance.addTransientModifier(new AttributeModifier(attrUuid,
                        "VoodooRingBoost", multiplier, AttributeModifier.Operation.MULTIPLY_BASE));
            }
        });
        entity.heal(entity.getMaxHealth() - prevMaxHealth);
    }

    private static void removeAllAttributeBoost(LivingEntity entity, UUID namespaceUuid) {
        ForgeRegistries.ATTRIBUTES.getEntries().forEach(entry -> {
            Attribute attribute = entry.getValue();
            var instance = entity.getAttribute(attribute);
            if (instance != null) {
                UUID attrUuid = UUID.nameUUIDFromBytes((namespaceUuid + entry.getKey().location().toString()).getBytes());
                instance.removeModifier(attrUuid);
            }
        });
    }

    /** Increase the phylactery absorption by an increment. */
    private static void growPhylacteryAbsorption(LivingEntity entity, String pkey, float increment) {
        double current = entity.getPersistentData().getDouble(pkey);
        double newTotal = current + increment;
        entity.getPersistentData().putDouble(pkey, newTotal);
        entity.setAbsorptionAmount(entity.getAbsorptionAmount() + increment);
    }

    /** Apply a flat max-mana modifier using Iron's Spells attribute (safe if ISSB not loaded). */
    private static void applyIronSpellsMaxManaModifier(LivingEntity entity, UUID uuid, int amount) {
        Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(
                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "max_mana"));
        if (attr != null) {
            var instance = entity.getAttribute(attr);
            if (instance != null) {
                instance.removeModifier(uuid);
                instance.addTransientModifier(new AttributeModifier(uuid,
                        "SayukiManaBoost", amount, AttributeModifier.Operation.ADDITION));
            }
        }
    }

    /** Remove the max-mana modifier. */
    private static void removeIronSpellsMaxManaModifier(LivingEntity entity, UUID uuid) {
        Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(
                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "max_mana"));
        if (attr != null) {
            var instance = entity.getAttribute(attr);
            if (instance != null) {
                instance.removeModifier(uuid);
            }
        }
    }

    // === Core lightning helpers (shared by Cracked Core, Infused Core, Emotion Chip) ===

    /** Get effective core cooldown ticks, reduced by 20% if Power Cell is equipped. */
    private static long getCoreCooldownTicks(Player player) {
        long baseTicks = Config.crackedCoreCooldownSeconds * 20L;
        boolean hasPowerCell = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.POWER_CELL.get())).isPresent();
        return hasPowerCell ? (long) (baseTicks * 0.8) : baseTicks;
    }

    /** Get bonus damage from Data Disk for core/virus effects. */
    private static int getDataDiskCoreBonus(Player player) {
        var dataDisk = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.DATA_DISK.get()));
        return dataDisk.isPresent() ? Config.dataDiskCoreBonus : 0;
    }

    /** Schedule Symbiotic Virus sonic boom via target PersistentData — first boom at 2s, +0.5s each extra. */
    private static void performVirusSonicBoom(LivingEntity target, Player player) {
        int boomCount = 1 + (hasGoldPlatedCables(player) ? 1 : 0);
        int bonus = getDataDiskCoreBonus(player);
        float perBoomDamage = 1.0F + (float) bonus / boomCount;

        if (!(target.level() instanceof ServerLevel)) {
            target.hurt(target.level().damageSources().magic(), perBoomDamage * boomCount);
            return;
        }

        var data = target.getPersistentData();
        // Guard: already queued in this tick — prevent duplicate stacking
        if (data.contains("SayukiSonicBoomEpoch")) {
            if (data.getLong("SayukiSonicBoomEpoch") == target.level().getGameTime()) return;
        }
        data.putLong("SayukiSonicBoomEpoch", target.level().getGameTime());

        int existingRemaining = data.getInt("SayukiSonicBoomRemaining");
        data.putFloat("SayukiSonicBoomDamage", perBoomDamage);
        data.putInt("SayukiSonicBoomRemaining", existingRemaining + boomCount);
        data.putString("SayukiSonicBoomOwner", player.getStringUUID());
        if (existingRemaining <= 0) {
            data.putLong("SayukiSonicBoomTrigger", target.level().getGameTime() + 40);
        }
    }

    /** Play sonic boom visual+sound at the given world position. */
    private static void playSonicBoomEffect(ServerLevel serverLevel, double x, double y, double z) {
        serverLevel.playSound(null, x, y, z,
                SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 0.1F, 1.0F);

        double maxHeight = 7.0;
        int rings = 20;
        for (int ring = 0; ring < rings; ring++) {
            double dy = (maxHeight * ring) / rings;
            double radius = 0.25 + dy * 0.10;
            int particlesPerRing = 2;
            for (int p = 0; p < particlesPerRing; p++) {
                double angle = (2.0 * Math.PI * p) / particlesPerRing + ring * 0.5;
                double px = x + Math.cos(angle) * radius;
                double pz = z + Math.sin(angle) * radius;
                serverLevel.sendParticles(ParticleTypes.SONIC_BOOM,
                        px, y + 0.1 + dy, pz, 1, 0, 0, 0, 0);
            }
        }
    }

    // === LivingTickEvent: process delayed sonic boom ===

    @SubscribeEvent
    public static void onLivingTickSonicBoom(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;
        var data = entity.getPersistentData();
        int remaining = data.getInt("SayukiSonicBoomRemaining");
        if (remaining <= 0) return;
        long triggerTick = data.getLong("SayukiSonicBoomTrigger");
        if (entity.level().getGameTime() < triggerTick) return;

        // Pop one boom: play effect + apply damage
        float damage = data.getFloat("SayukiSonicBoomDamage");
        remaining--;

        if (entity.level() instanceof ServerLevel serverLevel) {
            playSonicBoomEffect(serverLevel, entity.getX(), entity.getY(), entity.getZ());
        }
        entity.hurt(entity.level().damageSources().magic(), damage);

        // Increment metronome counter for the owner
        String ownerUuid = data.getString("SayukiSonicBoomOwner");
        if (!ownerUuid.isEmpty() && entity.level() instanceof ServerLevel serverLevel) {
            ServerPlayer owner = serverLevel.getServer().getPlayerList().getPlayer(UUID.fromString(ownerUuid));
            if (owner != null) {
                tryIncrementMetronomeCounter(owner);
            }
        }

        if (remaining > 0) {
            data.putInt("SayukiSonicBoomRemaining", remaining);
            data.putLong("SayukiSonicBoomTrigger", triggerTick + 10); // 0.5s gap between booms
        } else {
            data.remove("SayukiSonicBoomDamage");
            data.remove("SayukiSonicBoomRemaining");
            data.remove("SayukiSonicBoomTrigger");
            data.remove("SayukiSonicBoomEpoch");
            data.remove("SayukiSonicBoomOwner");
        }
    }

    // === LivingTickEvent: Music Box delayed double-hit (1 tick after original hit) ===

    @SubscribeEvent
    public static void onLivingTickMusicBoxDoubleHit(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;
        var data = entity.getPersistentData();
        if (!data.getBoolean(PKEY_MUSIC_BOX_DOUBLE_HIT)) return;

        // Apply second hit (same amount, player-sourced) then clear
        float amount = data.getFloat("SayukiMusicBoxDmg");
        String ownerUuid = data.getString("SayukiMusicBoxOwner");

        data.remove(PKEY_MUSIC_BOX_DOUBLE_HIT);
        data.remove("SayukiMusicBoxDmg");
        data.remove("SayukiMusicBoxOwner");

        if (entity.level() instanceof ServerLevel serverLevel && !ownerUuid.isEmpty()) {
            ServerPlayer owner = serverLevel.getServer().getPlayerList().getPlayer(UUID.fromString(ownerUuid));
            if (owner != null) {
                entity.hurt(entity.level().damageSources().mobAttack(owner), amount);
            }
        }
    }

    private static void summonLightningOnTarget(LivingEntity target, Player player) {
        int boltCount = hasGoldPlatedCables(player) ? 2 : 1;
        for (int i = 0; i < boltCount; i++) {
            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(target.level());
            if (bolt != null) {
                bolt.moveTo(target.getX(), target.getY(), target.getZ());
                target.level().addFreshEntity(bolt);
                tryIncrementMetronomeCounter(player);
            }
        }
        // Data Disk bonus: extra lightning damage
        int bonus = getDataDiskCoreBonus(player);
        if (bonus > 0) {
            target.hurt(target.level().damageSources().lightningBolt(), (float) bonus);
        }
    }

    private static void summonAreaLightningBolts(LivingEntity center, Player player) {
        double radius = Config.infusedCoreLightningRadius;
        double radiusSq = radius * radius;
        int boltCount = 3 + (hasGoldPlatedCables(player) ? 1 : 0);
        for (int i = 0; i < boltCount; i++) {
            double angle = center.getRandom().nextDouble() * Math.PI * 2;
            double dist = 1.0 + center.getRandom().nextDouble() * (radius * 0.4);
            double sx = center.getX() + Math.cos(angle) * dist;
            double sz = center.getZ() + Math.sin(angle) * dist;

            AABB box = new AABB(sx - radius, center.getY() - 4, sz - radius,
                    sx + radius, center.getY() + 4, sz + radius);
            var hostiles = center.level().getEntitiesOfClass(LivingEntity.class, box,
                    e -> e instanceof Enemy && e.isAlive() && e != center
                            && e.distanceToSqr(sx, e.getY(), sz) <= radiusSq);

            double tx, tz;
            if (!hostiles.isEmpty()) {
                LivingEntity h = hostiles.get(center.getRandom().nextInt(hostiles.size()));
                tx = h.getX();
                tz = h.getZ();
            } else {
                tx = sx;
                tz = sz;
            }

            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(center.level());
            if (bolt != null) {
                int groundY = center.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING,
                        BlockPos.containing(tx, 0, tz)).getY();
                bolt.moveTo(tx, groundY, tz);
                center.level().addFreshEntity(bolt);
                tryIncrementMetronomeCounter(player);
            }
        }
    }

    // === Metronome counter ===

    private static void tryIncrementMetronomeCounter(Player player) {
        if (player.level().isClientSide()) return;

        var metronome = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.METRONOME.get()));
        if (metronome.isEmpty()) return;

        // Cooldown check
        long cooldownTicks = Config.metronomeCooldownSeconds * 20L;
        long now = player.level().getGameTime();
        if (now - player.getPersistentData().getLong(PKEY_METRONOME_COOLDOWN) < cooldownTicks) return;

        int counter = player.getPersistentData().getInt(PKEY_METRONOME_COUNTER) + 1;
        player.getPersistentData().putInt(PKEY_METRONOME_COUNTER, counter);

        int threshold = Config.METRONOME_LIGHTNING_THRESHOLD;
        if (counter >= threshold) {
            player.getPersistentData().putInt(PKEY_METRONOME_COUNTER, 0);
            player.getPersistentData().putLong(PKEY_METRONOME_COOLDOWN, now);

            // Visual cooldown overlay on the metronome item
            player.getCooldowns().addCooldown(ModItems.METRONOME.get(), (int) cooldownTicks);

            // Burst: deal damage to all hostiles in radius
            double radius = Config.infusedCoreLightningRadius;
            double radiusSq = radius * radius;
            AABB box = new AABB(player.getX() - radius, player.getY() - 4, player.getZ() - radius,
                    player.getX() + radius, player.getY() + 4, player.getZ() + radius);
            var hostiles = player.level().getEntitiesOfClass(LivingEntity.class, box,
                    e -> e instanceof Enemy && e.isAlive() && e != player
                            && e.distanceToSqr(player) <= radiusSq);

            float damage = (float) Config.METRONOME_DAMAGE;
            for (LivingEntity hostile : hostiles) {
                hostile.hurt(hostile.level().damageSources().lightningBolt(), damage);
            }
        }
    }

    // === Whispering Earring helpers ===

    private static double getMainHandWeaponAttackDamage(Player player) {
        ItemStack weapon = player.getMainHandItem();
        if (weapon.isEmpty()) return 0.0;
        var modifiers = weapon.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE);
        double total = 0.0;
        for (AttributeModifier mod : modifiers) {
            if (mod.getOperation() == AttributeModifier.Operation.ADDITION) {
                total += mod.getAmount();
            }
        }
        return total;
    }

    private static void applyWhisperingAttackModifier(LivingEntity entity, double amount) {
        var attr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attr != null) {
            attr.removeModifier(WHISPERING_EARRING_ATTACK_UUID);
            attr.addTransientModifier(new AttributeModifier(WHISPERING_EARRING_ATTACK_UUID,
                    "WhisperingAttackMod", amount, AttributeModifier.Operation.ADDITION));
        }
    }

    private static void removeWhisperingAttackModifier(LivingEntity entity) {
        var attr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attr != null) {
            attr.removeModifier(WHISPERING_EARRING_ATTACK_UUID);
        }
    }

    private static void clearWhisperingData(LivingEntity entity) {
        var data = entity.getPersistentData();
        data.remove(PKEY_WHISPERING_EQUIPPED);
        data.remove(PKEY_WHISPERING_WEAPON_DMG);
        data.remove(PKEY_WHISPERING_WINDOW_START);
        data.remove(PKEY_WHISPERING_WINDOW_DMG);
    }

    private static void syncWhisperingWeaponModifier(Player player) {
        double currentWeaponDmg = getMainHandWeaponAttackDamage(player);
        var data = player.getPersistentData();
        double lastKnownDmg = data.getDouble(PKEY_WHISPERING_WEAPON_DMG);

        if (Math.abs(currentWeaponDmg - lastKnownDmg) > 0.0001) {
            removeWhisperingAttackModifier(player);
            if (currentWeaponDmg > 0) {
                applyWhisperingAttackModifier(player, currentWeaponDmg);
            }
            data.putDouble(PKEY_WHISPERING_WEAPON_DMG, currentWeaponDmg);
        }
    }

    private static void handleWhisperingBuffs(LivingHurtEvent event, Player player) {
        // Check if whisper buff is active → this attack triggers self-damage
        if (player.hasEffect(ModEffects.WHISPER.get())) {
            float selfDamage = event.getAmount() * (float) Config.whisperingSelfDamageRatio;
            if (selfDamage > 0) {
                player.hurt(ModDamageTypes.whisperingEcho(player), selfDamage);
            }
            // Remove whisper, apply combat
            player.removeEffect(ModEffects.WHISPER.get());
            player.addEffect(new MobEffectInstance(ModEffects.COMBAT.get(),
                    Config.whisperingIdleSeconds * 20, 0, false, false, true));
        } else {
            // Refresh combat buff duration
            player.addEffect(new MobEffectInstance(ModEffects.COMBAT.get(),
                    Config.whisperingIdleSeconds * 20, 0, false, false, true));
        }

        // Track damage window for threshold check
        long now = player.level().getGameTime();
        long windowTicks = Config.whisperingIdleSeconds * 20L;
        var data = player.getPersistentData();
        long windowStart = data.getLong(PKEY_WHISPERING_WINDOW_START);
        double windowDamage = data.getDouble(PKEY_WHISPERING_WINDOW_DMG);

        if (windowStart == 0 || now - windowStart > windowTicks) {
            windowStart = now;
            windowDamage = 0;
        }
        data.putLong(PKEY_WHISPERING_WINDOW_START, windowStart);
        data.putDouble(PKEY_WHISPERING_WINDOW_DMG, windowDamage + event.getAmount());
    }

    // === Blood-Soaked Rose: weapon damage sync helpers (same logic as Whispering Earring) ===

    private static void applyBloodSoakedRoseAttackModifier(LivingEntity entity, double amount) {
        var attr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attr != null) {
            attr.removeModifier(BLOOD_SOAKED_ROSE_ATTACK_UUID);
            attr.addTransientModifier(new AttributeModifier(BLOOD_SOAKED_ROSE_ATTACK_UUID,
                    "BloodSoakedRoseAttackMod", amount, AttributeModifier.Operation.ADDITION));
        }
    }

    private static void removeAttackDamageModifier(LivingEntity entity, UUID uuid) {
        var attr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attr != null) {
            attr.removeModifier(uuid);
        }
    }

    private static void clearBloodSoakedRoseData(LivingEntity entity) {
        var data = entity.getPersistentData();
        data.remove(PKEY_BLOOD_SOAKED_ROSE_EQUIPPED);
        data.remove(PKEY_BLOOD_SOAKED_ROSE_WEAPON_DMG);
    }

    private static void syncBloodSoakedRoseWeaponModifier(Player player) {
        double currentWeaponDmg = getMainHandWeaponAttackDamage(player);
        var data = player.getPersistentData();
        double lastKnownDmg = data.getDouble(PKEY_BLOOD_SOAKED_ROSE_WEAPON_DMG);

        if (Math.abs(currentWeaponDmg - lastKnownDmg) > 0.0001) {
            removeAttackDamageModifier(player, BLOOD_SOAKED_ROSE_ATTACK_UUID);
            if (currentWeaponDmg > 0) {
                applyBloodSoakedRoseAttackModifier(player, currentWeaponDmg);
            }
            data.putDouble(PKEY_BLOOD_SOAKED_ROSE_WEAPON_DMG, currentWeaponDmg);
        }
    }

    // === Regalite: Forged Sword mining ===

    private static boolean isRegaliteForgedSwordActive(Player player) {
        if (player.getMainHandItem().getItem() != ModItems.FORGED_SWORD.get()) return false;
        return CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.REGALITE.get())).isPresent();
    }

    @SubscribeEvent
    public static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (isRegaliteForgedSwordActive(player)) {
            event.setCanHarvest(true);
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (!isRegaliteForgedSwordActive(player)) return;
        BlockState state = event.getState();
        if (state.isAir()) return;

        // Only boost speed for blocks that need a pickaxe
        float required = state.getDestroySpeed(player.level(), event.getPosition().orElse(null));
        if (required <= 0) return;

        // Netherite pickaxe base speed 9.0, apply similar level
        event.setNewSpeed(Math.max(event.getOriginalSpeed(), 9.0F));
    }

    // === Mini Regent: attack modifier helpers ===

    private static void applyMiniRegentAttackModifier(LivingEntity entity, int amount) {
        var attr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attr != null) {
            attr.removeModifier(MINI_REGENT_ATTACK_UUID);
            attr.addTransientModifier(new AttributeModifier(MINI_REGENT_ATTACK_UUID,
                    "MiniRegentAttackMod", amount, AttributeModifier.Operation.ADDITION));
        }
    }

    private static void removeMiniRegentAttackModifier(LivingEntity entity) {
        var attr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attr != null) {
            attr.removeModifier(MINI_REGENT_ATTACK_UUID);
        }
    }

    // === Vitruvian Minion: owner resolution & health buff ===

    /** Resolve the owning Player of an entity (tamable, ISS summon, or Goety minion). */
    @javax.annotation.Nullable
    private static Player resolveOwner(@javax.annotation.Nullable net.minecraft.world.entity.Entity entity) {
        if (entity == null) return null;
        // Vanilla tamable
        if (entity instanceof TamableAnimal ta && ta.getOwner() instanceof Player p) return p;
        // ISS summon
        if (IronSpellsCompat.isLoaded()) {
            LivingEntity issOwner = IronSpellsCompat.getSummonOwner((LivingEntity) entity);
            if (issOwner instanceof Player p) return p;
        }
        // Goety minion
        if (GoetyCompat.isLoaded()) {
            LivingEntity goetyOwner = GoetyCompat.getMinionOwner((LivingEntity) entity);
            if (goetyOwner instanceof Player p) return p;
        }
        return null;
    }

    private static boolean hasVitruvianMinion(Player player) {
        return player.getPersistentData().getBoolean(PKEY_VITRUVIAN_MINION_EQUIPPED);
    }

    private static void buffMinionHealth(LivingEntity entity, double multiplier) {
        var attr = entity.getAttribute(Attributes.MAX_HEALTH);
        if (attr != null) {
            double base = attr.getBaseValue();
            attr.setBaseValue(base * multiplier);
            entity.setHealth(entity.getMaxHealth());
            entity.getPersistentData().putBoolean(PKEY_VITRUVIAN_BUFFED, true);
        }
    }

    // === Sere Talon helpers ===

    /** Pick 2 negative + 3 positive effects at random, store IDs, apply with long duration. */
    private static void rollAndApplySereTalonEffects(LivingEntity entity) {
        var negative = new java.util.ArrayList<MobEffect>();
        var positive = new java.util.ArrayList<MobEffect>();
        for (var entry : ForgeRegistries.MOB_EFFECTS.getEntries()) {
            MobEffect effect = entry.getValue();
            if (effect.isBeneficial()) {
                positive.add(effect);
            } else {
                negative.add(effect);
            }
        }
        var random = entity.getRandom();
        StringBuilder sb = new StringBuilder();
        int duration = 72000; // 1 hour, refreshed by tick

        for (int i = 0; i < SERE_TALON_NEGATIVE_COUNT && !negative.isEmpty(); i++) {
            MobEffect e = negative.remove(random.nextInt(negative.size()));
            entity.addEffect(new MobEffectInstance(e, duration, 0, false, true));
            if (!sb.isEmpty()) sb.append(",");
            sb.append(ForgeRegistries.MOB_EFFECTS.getKey(e));
        }
        for (int i = 0; i < SERE_TALON_POSITIVE_COUNT && !positive.isEmpty(); i++) {
            MobEffect e = positive.remove(random.nextInt(positive.size()));
            entity.addEffect(new MobEffectInstance(e, duration, 0, false, true));
            if (!sb.isEmpty()) sb.append(",");
            sb.append(ForgeRegistries.MOB_EFFECTS.getKey(e));
        }
        entity.getPersistentData().putString(PKEY_SERE_TALON_EFFECTS, sb.toString());
    }

    /** Remove all Sere Talon-granted effects and clear persistent data. */
    private static void clearSereTalonEffects(LivingEntity entity) {
        var data = entity.getPersistentData();
        String chosen = data.getString(PKEY_SERE_TALON_EFFECTS);
        if (!chosen.isEmpty()) {
            for (String id : chosen.split(",")) {
                var effect = ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.tryParse(id));
                if (effect != null) {
                    entity.removeEffect(effect);
                }
            }
        }
        data.remove(PKEY_SERE_TALON_EQUIPPED);
        data.remove(PKEY_SERE_TALON_EFFECTS);
    }
}
