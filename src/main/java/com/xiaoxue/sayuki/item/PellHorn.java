package com.xiaoxue.sayuki.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class PellHorn extends Item implements ICurioItem {
    public PellHorn(Properties properties) { super(properties); }

    public static final int HORN_PACIFY_DURATION = 10 * 20; // 10 seconds (base 5 + horn 5)
    private static final String PKEY_HORN_DMG_REDUCED = "SayukiPellHornDmgReduced";

    /**
     * Mark a mob so its next hit on the player deals -15 damage.
     * Called when pacification starts with PellHorn equipped.
     */
    public static void markReducedDamage(Mob mob) {
        mob.getPersistentData().putBoolean(PKEY_HORN_DMG_REDUCED, true);
    }

    /**
     * Check if a mob has the PellHorn damage reduction pending, consume it, and apply.
     * Returns how much to reduce the incoming damage.
     */
    public static float applyDamageReduction(LivingEntity attacker) {
        if (!(attacker instanceof Mob mob)) return 0;
        if (!mob.getPersistentData().contains(PKEY_HORN_DMG_REDUCED)) return 0;
        mob.getPersistentData().remove(PKEY_HORN_DMG_REDUCED);
        return 15;
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.pell_horn.1"));
    }
}
