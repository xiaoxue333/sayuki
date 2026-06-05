/**
 * Sayuki — Heart Grenade (throwable, explosive via HeartGrenadeProjectile entity)
 * Compat: Goety-2 and IronsSpellbooks — uses sayuki: namespace projectile, no entity name clash
 */
package com.xiaoxue.sayuki.item;

import com.xiaoxue.sayuki.entity.HeartGrenadeProjectile;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import java.util.List;
import net.minecraft.world.phys.Vec3;

public class HeartGrenade extends Item {

    public HeartGrenade(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.heart_grenade.1"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        Vec3 lookVec = player.getLookAngle();
        Vec3 throwPos = new Vec3(
                player.getX() + lookVec.x * 0.3,
                player.getY() + player.getEyeHeight() * 0.75,
                player.getZ() + lookVec.z * 0.3
        );

        if (!level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;

            HeartGrenadeProjectile projectile = new HeartGrenadeProjectile(level, player);
            projectile.setPos(throwPos.x, throwPos.y, throwPos.z);
            projectile.shoot(lookVec.x, lookVec.y, lookVec.z, 1.4F, 0.7F);
            level.addFreshEntity(projectile);

            for (int i = 0; i < 5; i++) {
                double angle = (2.0 * Math.PI * i) / 5;
                serverLevel.sendParticles(ParticleTypes.SMOKE,
                        throwPos.x + Math.cos(angle) * 0.1,
                        throwPos.y + Math.sin(angle) * 0.1,
                        throwPos.z,
                        1, lookVec.x * 0.05, lookVec.y * 0.05, lookVec.z * 0.05, 0.025);
            }
            serverLevel.sendParticles(ParticleTypes.FLAME,
                    throwPos.x, throwPos.y, throwPos.z,
                    2, 0.05, 0.05, 0.05, 0.01);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.7F, 1.2F);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 0.5F, 1.5F);

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
