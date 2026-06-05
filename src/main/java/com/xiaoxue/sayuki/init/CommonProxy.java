/**
 * Sayuki — Server-safe proxy (returns null for client-only calls)
 * Compat: Goety-2 and IronsSpellbooks — server-safe, no conflict
 */
package com.xiaoxue.sayuki.init;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class CommonProxy implements ModProxy {

    @Nullable
    @Override
    public Player getPlayer() {
        return null;
    }

    @Nullable
    @Override
    public Level getLevel() {
        return null;
    }
}
