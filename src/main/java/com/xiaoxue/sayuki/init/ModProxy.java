/**
 * Sayuki — Proxy interface (ClientProxy / CommonProxy, DistExecutor-safe)
 * Compat: Goety-2 — uses similar ModProxy pattern, independent implementation
 * Compat: IronsSpellbooks — uses direct approach, no proxy conflict
 */
package com.xiaoxue.sayuki.init;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public interface ModProxy {

    @Nullable
    Player getPlayer();

    @Nullable
    Level getLevel();

    default void init() {
    }

    default void clientInit() {
    }
}
