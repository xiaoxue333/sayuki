/**
 * Sayuki — Data Disk (Curios relic slot item, ISS all-spell-power +0.1, core/virus damage bonus)
 */
package com.xiaoxue.sayuki.item;

import com.xiaoxue.sayuki.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class DataDisk extends Item implements ICurioItem {

    public DataDisk(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.data_disk.1"));
        tooltip.add(Component.translatable("tooltip.sayuki.data_disk.2", Config.dataDiskCoreBonus));
    }
}
