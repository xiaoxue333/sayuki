/**
 * Sayuki — Magenta Spear (SwordItem, thrust attack via RightClick event)
 * Compat: Goety-2 and IronsSpellbooks — uses PlayerInteractEvent.RightClickItem, no mixin sharing, independent items
 */
package com.xiaoxue.sayuki.item;

import com.xiaoxue.sayuki.Config;
import com.xiaoxue.sayuki.damage.ModDamageTypes;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
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
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class MagentaSpearItem extends SwordItem {

    private static final int THRUST_RANGE = 6;
    private static final float THRUST_WIDTH = 1.5F;
    private static final float DASH_VELOCITY = 1.8F;
    private static final int ANIM_DURATION = 8;

    public MagentaSpearItem(Properties properties) {
        super(Tiers.IRON, 5, -2.7F, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.magenta_spear.1"));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
                if (stack.hasTag()) {
                    long thrustTick = stack.getTag().getLong("SayukiSpearThrustTick");
                    if (thrustTick > 0) {
                        long elapsed = entity.level().getGameTime() - thrustTick;
                        if (elapsed >= 0 && elapsed < ANIM_DURATION) {
                            return HumanoidModel.ArmPose.THROW_SPEAR;
                        }
                    }
                }
                return HumanoidModel.ArmPose.ITEM;
            }
        });
    }

    public static void performRapidThrust(Level level, Player player) {
        Vec3 dir = player.getLookAngle().normalize();
        Vec3 dash = dir.scale(DASH_VELOCITY);

        player.setDeltaMovement(player.getDeltaMovement().add(dash.x, 0.0, dash.z));
        player.hurtMarked = true;
        player.hasImpulse = true;

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.5F, 0.8F);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.2F);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.TRIDENT_HIT, SoundSource.PLAYERS, 0.5F, 1.8F);

        Vec3 eye = player.getEyePosition();
        AABB sweepBox = new AABB(
                eye.x - THRUST_WIDTH, eye.y - 0.3, eye.z - THRUST_WIDTH,
                eye.x + THRUST_WIDTH, eye.y + 0.3, eye.z + THRUST_WIDTH)
                .expandTowards(dir.x * THRUST_RANGE, dir.y * THRUST_RANGE, dir.z * THRUST_RANGE);

        for (LivingEntity target : player.level().getEntitiesOfClass(LivingEntity.class, sweepBox,
                e -> e != player && e.isAlive())) {
            Vec3 toTarget = target.position().subtract(eye);
            double dot = toTarget.normalize().dot(dir);
            if (dot < 0.85) continue;
            if (toTarget.length() > THRUST_RANGE + 1.5) continue;

            target.hurt(ModDamageTypes.whip(player), (float) Config.magentaSpearDamage);
            Vec3 knockback = dir.scale(0.7).add(0, 0.05, 0);
            target.setDeltaMovement(target.getDeltaMovement().add(knockback));
            target.hurtMarked = true;
        }
    }
}
