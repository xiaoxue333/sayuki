package com.xiaoxue.sayuki.item;

import com.google.common.collect.Sets;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.Set;

public class MeatCleaver extends Item implements ICurioItem {

    public static final ToolAction KNIFE_DIG = ToolAction.get("knife_dig");
    public static final ToolAction KNIFE_HARVEST = ToolAction.get("knife_harvest");
    private static final Set<ToolAction> KNIFE_ACTIONS = Sets.newHashSet(
            ToolActions.SHEARS_CARVE, ToolActions.SWORD_DIG, KNIFE_DIG, KNIFE_HARVEST);

    public MeatCleaver(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return KNIFE_ACTIONS.contains(toolAction);
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.meat_cleaver.1"));
    }
}
