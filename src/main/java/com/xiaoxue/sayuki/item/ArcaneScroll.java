package com.xiaoxue.sayuki.item;

import com.xiaoxue.sayuki.compat.IronSpellsCompat;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;

public class ArcaneScroll extends Item implements ICurioItem {
    public ArcaneScroll(Properties properties) { super(properties); }

    private static List<Item> rareScrollItems;
    private static long cachedServerHash;

    /** Right-click consumes the ArcaneScroll and gives a random rare ISS spell scroll. */
    public static void onUse(Player player, ItemStack stack) {
        if (!IronSpellsCompat.isLoaded()) return;
        stack.shrink(1);
        Item randomScroll = getRandomRareScroll();
        if (randomScroll != null) {
            ItemStack reward = new ItemStack(randomScroll);
            if (!player.getInventory().add(reward)) {
                player.drop(reward, false);
            }
        }
    }

    private static Item getRandomRareScroll() {
        long serverHash = ForgeRegistries.ITEMS.getKeys().hashCode();
        if (rareScrollItems == null || cachedServerHash != serverHash) {
            cachedServerHash = serverHash;
            rareScrollItems = new ArrayList<>();
            for (ResourceLocation key : ForgeRegistries.ITEMS.getKeys()) {
                if (!key.getNamespace().equals("irons_spellbooks")) continue;
                if (!key.getPath().contains("scroll")) continue;
                Item item = ForgeRegistries.ITEMS.getValue(key);
                if (item == null) continue;
                // Only rare+ scrolls
                Rarity rarity = new ItemStack(item).getRarity();
                if (rarity == Rarity.RARE || rarity == Rarity.EPIC) {
                    rareScrollItems.add(item);
                }
            }
        }
        if (rareScrollItems.isEmpty()) return null;
        return rareScrollItems.get(net.minecraft.util.RandomSource.create().nextInt(rareScrollItems.size()));
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.arcane_scroll.1"));
    }
}
