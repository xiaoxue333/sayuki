package com.xiaoxue.sayuki.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.Random;

public class PandorasBox extends Item implements ICurioItem {

    private static final double TARGET_VALUE = 6.0;
    private static final double MIN_VALUE = 0.0;
    private static final double MAX_VALUE = 12.0;
    private static final double EPSILON = 1e-6;

    public PandorasBox(Properties properties) {
        super(properties);
    }

    public static boolean isEquippedBy(Player player) {
        return CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.PANDORAS_BOX.get())).isPresent();
    }

    /**
     * Scan all player attributes. Any attribute with value == 6 is randomized to 0~12.
     */
    public static void randomizeSixes(Player player) {
        if (player.level().isClientSide()) return;
        Random rng = new Random();

        for (Attribute attr : ForgeRegistries.ATTRIBUTES) {
            AttributeInstance inst = player.getAttribute(attr);
            if (inst == null) continue;
            if (Math.abs(inst.getValue() - TARGET_VALUE) < EPSILON) {
                double newVal = MIN_VALUE + rng.nextDouble() * (MAX_VALUE - MIN_VALUE);
                inst.setBaseValue(newVal);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.pandoras_box.1"));
    }
}
