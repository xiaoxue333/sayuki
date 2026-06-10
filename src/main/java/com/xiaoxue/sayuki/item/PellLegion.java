package com.xiaoxue.sayuki.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class PellLegion extends Item implements ICurioItem {
    public PellLegion(Properties properties) { super(properties); }

    private static final String PKEY_LEGION_DOUBLE_NEXT = "SayukiPellLegionDoubleNext";

    /**
     * Called in player tick. Every 2 seconds, sets a flag so the next block gain is doubled.
     */
    public static void onPlayerTick(Player player) {
        long now = player.level().getGameTime();
        if (now % 40 != 0) return; // every 2 seconds
        player.getPersistentData().putBoolean(PKEY_LEGION_DOUBLE_NEXT, true);
    }

    /**
     * Wrap block gain: if Pell's Legion double flag is active, double the amount and clear flag.
     * Call this instead of directly adding to GalacticDustBlock.
     */
    public static int tryDoubleBlock(Player player, int amount) {
        if (amount <= 0) return 0;
        if (player.getPersistentData().getBoolean(PKEY_LEGION_DOUBLE_NEXT)) {
            player.getPersistentData().remove(PKEY_LEGION_DOUBLE_NEXT);
            return amount * 2;
        }
        return amount;
    }

    /**
     * Convenience: add block to player, with Pell's Legion doubling applied.
     */
    public static void addBlock(Player player, int amount, String pkeyBlock) {
        int gained = tryDoubleBlock(player, amount);
        if (gained > 0) {
            int current = player.getPersistentData().getInt(pkeyBlock);
            player.getPersistentData().putInt(pkeyBlock, current + gained);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.pell_legion.1"));
    }
}
