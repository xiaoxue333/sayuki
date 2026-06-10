package com.xiaoxue.sayuki.item;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class BigHug extends Item implements ICurioItem {
    public BigHug(Properties properties) { super(properties); }

    private static final String KEY_MARKED_ITEM = "SayukiMarkedItem";
    private static final String PKEY_BIG_HUG_COUNT = "SayukiBigHugCount";

    /**
     * Get the marked item from the BigHug stack, or empty string if none.
     */
    public static String getMarkedItemId(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_MARKED_ITEM)) {
            return tag.getString(KEY_MARKED_ITEM);
        }
        return "";
    }

    /**
     * Check if the given Item matches the mark on this BigHug.
     */
    public static boolean isMarkedItem(ItemStack bigHug, Item item) {
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
        return key != null && key.toString().equals(getMarkedItemId(bigHug));
    }

    /**
     * Called when a player with BigHug equipped picks up an item.
     * Returns true if the item was consumed (pickup should be cancelled).
     */
    public static boolean tryConsumePickup(Player player, ItemEntity itemEntity) {
        ItemStack pickup = itemEntity.getItem();
        if (pickup.isEmpty()) return false;

        var result = CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() instanceof BigHug
                        && isMarkedItem(stack, pickup.getItem())));
        if (result.isEmpty()) return false;

        int count = pickup.getCount();
        int current = player.getPersistentData().getInt(PKEY_BIG_HUG_COUNT);
        int total = current + count;
        int coal = total / 4;
        int remainder = total % 4;
        player.getPersistentData().putInt(PKEY_BIG_HUG_COUNT, remainder);

        if (coal > 0) {
            BlockPos pos = player.blockPosition();
            ItemEntity coalDrop = new ItemEntity(player.level(),
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    new ItemStack(Items.COAL, coal));
            coalDrop.setDefaultPickUpDelay();
            player.level().addFreshEntity(coalDrop);
        }

        itemEntity.discard();
        return true;
    }

    /**
     * Check if the BigHug is already marked (has a mark set).
     */
    public static boolean hasMark(ItemStack stack) {
        return !getMarkedItemId(stack).isEmpty();
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        String marked = getMarkedItemId(stack);
        if (!marked.isEmpty()) {
            int count = 0;
            // Try to get count if we have a player context (won't work in all contexts)
            tooltip.add(Component.translatable("tooltip.sayuki.big_hug.marked", marked));
        }
        tooltip.add(Component.translatable("tooltip.sayuki.big_hug.1"));
    }
}
