package com.xiaoxue.sayuki.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class PellEye extends Item implements ICurioItem {
    public PellEye(Properties properties) { super(properties); }

    public static final String PKEY_PACIFIED_UNTIL = "SayukiPellEyePacifiedUntil";
    public static final int BASE_PACIFY_DURATION = 5 * 20; // 5 seconds

    /** Called when the player attacks a mob — cancels any active pacification. */
    public static void onPlayerAttack(Player player, LivingEntity target) {
        if (player.level().isClientSide()) return;
        if (!(target instanceof Mob mob)) return;
        mob.getPersistentData().remove(PKEY_PACIFIED_UNTIL);
    }

    /**
     * Try to pacify the attacker for the given duration in ticks.
     * Does nothing if already pacified.
     */
    public static void tryPacify(Player player, LivingEntity attacker, int pacifyDuration) {
        if (player.level().isClientSide()) return;
        if (!(attacker instanceof Mob mob)) return;

        if (mob.getPersistentData().contains(PKEY_PACIFIED_UNTIL)) return;

        long until = mob.level().getGameTime() + pacifyDuration;
        mob.getPersistentData().putLong(PKEY_PACIFIED_UNTIL, until);
        mob.setTarget(null);
        mob.setLastHurtByMob(null);
    }

    /** Convenience overload using the base 5s duration. */
    public static void tryPacify(Player player, LivingEntity attacker) {
        tryPacify(player, attacker, BASE_PACIFY_DURATION);
    }

    /** Call every tick for each mob — keep clearing the target while pacified. */
    public static void onMobTick(Mob mob) {
        if (!mob.getPersistentData().contains(PKEY_PACIFIED_UNTIL)) return;
        long until = mob.getPersistentData().getLong(PKEY_PACIFIED_UNTIL);

        if (mob.level().getGameTime() < until) {
            if (mob.getTarget() != null) {
                mob.setTarget(null);
            }
        } else {
            mob.getPersistentData().remove(PKEY_PACIFIED_UNTIL);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.pell_eye.1"));
    }
}
