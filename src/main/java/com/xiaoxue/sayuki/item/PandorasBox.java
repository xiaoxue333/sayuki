package com.xiaoxue.sayuki.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class PandorasBox extends Item implements ICurioItem {

    public PandorasBox(Properties properties) {
        super(properties);
    }

    public static boolean isEquippedBy(Player player) {
        return CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.PANDORAS_BOX.get())).isPresent();
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.pandoras_box.1"));
    }
}
