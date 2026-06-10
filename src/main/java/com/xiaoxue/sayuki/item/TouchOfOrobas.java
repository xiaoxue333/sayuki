package com.xiaoxue.sayuki.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TouchOfOrobas extends Item implements ICurioItem {
    public TouchOfOrobas(Properties properties) { super(properties); }

    /** Map of base relic → upgraded relic for anvil conversion. */
    private static final Map<Item, Item> UPGRADE_MAP = new LinkedHashMap<>();

    static {
        UPGRADE_MAP.put(ModItems.BURNING_BLOOD.get(), ModItems.BLACK_BLOOD.get());
        UPGRADE_MAP.put(ModItems.RING_OF_THE_SNAKE.get(), ModItems.RING_OF_THE_DRAKE.get());
        UPGRADE_MAP.put(ModItems.DIVINE_RIGHT.get(), ModItems.DIVINE_DESTINY.get());
        UPGRADE_MAP.put(ModItems.BOUND_PHYLACTERY.get(), ModItems.PHYLACTERY_UNBOUND.get());
        UPGRADE_MAP.put(ModItems.CRACKED_CORE.get(), ModItems.INFUSED_CORE.get());
    }

    /** Get the upgrade result for a base relic, or null if not in map. */
    public static Item getUpgradeResult(Item base) {
        return UPGRADE_MAP.get(base);
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.touch_of_orobas.1"));
        tooltip.add(Component.translatable("tooltip.sayuki.touch_of_orobas.2"));
        tooltip.add(Component.translatable("tooltip.sayuki.touch_of_orobas.3"));
        tooltip.add(Component.translatable("tooltip.sayuki.touch_of_orobas.4"));
    }
}
