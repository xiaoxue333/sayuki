package com.xiaoxue.sayuki.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class BakingMittens extends Item implements ICurioItem {
    public BakingMittens(Properties properties) { super(properties); }

    private static final String PKEY_MITTENS_LAST_WEAPON = "SayukiMittensLastWeapon";
    private static final String PKEY_MITTENS_STREAK = "SayukiMittensStreak";

    /**
     * Apply Baking Mittens effect on melee attack.
     * Returns the bonus damage to add.
     */
    public static float onAttack(Player player, ItemStack weapon) {
        if (weapon.isEmpty() || weapon.getMaxDamage() <= 0) return 0;

        ResourceLocation key = ForgeRegistries.ITEMS.getKey(weapon.getItem());
        String weaponId = key != null ? key.toString() : "";

        String lastWeapon = player.getPersistentData().getString(PKEY_MITTENS_LAST_WEAPON);
        int streak;
        if (!weaponId.equals(lastWeapon)) {
            streak = 1;
            player.getPersistentData().putString(PKEY_MITTENS_LAST_WEAPON, weaponId);
        } else {
            streak = player.getPersistentData().getInt(PKEY_MITTENS_STREAK) + 1;
        }
        player.getPersistentData().putInt(PKEY_MITTENS_STREAK, streak);

        // Apply durability damage
        weapon.hurtAndBreak(streak, player, p -> p.broadcastBreakEvent(p.getUsedItemHand()));

        return streak;
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.baking_mittens.1"));
    }
}
