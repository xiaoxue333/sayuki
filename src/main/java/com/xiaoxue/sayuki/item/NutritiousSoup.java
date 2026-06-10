package com.xiaoxue.sayuki.item;

import com.xiaoxue.sayuki.compat.TetraCompat;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import com.xiaoxue.sayuki.enchantment.ModEnchantments;

import java.util.List;

public class NutritiousSoup extends Item implements ICurioItem {
    public NutritiousSoup(Properties properties) { super(properties); }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide() && entity instanceof Player player) {
            // Restore hunger and saturation
            FoodData food = player.getFoodData();
            food.setFoodLevel(Math.min(20, food.getFoodLevel() + 4));
            food.setSaturation(Math.min(food.getFoodLevel(), food.getSaturationLevel() + 4));

            // Enchant all swords in inventory (including Tetra swords)
            var ember = ModEnchantments.TEZCATLIPOCAS_EMBER.get();
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (!item.isEmpty() && (item.getItem() instanceof SwordItem || TetraCompat.isMelee(item))
                        && EnchantmentHelper.getItemEnchantmentLevel(ember, item) == 0) {
                    item.enchant(ember, 1);
                }
            }
        }
        return stack; // not consumed
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.nutritious_soup.1"));
    }
}
