package com.xiaoxue.sayuki.item;

import com.xiaoxue.sayuki.effect.ModEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class SneckoEye extends Item {

    public static final UUID SNECKO_SPEED_UUID = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901");

    public SneckoEye(Properties properties) {
        super(properties);
    }

    /**
     * Apply Confused Power to the player, randomizing attack speed between 0 and 3.
     */
    public static void applyConfused(LivingEntity entity) {
        // Random attack speed between 0.0 and 3.0
        double baseSpeed = 4.0;
        double delta = entity.level().getRandom().nextDouble() * 3.0 - baseSpeed;

        var attr = entity.getAttribute(Attributes.ATTACK_SPEED);
        if (attr != null) {
            attr.removeModifier(SNECKO_SPEED_UUID);
            attr.addTransientModifier(new AttributeModifier(SNECKO_SPEED_UUID,
                    "SneckoEye speed", delta, AttributeModifier.Operation.ADDITION));
        }

        // Apply confused effect for visual indicator (3s, refreshed each attack)
        entity.addEffect(new MobEffectInstance(ModEffects.CONFUSED_POWER.get(), 60, 0,
                false, false, true));
    }

    /**
     * Remove the SneckoEye attack speed modifier.
     */
    public static void removeConfusedModifier(LivingEntity entity) {
        var attr = entity.getAttribute(Attributes.ATTACK_SPEED);
        if (attr != null) {
            attr.removeModifier(SNECKO_SPEED_UUID);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.snecko_eye.1"));
        tooltip.add(Component.translatable("tooltip.sayuki.snecko_eye.2"));
        tooltip.add(Component.translatable("tooltip.sayuki.snecko_eye.3"));
    }
}
