package com.xiaoxue.sayuki.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class GoldenSeal extends Item implements ICurioItem {
    public GoldenSeal(Properties properties) { super(properties); }

    private static final float DISCOUNT = 0.05f;
    private static final String PKEY_DISCOUNTED = "SayukiGoldenSealDiscounted";

    /** Apply 5% discount to all merchant offers when player opens trade GUI. */
    public static void onOpenTrade(Player player, Merchant merchant) {
        if (player.level().isClientSide()) return;

        // Don't double-apply: track by merchant entity ID
        int merchantId = (merchant instanceof Entity entity) ? entity.getId() : merchant.hashCode();
        String discounted = player.getPersistentData().getString(PKEY_DISCOUNTED);
        String token = String.valueOf(merchantId);
        if (discounted.contains("," + token + ",") || discounted.equals(token)
                || discounted.startsWith(token + ",") || discounted.endsWith("," + token)) {
            return;
        }

        for (MerchantOffer offer : merchant.getOffers()) {
            int baseCost = offer.getBaseCostA().getCount();
            if (baseCost <= 1) continue;
            int discount = Math.max(1, Math.round(baseCost * DISCOUNT));
            offer.addToSpecialPriceDiff(-discount);
        }

        player.getPersistentData().putString(PKEY_DISCOUNTED,
                discounted.isEmpty() ? token : discounted + "," + token);
    }

    /** Clear discounted tracking when seal is unequipped. */
    public static void onUnequip(Player player) {
        player.getPersistentData().remove(PKEY_DISCOUNTED);
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.golden_seal.1"));
    }
}
