/**
 * Sayuki — Client-side MOD-bus events (renderer registration, client setup)
 * Compat: Goety-2 and IronsSpellbooks — independent client subscriber, no conflict
 */
package com.xiaoxue.sayuki.client;

import com.mojang.logging.LogUtils;
import com.xiaoxue.sayuki.Sayuki;
import com.xiaoxue.sayuki.entity.ModEntities;
import com.xiaoxue.sayuki.entity.StellarJadeProjectileRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = Sayuki.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        LOGGER.info("HELLO FROM CLIENT SETUP");
        LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.STELLAR_JADE_PROJECTILE.get(), StellarJadeProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.HEART_GRENADE_PROJECTILE.get(), ThrownItemRenderer::new);
    }
}
