/**
 * Sayuki — Client-side Forge EVENT_BUS subscribers
 */
package com.xiaoxue.sayuki.client;

import com.mojang.datafixers.util.Either;
import com.xiaoxue.sayuki.Sayuki;
import com.xiaoxue.sayuki.item.GuMu;
import com.xiaoxue.sayuki.item.ModItems;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
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
}
