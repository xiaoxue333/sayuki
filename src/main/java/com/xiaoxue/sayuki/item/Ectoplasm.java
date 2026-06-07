package com.xiaoxue.sayuki.item;

import com.xiaoxue.sayuki.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class Ectoplasm extends Item implements ICurioItem {

    public Ectoplasm(Properties properties) {
        super(properties);
    }

    /**
     * Scan player inventory (and optionally Sophisticated Backpacks) for emeralds/emerald blocks,
     * consume them and grant experience.
     */
    public static void consumeEmeralds(Player player) {
        boolean scanBackpacks = Config.ectoplasmSophisticatedBackpacks &&
                ModList.get().isLoaded("sophisticatedbackpacks");

        int totalXp = 0;

        // Scan main inventory + armor + offhand
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.is(Items.EMERALD)) {
                totalXp += stack.getCount() * 4;
                stack.setCount(0);
            } else if (stack.is(Items.EMERALD_BLOCK)) {
                totalXp += stack.getCount() * 36;
                stack.setCount(0);
            } else if (scanBackpacks && isSophisticatedBackpack(stack)) {
                totalXp += consumeEmeraldsFromBackpack(stack);
            }
        }

        if (totalXp > 0) {
            player.giveExperiencePoints(totalXp);
        }
    }

    private static int consumeEmeraldsFromBackpack(ItemStack backpack) {
        int total = 0;
        var cap = backpack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
        if (cap.isEmpty()) return 0;
        var handler = cap.get();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            if (stack.is(Items.EMERALD)) {
                total += stack.getCount() * 4;
                handler.extractItem(i, stack.getCount(), false);
            } else if (stack.is(Items.EMERALD_BLOCK)) {
                total += stack.getCount() * 36;
                handler.extractItem(i, stack.getCount(), false);
            }
        }
        return total;
    }

    private static boolean isSophisticatedBackpack(ItemStack stack) {
        return stack.getItem().getClass().getName().startsWith("net.p3pp3rf1y.sophisticatedbackpacks");
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.ectoplasm.1"));
    }
}
