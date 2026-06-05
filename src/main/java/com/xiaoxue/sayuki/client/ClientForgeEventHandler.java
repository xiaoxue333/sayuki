/**
 * Sayuki — Client-side Forge EVENT_BUS subscribers
 */
package com.xiaoxue.sayuki.client;

import com.xiaoxue.sayuki.Sayuki;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Sayuki.MOD_ID, value = Dist.CLIENT)
public class ClientForgeEventHandler {
}
