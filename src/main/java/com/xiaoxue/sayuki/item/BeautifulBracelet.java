package com.xiaoxue.sayuki.item;

import com.xiaoxue.sayuki.compat.GoetyCompat;
import com.xiaoxue.sayuki.compat.IronSpellsCompat;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class BeautifulBracelet extends Item implements ICurioItem {
    public BeautifulBracelet(Properties properties) {
        super(properties.durability(3).setNoRepair());
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    /**
     * Check if this item can be used as a Swiftness enchantment source in the anvil.
     * Valid targets: bows, crossbows, ISS staves, Goety focuses.
     */
    public static boolean canApplySwift(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof BowItem || item instanceof CrossbowItem) return true;
        if (IronSpellsCompat.isLoaded() && IronSpellsCompat.isStaff(item)) return true;
        if (GoetyCompat.isLoaded() && GoetyCompat.isFocus(item)) return true;
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.beautiful_bracelet.1"));
    }
}
