package com.xiaoxue.sayuki.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class EmptyCage extends Item implements ICurioItem {

    private static final String TAG_ABSORBED = "SayukiEmptyCageAbsorbed";
    private static final String TAG_STORED = "SayukiEmptyCageStored";
    private static final int MAX_CURSES = 2;

    public EmptyCage(Properties properties) {
        super(properties);
    }

    /**
     * Called from ModEventHandler.onCurioChange when this item is equipped.
     * Scans all player items for curse enchantments, removes up to 2, stores them.
     */
    public static void tryAbsorbCurses(LivingEntity entity, ItemStack cageStack) {
        if (cageStack.isEmpty() || cageStack.getItem() != ModItems.EMPTY_CAGE.get()) return;
        CompoundTag tag = cageStack.getOrCreateTag();
        if (tag.getBoolean(TAG_ABSORBED)) return; // already absorbed

        List<CurseEntry> curses = collectPlayerCurses(entity);
        if (curses.isEmpty()) return;

        // Prioritize higher-level curses, pick up to MAX_CURSES
        curses.sort(Comparator.comparingInt(CurseEntry::level).reversed());
        int toAbsorb = Math.min(MAX_CURSES, curses.size());
        List<CurseEntry> selected = curses.subList(0, toAbsorb);

        // Remove from source items
        for (CurseEntry entry : selected) {
            Map<Enchantment, Integer> enchMap = entry.stack().getAllEnchantments();
            enchMap.remove(entry.enchantment());
            EnchantmentHelper.setEnchantments(enchMap, entry.stack());
        }

        // Store on cage
        ListTag list = new ListTag();
        for (CurseEntry entry : selected) {
            CompoundTag entryTag = new CompoundTag();
            ResourceLocation id = ForgeRegistries.ENCHANTMENTS.getKey(entry.enchantment());
            if (id != null) {
                entryTag.putString("id", id.toString());
                entryTag.putInt("lvl", entry.level());
                list.add(entryTag);
            }
        }
        tag.put(TAG_STORED, list);
        tag.putBoolean(TAG_ABSORBED, true);
    }

    /**
     * Gather all curse enchantments from the player's equipment and curios.
     */
    private static List<CurseEntry> collectPlayerCurses(LivingEntity entity) {
        List<CurseEntry> results = new ArrayList<>();

        // Armor + hands
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.ARMOR && slot != EquipmentSlot.MAINHAND && slot != EquipmentSlot.OFFHAND)
                continue;
            ItemStack stack = entity.getItemBySlot(slot);
            collectCursesFromStack(stack, results);
        }

        // Curios
        if (entity instanceof Player player) {
            CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
                handler.getCurios().values().forEach(stacksHandler -> {
                    for (int i = 0; i < stacksHandler.getSlots(); i++) {
                        collectCursesFromStack(stacksHandler.getStacks().getStackInSlot(i), results);
                    }
                });
            });
        }

        return results;
    }

    private static void collectCursesFromStack(ItemStack stack, List<CurseEntry> out) {
        if (stack.isEmpty()) return;
        for (Map.Entry<Enchantment, Integer> entry : stack.getAllEnchantments().entrySet()) {
            if (entry.getKey().isCurse()) {
                out.add(new CurseEntry(stack, entry.getKey(), entry.getValue()));
            }
        }
    }

    /**
     * Check if a cage has already absorbed curses.
     */
    public static boolean isAbsorbed(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(TAG_ABSORBED);
    }

    /**
     * Get the stored enchantments as display text.
     */
    public static List<Component> getStoredEnchantmentText(ItemStack stack) {
        List<Component> lines = new ArrayList<>();
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TAG_STORED)) return lines;

        ListTag list = tag.getList(TAG_STORED, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            String id = entry.getString("id");
            int lvl = entry.getInt("lvl");
            Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryParse(id));
            if (enchantment != null) {
                lines.add(Component.literal(" §7- ").append(enchantment.getFullname(lvl)));
            }
        }
        return lines;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        if (isAbsorbed(stack)) {
            tooltip.add(Component.translatable("tooltip.sayuki.empty_cage.1"));
            tooltip.addAll(getStoredEnchantmentText(stack));
        } else {
            tooltip.add(Component.translatable("tooltip.sayuki.empty_cage.2"));
        }
    }

    private record CurseEntry(ItemStack stack, Enchantment enchantment, int level) {}
}
