/**
 * Sayuki — Client-side Forge EVENT_BUS subscribers
 */
package com.xiaoxue.sayuki.client;

import com.mojang.datafixers.util.Either;
import com.xiaoxue.sayuki.Sayuki;
import com.xiaoxue.sayuki.item.GuMu;
import com.xiaoxue.sayuki.item.ModItems;
import com.xiaoxue.sayuki.item.WingedBoots;
import com.xiaoxue.sayuki.network.C2SMidAirJumpPacket;
import com.xiaoxue.sayuki.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Sayuki.MOD_ID, value = Dist.CLIENT)
public class ClientForgeEventHandler {

    @SubscribeEvent
    public static void onGatherTooltipBlightIcons(RenderTooltipEvent.GatherComponents event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() != ModItems.GU_MU.get()) return;

        List<String> blightIds = GuMu.getBlights(stack);
        if (blightIds.isEmpty()) return;

        // Aggregate counts
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String id : blightIds) {
            counts.merge(id, 1, Integer::sum);
        }

        // Add icon tooltip components after the header
        List<Either<FormattedText, TooltipComponent>> elements = event.getTooltipElements();
        for (var entry : counts.entrySet()) {
            elements.add(Either.right(new BlightIconTooltipData(entry.getKey(), entry.getValue())));
        }
    }

    // === Winged Boots: client detects mid-air jump press → sends packet ===

    @SubscribeEvent
    public static void onClientTickWingedBoots(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.player.onGround()) return;
        if (!mc.options.keyJump.isDown()) return;

        // Only trigger once per press
        if (mc.player.getPersistentData().getBoolean("SayukiWingedBootsJumpDown")) return;
        mc.player.getPersistentData().putBoolean("SayukiWingedBootsJumpDown", true);

        // Check if equipped
        var boots = top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(mc.player).resolve()
                .flatMap(handler -> handler.findFirstCurio(stack ->
                        stack.getItem() == ModItems.WINGED_BOOTS.get()));
        if (boots.isEmpty()) return;

        ModNetwork.sendToServer(new C2SMidAirJumpPacket());
    }

    @SubscribeEvent
    public static void onClientTickWingedBootsRelease(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.options.keyJump.isDown()) return;
        mc.player.getPersistentData().putBoolean("SayukiWingedBootsJumpDown", false);
    }
}
