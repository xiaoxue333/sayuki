package com.xiaoxue.sayuki.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LostCoffer extends Item implements ICurioItem {
    public LostCoffer(Properties properties) { super(properties); }

    /** Right-click: consume → random potion effect + 2 iron ingots. */
    public static void onUse(Player player, ItemStack stack) {
        stack.shrink(1);

        List<MobEffect> beneficial = new ArrayList<>();
        for (MobEffect effect : ForgeRegistries.MOB_EFFECTS) {
            if (effect.isBeneficial()) beneficial.add(effect);
        }
        if (!beneficial.isEmpty()) {
            Collections.shuffle(beneficial, new Random());
            int level = player.getRandom().nextInt(2);
            player.addEffect(new MobEffectInstance(beneficial.get(0), 10 * 20, level));
        }

        ItemStack iron = new ItemStack(Items.IRON_INGOT, 2);
        if (!player.getInventory().add(iron)) {
            player.drop(iron, false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.lost_coffer.1"));
    }
}
