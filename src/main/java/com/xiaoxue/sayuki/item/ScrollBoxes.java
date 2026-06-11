package com.xiaoxue.sayuki.item;

import com.xiaoxue.sayuki.compat.IronSpellsCompat;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;

public class ScrollBoxes extends Item implements ICurioItem {
    private static List<Item> allScrollItems;
    private static long cachedServerHash;

    public ScrollBoxes(Properties properties) { super(properties); }

    /** Right-click consumes the ScrollBoxes and gives 3 random spell scrolls of any rarity. */
    public static void onUse(Player player, ItemStack stack) {
        if (!IronSpellsCompat.isLoaded()) return;
        stack.shrink(1);
        List<Item> scrolls = getAllScrolls();
        if (scrolls.isEmpty()) return;
        var rand = net.minecraft.util.RandomSource.create();
        for (int i = 0; i < 3; i++) {
            Item randomScroll = scrolls.get(rand.nextInt(scrolls.size()));
            ItemStack reward = new ItemStack(randomScroll);
            if (!player.getInventory().add(reward)) {
                player.drop(reward, false);
            }
        }
    }

    private static List<Item> getAllScrolls() {
        long serverHash = ForgeRegistries.ITEMS.getKeys().hashCode();
        if (allScrollItems == null || cachedServerHash != serverHash) {
            cachedServerHash = serverHash;
            allScrollItems = new ArrayList<>();
            for (ResourceLocation key : ForgeRegistries.ITEMS.getKeys()) {
                if (!key.getNamespace().equals("irons_spellbooks")) continue;
                if (!key.getPath().contains("scroll")) continue;
                Item item = ForgeRegistries.ITEMS.getValue(key);
                if (item != null) allScrollItems.add(item);
            }
        }
        return allScrollItems;
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.scroll_boxes.1"));
    }
}
