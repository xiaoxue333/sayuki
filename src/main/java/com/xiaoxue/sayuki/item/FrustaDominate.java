/**
 * Sayuki — Frusta Dominate (SwordItem, sonic boom sweep via RightClick event)
 * Compat: Goety-2 and IronsSpellbooks — uses damage type whip (sayuki: namespace), no conflict
 */
package com.xiaoxue.sayuki.item;

import com.xiaoxue.sayuki.Config;
import com.xiaoxue.sayuki.damage.ModDamageTypes;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FrustaDominate extends SwordItem {

    private static final int RANGE = 14;
    private static final float MAX_SLASH_WIDTH = 6.0F;

    public FrustaDominate(Properties properties) {
        super(Tiers.IRON, 0, -2.4F, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.frusta_dominate.1"));
    }

    public static void performSonicBoom(Level level, Player player) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 look = player.getLookAngle();

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 2.0F, 0.7F);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENDER_DRAGON_FLAP, SoundSource.PLAYERS, 0.9F, 1.8F);

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

        Vector3f purple = new Vector3f(0.65F, 0.1F, 0.95F);

        int totalSteps = RANGE * 2;
        for (int step = 1; step <= totalSteps; step++) {
            double progress = step / (double) totalSteps;
            double distance = progress * RANGE;
            Vec3 pos = eyePos.add(look.scale(distance));

            double halfWidth = MAX_SLASH_WIDTH * (0.2 + 0.8 * progress) * 0.5;
            float particleStep = 0.25f;
            int particleCount = (int) (halfWidth * 2 / particleStep);

            for (int i = 0; i < particleCount; i++) {
                double offset = particleStep * (i - particleCount / 2.0);
                Vec3 p = pos.add(right.scale(offset));

                float speed = 0.1f;
                double dx = (serverLevel.random.nextDouble() * 2 - 1) * speed;
                double dy = (serverLevel.random.nextDouble() * 2 - 1) * speed;
                double dz = (serverLevel.random.nextDouble() * 2 - 1) * speed;

                serverLevel.sendParticles(
                        new DustParticleOptions(purple, 2.0F),
                        p.x, p.y, p.z,
                        1, dx, dy, dz, 0.0);
            }

            if (step % 3 == 0) {
                Vec3 leftTip = pos.add(right.scale(-halfWidth));
                Vec3 rightTip = pos.add(right.scale(halfWidth));

                serverLevel.sendParticles(ParticleTypes.WITCH,
                        leftTip.x, leftTip.y, leftTip.z,
                        1, 0.02, 0.03, 0.02, 0.02);
                serverLevel.sendParticles(ParticleTypes.WITCH,
                        rightTip.x, rightTip.y, rightTip.z,
                        1, 0.02, 0.03, 0.02, 0.02);
            }

            double halfW = halfWidth + 0.5;
            AABB aabb = new AABB(
                    pos.x - halfW, pos.y - 0.8, pos.z - halfW,
                    pos.x + halfW, pos.y + 0.8, pos.z + halfW);

            for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, aabb,
                    e -> e != player && e.isAlive() && !hitEntities.contains(e))) {
                hitEntities.add(target);
                target.hurt(ModDamageTypes.whip(player), (float) Config.frustaDamage);
                Vec3 knockback = look.scale(3.5).add(0, 0.3, 0);
                target.setDeltaMovement(target.getDeltaMovement().add(knockback));
                target.hurtMarked = true;

                for (int j = 0; j < 5; j++) {
                    serverLevel.sendParticles(
                            new DustParticleOptions(purple, 3.5F),
                            target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
                            5, 0.3, 0.3, 0.3, 0.06);
                }
                serverLevel.sendParticles(ParticleTypes.WITCH,
                        target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
                        8, 0.3, 0.3, 0.3, 0.04);
            }
        }

        Vec3 endPos = eyePos.add(look.scale(RANGE));
        double halfEnd = MAX_SLASH_WIDTH * 0.5;
        float endStep = 0.25f;
        int endCount = (int) (halfEnd * 2 / endStep);
        for (int i = 0; i < endCount; i++) {
            double offset = endStep * (i - endCount / 2.0);
            Vec3 p = endPos.add(right.scale(offset));

            float speed = 0.12f;
            double dx = (serverLevel.random.nextDouble() * 2 - 1) * speed;
            double dy = (serverLevel.random.nextDouble() * 2 - 1) * speed;
            double dz = (serverLevel.random.nextDouble() * 2 - 1) * speed;

            serverLevel.sendParticles(
                    new DustParticleOptions(purple, 3.5F),
                    p.x, p.y, p.z,
                    1, dx, dy, dz, 0.0);

            if (i % 6 == 0) {
                serverLevel.sendParticles(ParticleTypes.WITCH,
                        p.x, p.y, p.z,
                        1, 0.03, 0.05, 0.03, 0.02);
            }
        }
    }
}
