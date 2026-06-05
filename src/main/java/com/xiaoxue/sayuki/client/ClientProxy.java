/**
 * Sayuki — Client-side proxy (has access to Minecraft.getInstance())
 * Compat: Goety-2 and IronsSpellbooks — safe via DistExecutor, no conflict
 */
package com.xiaoxue.sayuki.client;

import com.xiaoxue.sayuki.init.ModProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class ClientProxy implements ModProxy {

    @Nullable
    @Override
    public Player getPlayer() {
        return Minecraft.getInstance().player;
    }

    @Nullable
    @Override
    public Level getLevel() {
        return Minecraft.getInstance().level;
    }
}
