package com.xiaoxue.sayuki.handler;

/**
 * ThreadLocal flag for Doom Tier 6: +50% trade price tax.
 * Lives outside the mixin package to avoid non-mixin classes
 * depending on mixin-package types.
 * <p>
 * Set / cleared by {@link ModEventHandler}; read by
 * {@link com.xiaoxue.sayuki.mixin.MerchantOfferMixin} inject callbacks.
 */
public final class DoomTradeTaxHandler {
    private DoomTradeTaxHandler() {}

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
