/**
 * Sayuki — Mixin: ItemInHandRenderer#renderArmWithItem (spear thrust animation pushback)
 * Compat: Goety-2 — may also mixin ItemInHandRenderer; check injection point head/tail ordering
 * Compat: IronsSpellbooks — may also mixin same class; verify no priority clash
 */
package com.xiaoxue.sayuki.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.xiaoxue.sayuki.item.MagentaSpearItem;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

    private static final int ANIM_DURATION = 8;

    @Inject(method = "renderArmWithItem", at = @At("HEAD"))
    private void onRenderArmWithItem(
            AbstractClientPlayer player, float partialTick, float pitch,
            InteractionHand hand, float swingProgress, ItemStack stack,
            float equippedProgress, PoseStack poseStack,
            MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {

        if (!(stack.getItem() instanceof MagentaSpearItem)) return;
        if (!stack.hasTag()) return;

        long thrustTick = stack.getTag().getLong("SayukiSpearThrustTick");
        if (thrustTick == 0) return;

        long elapsed = player.level().getGameTime() - thrustTick;
        if (elapsed < 0 || elapsed >= ANIM_DURATION) return;

        float elapsedF = elapsed + partialTick;
        float progress = elapsedF / ANIM_DURATION;
        float push = (float) Math.sin(progress * Math.PI);

        poseStack.translate(0.0, 0.0, -push * 1.2);
    }
}
