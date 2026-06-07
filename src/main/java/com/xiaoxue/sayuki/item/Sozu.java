package com.xiaoxue.sayuki.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;

public class Sozu extends Item implements ICurioItem {
    public Sozu(Properties properties) {
        super(properties);
    }

    public static boolean isEquippedBy(LivingEntity entity) {
        if (!(entity instanceof Player player)) return false;
        return CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.SOZU.get())).isPresent();
    }

    /**
     * Remove all active potion effects from the entity. Called on equip and via event interception.
     */
    public static void removeAllEffects(LivingEntity entity) {
        List<MobEffectInstance> effects = new ArrayList<>(entity.getActiveEffects());
        for (MobEffectInstance effect : effects) {
            entity.removeEffect(effect.getEffect());
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.sozu.1"));
    }
}
