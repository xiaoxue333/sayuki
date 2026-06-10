package com.xiaoxue.sayuki.mixin;

/**
 * ThreadLocal flag for Doom Tier 6: +50% trade price tax.
 * Lives outside any Mixin class to avoid non-private static member rules.
 * Set / cleared by ModEventHandler; read by MerchantOfferMixin inject callbacks.
 */
public final class DoomTradeTax {
    private DoomTradeTax() {}

    private static final ThreadLocal<Boolean> ENABLED = ThreadLocal.withInitial(() -> false);

    public static void enable() {
        ENABLED.set(true);
    }

    public static void disable() {
        ENABLED.remove();
    }

    public static boolean isEnabled() {
        return Boolean.TRUE.equals(ENABLED.get());
    }
}
