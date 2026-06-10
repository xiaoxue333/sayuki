package com.xiaoxue.sayuki.mixin;

import com.xiaoxue.sayuki.enchantment.ModEnchantments;
import com.xiaoxue.sayuki.item.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.CuriosApi;

/**
 * Tezcatlipoca's Ember: cancel food exhaustion when player holds an
 * item enchanted with Tezcatlipoca's Ember in their main hand.
 * Pell Flesh: cancel food exhaustion when food level <= 1.
 */
@Mixin(Player.class)
public abstract class PlayerFoodExhaustionMixin {

    @Inject(method = "causeFoodExhaustion", at = @At("HEAD"), cancellable = true)
    private void sayuki$cancelExhaustionWithEmber(CallbackInfo ci) {
        Player self = (Player) (Object) this;

        // Tezcatlipoca's Ember: no exhaustion when holding enchanted weapon
        ItemStack mainHand = self.getMainHandItem();
        if (!mainHand.isEmpty() && EnchantmentHelper.getItemEnchantmentLevel(
                ModEnchantments.TEZCATLIPOCAS_EMBER.get(), mainHand) > 0) {
            ci.cancel();
            return;
        }

        // Pell Flesh: prevent hunger from dropping below 1
        if (self.getFoodData().getFoodLevel() <= 1) {
            var flesh = CuriosApi.getCuriosInventory(self).resolve().flatMap(handler ->
                    handler.findFirstCurio(stack -> stack.getItem() == ModItems.PELL_FLESH.get()));
            if (flesh.isPresent()) {
                ci.cancel();
            }
        }
    }
}
