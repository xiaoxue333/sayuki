/**
 * Sayuki — Main mod class, MODID: sayuki, namespace: sayuki
 * Compat: Goety-2(MODID:goety) — no conflict, separate namespace
 * Compat: IronsSpellbooks(MODID:irons_spellbooks) — no conflict, separate namespace
 */
package com.xiaoxue.sayuki;

import com.mojang.logging.LogUtils;
import com.xiaoxue.sayuki.block.ModBlocks;
import com.xiaoxue.sayuki.client.ClientProxy;
import com.xiaoxue.sayuki.effect.ModEffects;
import com.xiaoxue.sayuki.entity.ModEntities;
import com.xiaoxue.sayuki.init.CommonProxy;
import com.xiaoxue.sayuki.init.ModProxy;
import com.xiaoxue.sayuki.item.ModCreativeModeTab;
import com.xiaoxue.sayuki.item.ModItems;
import com.xiaoxue.sayuki.network.ModNetwork;
import com.xiaoxue.sayuki.recipe.ModRecipes;
import com.xiaoxue.sayuki.worldgen.ModStructures;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

@Mod(Sayuki.MOD_ID)
public class Sayuki {
    public static final String MOD_ID = "sayuki";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static ModProxy PROXY = net.minecraftforge.fml.DistExecutor.unsafeRunForDist(
            () -> ClientProxy::new,
            () -> CommonProxy::new
    );

    public Sayuki(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEntities.register(modEventBus);
        ModCreativeModeTab.register(modEventBus);
        ModStructures.register(modEventBus);
        ModEffects.register(modEventBus);
        ModRecipes.register(modEventBus);

        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Sayuki common setup");

        ModNetwork.init();

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(net.minecraft.world.level.block.Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.STELLAR_JADE.get());
            event.accept(ModItems.RAW_STELLAR_JADE.get());
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Sayuki server starting");
    }
}
