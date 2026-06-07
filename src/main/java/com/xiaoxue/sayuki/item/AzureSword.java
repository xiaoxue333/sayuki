/**
 * Sayuki — Azure Sword (SwordItem, water beam + freeze fluids, double-damage mark via LivingHurtEvent)
 * Compat: Goety-2 and IronsSpellbooks — uses damage type water_magic (sayuki: namespace), no conflict
 */
package com.xiaoxue.sayuki.item;

import com.xiaoxue.sayuki.Config;
import com.xiaoxue.sayuki.damage.ModDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.tags.FluidTags;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AzureSword extends SwordItem {
    private static final int SLOWNESS_DURATION = 99;
    private static final int SLOWNESS_AMPLIFIER = 9;
    private static final long ATTACK_COOLDOWN_TICKS = 100;
    private static final String TAG_LAST_ATTACK = "SayukiAzureLastAttack";
    public static final String MARK_DOUBLE_DAMAGE = "SayukiAzureDoubleDamage";

    private static final int WATER_BEAM_RANGE = 22;

    public AzureSword(Properties properties) {
        super(Tiers.IRON, 0, -2.0F, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        long currentTime = attacker.level().getGameTime();

        // Double-damage mark: always fires on slowed targets, removes one slowness level
        MobEffectInstance slownessEffect = target.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
        if (slownessEffect != null) {
            int currentLevel = slownessEffect.getAmplifier();
            if (currentLevel > 0) {
                target.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                target.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SLOWDOWN,
                        slownessEffect.getDuration(),
                        currentLevel - 1
                ));
            } else {
                target.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            }
            target.getPersistentData().putBoolean(MARK_DOUBLE_DAMAGE, true);
        }

        // Slowness application: 100t CD with 99t duration preserves 1-tick gap
        long lastAttackTime = getLastAttackTime(stack);
        if (currentTime - lastAttackTime < ATTACK_COOLDOWN_TICKS) {
            return super.hurtEnemy(stack, target, attacker);
        }
        setLastAttackTime(stack, currentTime);

        target.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SLOWDOWN,
                SLOWNESS_DURATION,
                SLOWNESS_AMPLIFIER
        ));

        return super.hurtEnemy(stack, target, attacker);
    }

    private long getLastAttackTime(ItemStack stack) {
        return stack.getOrCreateTag().getLong(TAG_LAST_ATTACK);
    }

    private void setLastAttackTime(ItemStack stack, long time) {
        stack.getOrCreateTag().putLong(TAG_LAST_ATTACK, time);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.azure_sword.1"));
        tooltip.add(Component.translatable("tooltip.sayuki.azure_sword.2"));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    public static void performWaterBeam(Level level, Player player) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 look = player.getLookAngle();

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.AMBIENT_UNDERWATER_ENTER, SoundSource.PLAYERS, 1.8F, 0.6F);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.PLAYERS, 0.8F, 1.8F);

        if (level.isClientSide()) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) level;
        Set<LivingEntity> hitEntities = new HashSet<>();

        Vec3 up = new Vec3(0, 1, 0);
        if (Math.abs(look.dot(up)) > 0.99) {
            up = new Vec3(1, 0, 0);
        }
        Vec3 right = look.cross(up).normalize();
        up = right.cross(look).normalize();

        Vector3f waterCore = new Vector3f(0.1F, 0.5F, 1.0F);
        Vector3f waterMid = new Vector3f(0.05F, 0.35F, 0.9F);
        Vector3f waterOuter = new Vector3f(0.0F, 0.2F, 0.7F);

        for (int step = 1; step <= WATER_BEAM_RANGE; step++) {
            double distance = step * 0.65;
            Vec3 pos = eyePos.add(look.scale(distance));
            double progress = step / (double) WATER_BEAM_RANGE;

            double coreRadius = 0.18;
            double midRadius = 0.45 + progress * 0.15;
            double outerRadius = 0.7 + progress * 0.2;

            for (int i = 0; i < 8; i++) {
                double angle = (2.0 * Math.PI * i) / 8 + progress * Math.PI * 3;
                Vec3 offset = right.scale(Math.cos(angle) * coreRadius).add(up.scale(Math.sin(angle) * coreRadius));
                serverLevel.sendParticles(
                        new DustParticleOptions(waterCore, 1.4F),
                        pos.x + offset.x, pos.y + offset.y, pos.z + offset.z,
                        1, 0.0, 0.0, 0.0, 0.0);
            }

            for (int i = 0; i < 12; i++) {
                double angle = (2.0 * Math.PI * i) / 12 + progress * Math.PI * 2.5;
                double r = midRadius * (0.7 + 0.3 * Math.sin(i * 0.5 + progress * 4));
                Vec3 offset = right.scale(Math.cos(angle) * r).add(up.scale(Math.sin(angle) * r));
                serverLevel.sendParticles(
                        new DustParticleOptions(waterMid, 1.0F),
                        pos.x + offset.x, pos.y + offset.y, pos.z + offset.z,
                        1, 0.0, 0.0, 0.0, 0.002);

                if (i % 3 == 0) {
                    double r2 = outerRadius * (0.8 + 0.2 * Math.sin(i * 0.3 + progress * 6));
                    Vec3 offset2 = right.scale(Math.cos(angle + 0.3) * r2).add(up.scale(Math.sin(angle + 0.3) * r2));
                    serverLevel.sendParticles(
                            new DustParticleOptions(waterOuter, 0.6F),
                            pos.x + offset2.x, pos.y + offset2.y, pos.z + offset2.z,
                            1, 0.02, 0.02, 0.02, 0.01);
                }
            }

            if (step % 2 == 0) {
                for (int i = 0; i < 3; i++) {
                    double angle = (2.0 * Math.PI * i) / 3 + progress * 2;
                    double r = midRadius * 1.3;
                    Vec3 offset = right.scale(Math.cos(angle) * r).add(up.scale(Math.sin(angle) * r));
                    serverLevel.sendParticles(ParticleTypes.BUBBLE,
                            pos.x + offset.x, pos.y + offset.y, pos.z + offset.z,
                            1, 0.04, 0.06, 0.04, 0.02);
                }
            }

            BlockPos blockPos = new BlockPos((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z));
            FluidState fluidState = serverLevel.getFluidState(blockPos);
            if (!fluidState.isEmpty()) {
                if (fluidState.is(FluidTags.WATER)) {
                    serverLevel.setBlock(blockPos, Blocks.ICE.defaultBlockState(), 3);
                    serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                            pos.x, pos.y, pos.z, 5, 0.3, 0.3, 0.3, 0.01);
                } else if (fluidState.is(FluidTags.LAVA)) {
                    boolean isSource = fluidState.isSource();
                    serverLevel.setBlock(blockPos,
                            isSource ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.STONE.defaultBlockState(), 3);
                    if (isSource) {
                        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                                pos.x, pos.y, pos.z, 8, 0.3, 0.3, 0.3, 0.03);
                    }
                }
            }

            Vec3 start = eyePos.add(look.scale(distance - 0.6));
            Vec3 end = pos;
            AABB aabb = new AABB(
                    Math.min(start.x, end.x) - 1.0, Math.min(start.y, end.y) - 1.0, Math.min(start.z, end.z) - 1.0,
                    Math.max(start.x, end.x) + 1.0, Math.max(start.y, end.y) + 1.0, Math.max(start.z, end.z) + 1.0);

            for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, aabb,
                    e -> e != player && e.isAlive() && !hitEntities.contains(e))) {
                HitResult hit = checkEntityIntersecting(target, start, end, 0.5f);
                if (hit.getType() != HitResult.Type.MISS) {
                    hitEntities.add(target);
                    target.hurt(ModDamageTypes.waterMagic(player), (float) Config.azureWaterBeamDamage);
                    Vec3 knockback = look.scale(1.0).add(0, 0.2, 0);
                    target.setDeltaMovement(target.getDeltaMovement().add(knockback));
                    target.hurtMarked = true;

                    for (int j = 0; j < 6; j++) {
                        serverLevel.sendParticles(ParticleTypes.SPLASH,
                                target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
                                4, 0.25, 0.25, 0.25, 0.05);
                    }
                    serverLevel.sendParticles(ParticleTypes.BUBBLE,
                            target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
                            6, 0.3, 0.3, 0.3, 0.03);
                }
            }
        }

        Vec3 endPos = eyePos.add(look.scale(WATER_BEAM_RANGE * 0.65));
        for (int i = 0; i < 15; i++) {
            double angle = (2.0 * Math.PI * i) / 15;
            double r = 1.0;
            Vec3 offset = right.scale(Math.cos(angle) * r).add(up.scale(Math.sin(angle) * r));
            serverLevel.sendParticles(
                    new DustParticleOptions(waterCore, 2.0F),
                    endPos.x + offset.x, endPos.y + offset.y, endPos.z + offset.z,
                    1, 0.0, 0.0, 0.0, 0.02);
            serverLevel.sendParticles(ParticleTypes.SPLASH,
                    endPos.x + offset.x * 0.7, endPos.y + offset.y * 0.7, endPos.z + offset.z * 0.7,
                    1, 0.0, 0.02, 0.0, 0.04);
        }
    }

    private static HitResult checkEntityIntersecting(LivingEntity target, Vec3 start, Vec3 end, float size) {
        AABB aabb = target.getBoundingBox().inflate(size);
        return aabb.clip(start, end).<HitResult>map(vec -> new HitResult(vec) {
            @Override
            public Type getType() {
                return Type.ENTITY;
            }
        }).orElse(new HitResult(Vec3.ZERO) {
            @Override
            public Type getType() {
                return Type.MISS;
            }
        });
    }
}
