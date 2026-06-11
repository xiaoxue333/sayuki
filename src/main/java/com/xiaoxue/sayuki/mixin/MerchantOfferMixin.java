package com.xiaoxue.sayuki.mixin;

import com.xiaoxue.sayuki.handler.DoomTradeTaxHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Doom Tier 6: all trade prices +50%.
 * Uses {@link DoomTradeTaxHandler} (non-mixin class) to avoid
 * Mixin static-member visibility rules.
 */
@Mixin(MerchantOffer.class)
public abstract class MerchantOfferMixin {

    @Inject(method = "getCostA()Lnet/minecraft/world/item/ItemStack;",
            at = @At("RETURN"), cancellable = true)
    private void sayuki$inflateCostA(CallbackInfoReturnable<ItemStack> cir) {
        if (DoomTradeTaxHandler.isEnabled()) {
            ItemStack original = cir.getReturnValue();
            if (!original.isEmpty() && original.getCount() > 0) {
                int newCount = Math.max(1, (int) (original.getCount() * 1.5));
                if (newCount != original.getCount()) {
                    ItemStack modified = original.copy();
                    modified.setCount(newCount);
                    cir.setReturnValue(modified);
                }
            }
        }
    }

    @Inject(method = "getCostB()Lnet/minecraft/world/item/ItemStack;",
            at = @At("RETURN"), cancellable = true)
    private void sayuki$inflateCostB(CallbackInfoReturnable<ItemStack> cir) {
        if (DoomTradeTaxHandler.isEnabled()) {
            ItemStack original = cir.getReturnValue();
            if (!original.isEmpty() && original.getCount() > 0) {
                int newCount = Math.max(1, (int) (original.getCount() * 1.5));
                if (newCount != original.getCount()) {
                    ItemStack modified = original.copy();
                    modified.setCount(newCount);
                    cir.setReturnValue(modified);
                }
            }
        }
    }
}
