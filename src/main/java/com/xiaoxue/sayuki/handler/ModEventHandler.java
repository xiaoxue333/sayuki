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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.effect.MobEffectInstance;
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
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

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
    private static final UUID DATA_DISK_LIGHTNING_POWER_UUID = UUID.fromString("d1e2f3a4-b5c6-7890-defa-bcdef1234567");
    private static final UUID SYMBIOTIC_VIRUS_ELDRITCH_POWER_UUID = UUID.fromString("e2f3a4b5-c6d7-8901-efab-cdef12345678");
    private static final UUID POWER_CELL_MAX_MANA_UUID = UUID.fromString("f3a4b5c6-d7e8-9012-fabc-def123456789");
    private static final UUID FUNERARY_MASK_ATTACK_SPEED_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private static final UUID DIVINE_RIGHT_FORGED_SWORD_AS_UUID = UUID.fromString("d5e6f7a8-b9c0-1234-defa-bcdef1234568");
    private static final UUID DIVINE_DESTINY_FORGED_SWORD_AS_UUID = UUID.fromString("e6f7a8b9-c0d1-2345-efab-cdef12345679");
    private static final UUID MINI_REGENT_ATTACK_UUID = UUID.fromString("b4c5d6e7-f8a9-0123-abcd-ef1234567890");
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
    private static final String PKEY_METRONOME_COUNTER = "SayukiMetronomeCounter";
    private static final String PKEY_METRONOME_COOLDOWN = "SayukiMetronomeCooldown";
    private static final String PKEY_BONE_FLUTE_HP_ACCUM = "SayukiBoneFluteHp";
    private static final String PKEY_BOOK_REPAIR_KNIFE_HEAL_ACCUM = "SayukiBookRepairKnifeHeal";
    private static final String PKEY_BOOK_REPAIR_KNIFE_CHARGES = "SayukiBookRepairKnifeCharges";
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
    private static final String PKEY_LUNAR_PASTRY_EQUIPPED = "SayukiLunarPastryEquipped";
    private static final String PKEY_LUNAR_PASTRY_LAST_MANA = "SayukiLunarPastryLastMana";
    private static final String PKEY_MINI_REGENT_EQUIPPED = "SayukiMiniRegentEquipped";
    private static final String PKEY_MINI_REGENT_ATTACK_BONUS = "SayukiMiniRegentAttackBonus";
    private static final String PKEY_MINI_REGENT_LAST_CAST = "SayukiMiniRegentLastCast";
    private static final String PKEY_MINI_REGENT_LAST_MANA = "SayukiMiniRegentLastMana";
    private static final String PKEY_MINI_REGENT_LAST_ATTACK = "SayukiMiniRegentLastAttack";
    private static final String PKEY_VITRUVIAN_MINION_EQUIPPED = "SayukiVitruvianMinionEquipped";
    private static final String PKEY_VITRUVIAN_BUFFED = "SayukiVitruvianBuffed";

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

    // === LivingHeal: Book Repair Knife — accumulate healing into Doom charges ===

    @SubscribeEvent
    public static void onLivingHealBookRepairKnife(LivingHealEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        var knife = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.BOOK_REPAIR_KNIFE.get()));
        if (knife.isEmpty()) return;

        double healAmount = event.getAmount();
        double accum = player.getPersistentData().getDouble(PKEY_BOOK_REPAIR_KNIFE_HEAL_ACCUM);
        int charges = player.getPersistentData().getInt(PKEY_BOOK_REPAIR_KNIFE_CHARGES);
        double threshold = Config.bookRepairKnifeHealPerCharge;

        accum += healAmount;
        while (accum >= threshold) {
            accum -= threshold;
            charges++;
        }

        player.getPersistentData().putDouble(PKEY_BOOK_REPAIR_KNIFE_HEAL_ACCUM, accum);
        player.getPersistentData().putInt(PKEY_BOOK_REPAIR_KNIFE_CHARGES, charges);
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
            boolean shouldDouble = target.getPersistentData().getBoolean(AzureSword.MARK_DOUBLE_DAMAGE);
            if (shouldDouble) {
                target.getPersistentData().remove(AzureSword.MARK_DOUBLE_DAMAGE);

                float armor = target.getArmorValue();
                float toughness = (float) target.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
                float postArmorDamage = event.getAmount();

                if (postArmorDamage > 0) {
                    float preArmor = postArmorDamage;
                    for (int i = 0; i < 8; i++) {
                        float result = CombatRules.getDamageAfterAbsorb(preArmor, armor, toughness);
                        if (result <= 0 || Math.abs(result - postArmorDamage) / postArmorDamage < 0.001f) break;
                        preArmor += (postArmorDamage - result);
                    }
                    event.setAmount(Math.max(preArmor, postArmorDamage) * 2.0F);
                }
            }
        }

        // Whispering Earring
        if (player.getPersistentData().getBoolean(PKEY_WHISPERING_EQUIPPED)) {
            syncWhisperingWeaponModifier(player);
            handleWhisperingBuffs(event, player);
        }

        // Vulnerable Power: targets with the effect take +25% damage
        if (event.getEntity().hasEffect(ModEffects.VULNERABLE_POWER.get())) {
            event.setAmount(event.getAmount() * 1.25F);
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

        // ---- Bone Flute: absorption per melee hit ----
        var boneFlute = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.BONE_FLUTE.get()));
        if (boneFlute.isPresent()) {
            double increment = Config.boneFluteHealthPerHit;
            double current = player.getPersistentData().getDouble(PKEY_BONE_FLUTE_HP_ACCUM);
            player.getPersistentData().putDouble(PKEY_BONE_FLUTE_HP_ACCUM, current + increment);
            player.setAbsorptionAmount(player.getAbsorptionAmount() + (float) increment);
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

        // ---- Projectile bounce: Ring of the Snake / Ring of the Drake ----
        if (event.getSource().getDirectEntity() instanceof Projectile projectile) {

            boolean hasSnake = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                    handler.findFirstCurio(stack -> stack.getItem() == ModItems.RING_OF_THE_SNAKE.get())).isPresent();
            boolean hasDrake = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                    handler.findFirstCurio(stack -> stack.getItem() == ModItems.RING_OF_THE_DRAKE.get())).isPresent();

            if (hasSnake || hasDrake) {
                int bounceCount = projectile.getPersistentData().getInt(PKEY_BOUNCE_COUNT);
                int maxBounces = hasDrake ? 6 : 2;
                double retention = hasDrake ? 0.75 : 0.5;

                // Apply damage reduction based on bounce count
                double multiplier = Math.pow(retention, bounceCount);
                event.setAmount((float) (event.getAmount() * multiplier));

                // Drake: apply Weak Power (duration stacking)
                if (hasDrake) {
                    LivingEntity target = event.getEntity();
                    var existing = target.getEffect(ModEffects.WEAK_POWER.get());
                    int newDuration = (existing != null) ? existing.getDuration() + 3 * 20 : 3 * 20;
                    target.addEffect(new MobEffectInstance(ModEffects.WEAK_POWER.get(), newDuration, 0,
                            false, false, true));
                }

                // Try to bounce to next target
                if (bounceCount < maxBounces && !event.getEntity().level().isClientSide()) {
                    LivingEntity currentTarget = event.getEntity();
                    Vec3 hitPos = currentTarget.position();
                    AABB searchBox = new AABB(hitPos.subtract(12, 12, 12), hitPos.add(12, 12, 12));

                    LivingEntity nextTarget = currentTarget.level().getEntitiesOfClass(Mob.class, searchBox,
                            mob -> mob instanceof Enemy && mob != currentTarget && mob.isAlive())
                            .stream()
                            .min((a, b) -> Double.compare(
                                    a.distanceToSqr(currentTarget),
                                    b.distanceToSqr(currentTarget)))
                            .orElse(null);

                    if (nextTarget != null) {
                        Projectile newProj = (Projectile) projectile.getType().create(currentTarget.level());
                        if (newProj != null) {
                            newProj.setOwner(player);
                            newProj.setPos(currentTarget.getEyePosition().add(0, -0.3, 0));
                            Vec3 dir = nextTarget.getEyePosition().subtract(newProj.position()).normalize();
                            newProj.setDeltaMovement(dir.scale(1.5));
                            newProj.getPersistentData().putInt(PKEY_BOUNCE_COUNT, bounceCount + 1);
                            currentTarget.level().addFreshEntity(newProj);
                        }
                    }
                }
            }
        }
    }

    // === LivingHurt: Cracked Core — summons lightning bolt on target (shared cooldown) ===

    @SubscribeEvent
    public static void onLivingHurtCrackedCore(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        var crackedCore = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.CRACKED_CORE.get()));
        if (crackedCore.isEmpty()) return;

        // Configurable cooldown (shared with Infused Core / Emotion Chip)
        long cooldownTicks = Config.crackedCoreCooldownSeconds * 20L;
        long now = player.level().getGameTime();
        if (now - player.getPersistentData().getLong(PKEY_CRACKED_CORE_COOLDOWN) < cooldownTicks) return;
        player.getPersistentData().putLong(PKEY_CRACKED_CORE_COOLDOWN, now);

        summonLightningOnTarget(event.getEntity(), player);
    }

    // === LivingHurt: Emotion Chip — when hurt, triggers Cracked/Infused Core effect ===

    @SubscribeEvent
    public static void onLivingHurtEmotionChip(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        var emotionChip = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.EMOTION_CHIP.get()));
        if (emotionChip.isEmpty()) return;

        // Check which core is equipped (mutually exclusive, but check both)
        var crackedCore = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.CRACKED_CORE.get()));
        var infusedCore = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.INFUSED_CORE.get()));

        if (crackedCore.isEmpty() && infusedCore.isEmpty()) return;

        // Shared cooldown with Cracked/Infused Core
        long cooldownTicks = Config.crackedCoreCooldownSeconds * 20L;
        long now = player.level().getGameTime();
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

    // === LivingAttackEvent: Infused Core — lightning immunity + damage conversion + area bolts ===

    @SubscribeEvent
    public static void onLivingAttackInfusedCore(net.minecraftforge.event.entity.living.LivingAttackEvent event) {
        if (applyingCoreLightning) return;

        // Defense: wearer immune to lightning
        if (event.getEntity() instanceof Player player) {
            var handler = CuriosApi.getCuriosInventory(player).resolve();
            if (handler.isPresent()) {
                var infused = handler.get().findFirstCurio(
                        stack -> stack.getItem() == ModItems.INFUSED_CORE.get());
                if (infused.isPresent() && event.getSource().is(DamageTypeTags.IS_LIGHTNING)) {
                    event.setCanceled(true);
                    return;
                }
            }
        }

        // Offense: attacker converts all damage to lightning + summons bolts
        if (!(event.getSource().getEntity() instanceof Player attacker)) return;
        if (attacker.level().isClientSide()) return;

        var handler = CuriosApi.getCuriosInventory(attacker).resolve();
        if (handler.isEmpty()) return;

        var infused = handler.get().findFirstCurio(
                stack -> stack.getItem() == ModItems.INFUSED_CORE.get());
        if (infused.isEmpty()) return;

        // If already lightning, let through (lightning is the final converted type)
        if (event.getSource().is(DamageTypeTags.IS_LIGHTNING)) return;

        float amount = event.getAmount();
        LivingEntity target = event.getEntity();

        // Cancel original damage
        event.setCanceled(true);

        // Apply as sourceless lightning
        applyingCoreLightning = true;
        target.hurt(target.level().damageSources().lightningBolt(), amount);
        applyingCoreLightning = false;

        // Summon area lightning bolts (shared cooldown with Cracked Core)
        long cooldownTicks = Config.crackedCoreCooldownSeconds * 20L;
        long now = attacker.level().getGameTime();
        if (now - attacker.getPersistentData().getLong(PKEY_CRACKED_CORE_COOLDOWN) >= cooldownTicks) {
            attacker.getPersistentData().putLong(PKEY_CRACKED_CORE_COOLDOWN, now);
            summonAreaLightningBolts(attacker, attacker);
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

        // ---- Data Disk: lightning spell power ----
        if (from.getItem() == ModItems.DATA_DISK.get()) {
            removeLightningSpellPowerModifier(entity, DATA_DISK_LIGHTNING_POWER_UUID);
        }
        if (to.getItem() == ModItems.DATA_DISK.get() && IronSpellsCompat.isLoaded()) {
            applyLightningSpellPowerModifier(entity, DATA_DISK_LIGHTNING_POWER_UUID, 1);
        }

        // ---- Symbiotic Virus: eldritch spell power ----
        if (from.getItem() == ModItems.SYMBIOTIC_VIRUS.get()) {
            removeEldritchSpellPowerModifier(entity, SYMBIOTIC_VIRUS_ELDRITCH_POWER_UUID);
        }
        if (to.getItem() == ModItems.SYMBIOTIC_VIRUS.get() && IronSpellsCompat.isLoaded()) {
            applyEldritchSpellPowerModifier(entity, SYMBIOTIC_VIRUS_ELDRITCH_POWER_UUID, 1);
        }

        // ---- Power Cell: %-based max mana ----
        if (from.getItem() == ModItems.POWER_CELL.get()) {
            removeIronSpellsMaxManaModifier(entity, POWER_CELL_MAX_MANA_UUID);
        }
        if (to.getItem() == ModItems.POWER_CELL.get() && IronSpellsCompat.isLoaded() && entity instanceof Player player) {
            float maxMana = IronSpellsCompat.getMaxMana(player);
            if (maxMana > 0) {
                applyIronSpellsMaxManaModifier(entity, POWER_CELL_MAX_MANA_UUID,
                        (int) (maxMana * Config.powerCellManaPercent));
            }
        }

        // ---- Bone Flute: persistent HP accumulation ----
        if (from.getItem() == ModItems.BONE_FLUTE.get()) {
            entity.getPersistentData().remove(PKEY_BONE_FLUTE_HP_ACCUM);
        }
        if (to.getItem() == ModItems.BONE_FLUTE.get()) {
            entity.getPersistentData().putDouble(PKEY_BONE_FLUTE_HP_ACCUM, 0);
        }

        // ---- Book Repair Knife: heal accumulation / charges ----
        if (from.getItem() == ModItems.BOOK_REPAIR_KNIFE.get()) {
            entity.getPersistentData().remove(PKEY_BOOK_REPAIR_KNIFE_HEAL_ACCUM);
            entity.getPersistentData().remove(PKEY_BOOK_REPAIR_KNIFE_CHARGES);
        }
        if (to.getItem() == ModItems.BOOK_REPAIR_KNIFE.get()) {
            entity.getPersistentData().putDouble(PKEY_BOOK_REPAIR_KNIFE_HEAL_ACCUM, 0);
            entity.getPersistentData().putInt(PKEY_BOOK_REPAIR_KNIFE_CHARGES, 0);
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
        }
        if (to.getItem() == ModItems.GALACTIC_DUST.get()) {
            entity.getPersistentData().putBoolean(PKEY_GALACTIC_DUST_EQUIPPED, true);
            entity.getPersistentData().putDouble(PKEY_GALACTIC_DUST_REMAINDER, 0);
            entity.getPersistentData().putFloat(PKEY_GALACTIC_DUST_LAST_MANA, -1);
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
                applyLightningSpellPowerModifier(player, DATA_DISK_LIGHTNING_POWER_UUID, 1);
            }
        }

        // ---- Symbiotic Virus: re-apply eldritch spell power each tick ----
        if (IronSpellsCompat.isLoaded()) {
            var symbioticVirus = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                    handler.findFirstCurio(stack -> stack.getItem() == ModItems.SYMBIOTIC_VIRUS.get()));
            if (symbioticVirus.isPresent()) {
                applyEldritchSpellPowerModifier(player, SYMBIOTIC_VIRUS_ELDRITCH_POWER_UUID, 1);
            }
        }

        // ---- Power Cell: re-apply %-based max mana each tick ----
        if (IronSpellsCompat.isLoaded()) {
            var powerCell = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                    handler.findFirstCurio(stack -> stack.getItem() == ModItems.POWER_CELL.get()));
            if (powerCell.isPresent()) {
                float maxMana = IronSpellsCompat.getMaxMana(player);
                if (maxMana > 0) {
                    applyIronSpellsMaxManaModifier(player, POWER_CELL_MAX_MANA_UUID,
                            (int) (maxMana * Config.powerCellManaPercent));
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

        // ---- Galactic Dust: 10 mana consumed → +1 absorption ----
        if (player.getPersistentData().getBoolean(PKEY_GALACTIC_DUST_EQUIPPED) && IronSpellsCompat.isLoaded()) {
            float currentMana = IronSpellsCompat.getMana(player);
            float lastMana = player.getPersistentData().getFloat(PKEY_GALACTIC_DUST_LAST_MANA);
            if (lastMana >= 0 && currentMana >= 0 && currentMana < lastMana) {
                double remainder = player.getPersistentData().getDouble(PKEY_GALACTIC_DUST_REMAINDER);
                float consumed = lastMana - currentMana;
                double total = remainder + consumed;
                int gain = (int) (total / 10.0);
                if (gain > 0) {
                    player.setAbsorptionAmount(player.getAbsorptionAmount() + gain);
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
                    int bonus = player.getPersistentData().getInt(PKEY_MINI_REGENT_ATTACK_BONUS) + Config.miniRegentAttackPerCast;
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

        // ---- Vitruvian Minion: buff summon health (scan every 2s) ----
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

    // === Lightning spell power helpers (Iron's Spells compat) ===

    private static void applyLightningSpellPowerModifier(LivingEntity entity, UUID uuid, int amount) {
        Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(
                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "lightning_spell_power"));
        if (attr != null) {
            var instance = entity.getAttribute(attr);
            if (instance != null) {
                instance.removeModifier(uuid);
                instance.addTransientModifier(new AttributeModifier(uuid,
                        "SayukiLightningPower", amount, AttributeModifier.Operation.ADDITION));
            }
        }
    }

    private static void removeLightningSpellPowerModifier(LivingEntity entity, UUID uuid) {
        Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(
                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "lightning_spell_power"));
        if (attr != null) {
            var instance = entity.getAttribute(attr);
            if (instance != null) {
                instance.removeModifier(uuid);
            }
        }
    }

    // === Eldritch spell power helpers (Iron's Spells compat) ===

    private static void applyEldritchSpellPowerModifier(LivingEntity entity, UUID uuid, int amount) {
        Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(
                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "eldritch_spell_power"));
        if (attr != null) {
            var instance = entity.getAttribute(attr);
            if (instance != null) {
                instance.removeModifier(uuid);
                instance.addTransientModifier(new AttributeModifier(uuid,
                        "SayukiEldritchPower", amount, AttributeModifier.Operation.ADDITION));
            }
        }
    }

    private static void removeEldritchSpellPowerModifier(LivingEntity entity, UUID uuid) {
        Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(
                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "eldritch_spell_power"));
        if (attr != null) {
            var instance = entity.getAttribute(attr);
            if (instance != null) {
                instance.removeModifier(uuid);
            }
        }
    }

    // === Core lightning helpers (shared by Cracked Core, Infused Core, Emotion Chip) ===

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

        int threshold = Config.metronomeLightningThreshold;
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

            float damage = (float) Config.metronomeDamage;
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
}
