package com.xiaoxue.sayuki.client;

import com.xiaoxue.sayuki.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

/**
 * Updates DynamicLightManager every client tick based on equipped light-emitting relics.
 */
@Mod.EventBusSubscriber(modid = "sayuki", value = Dist.CLIENT)
public class LightUpdateHandler {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) {
            DynamicLightManager.lightLevel = 0;
            return;
        }

        // Check Pumpkin Candle
        var candle = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.PUMPKIN_CANDLE.get()));
        if (candle.isPresent()) {
            DynamicLightManager.lightLevel = 15; // jack-o-lantern level
            DynamicLightManager.x = player.getX();
            DynamicLightManager.y = player.getY() + 1.0; // chest height
            DynamicLightManager.z = player.getZ();
        } else {
            DynamicLightManager.lightLevel = 0;
        }
    }
}
