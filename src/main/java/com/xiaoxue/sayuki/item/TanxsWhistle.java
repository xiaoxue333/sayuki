package com.xiaoxue.sayuki.item;

import com.xiaoxue.sayuki.Config;
import com.xiaoxue.sayuki.effect.ModEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.Random;

public class TanxsWhistle extends Item implements ICurioItem {
    public TanxsWhistle(Properties properties) {
        super(properties);
    }

    private static final String PKEY_CHANCE = "SayukiTanxsWhistleChance";
    private static final String PKEY_UNSTUN_TICK = "SayukiJewelryBoxUnstun";
    private static final int BASE_CHANCE = 1; // 1%
    public static final int STUN_DURATION = 60; // 3s

    /**
     * Attempt to stun target. Each call increases probability by 1% on miss.
     * Does NOT stack duration if target already has the effect.
     */
    public static void tryStun(LivingEntity attacker, LivingEntity target) {
        int chance = attacker.getPersistentData().getInt(PKEY_CHANCE);
        if (chance <= 0) chance = BASE_CHANCE;

        if (new Random().nextInt(100) < chance) {
            if (!target.hasEffect(ModEffects.JEWELRY_BOX.get())) {
                target.addEffect(new MobEffectInstance(ModEffects.JEWELRY_BOX.get(), STUN_DURATION, 0,
                        false, false, true));
            }
            if (target instanceof Mob mob) {
                mob.getPersistentData().putLong(PKEY_UNSTUN_TICK, target.level().getGameTime() + STUN_DURATION);
                mob.setNoAi(true);
            }
            attacker.getPersistentData().putInt(PKEY_CHANCE, BASE_CHANCE);
        } else {
            attacker.getPersistentData().putInt(PKEY_CHANCE, chance + 1);
        }
    }

    /**
     * Area stun: stun all nearby hostiles and play a sharp sound.
     */
    public static void areaStun(LivingEntity user, double radius) {
        Level level = user.level();
        if (level.isClientSide()) return;

        AABB box = new AABB(user.blockPosition()).inflate(radius);
        List<Mob> monsters = level.getEntitiesOfClass(Mob.class, box, m -> m.isAlive());
        for (Mob mob : monsters) {
            if (!mob.hasEffect(ModEffects.JEWELRY_BOX.get())) {
                mob.addEffect(new MobEffectInstance(ModEffects.JEWELRY_BOX.get(), STUN_DURATION, 0,
                        false, false, true));
            }
            mob.getPersistentData().putLong(PKEY_UNSTUN_TICK, level.getGameTime() + STUN_DURATION);
            mob.setNoAi(true);
        }
        level.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0F, 1.5F);
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.tanxs_whistle.1"));
        tooltip.add(Component.translatable("tooltip.sayuki.tanxs_whistle.2", Config.tanxsWhistleAreaRadius,
                Config.tanxsWhistleAreaCooldownSeconds));
    }
}
