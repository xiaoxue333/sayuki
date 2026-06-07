package com.xiaoxue.sayuki.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

import java.util.*;

public class Astrolabe extends Item implements ICurioItem {

    private static final String PKEY_ATTRIBUTES = "SayukiAstrolabeAttributes";
    private static final int ATTRIBUTE_COUNT = 3;
    private static final double BASE_VALUE = 1.0;

    public Astrolabe(Properties properties) {
        super(properties);
    }

    public static boolean isEquippedBy(Player player) {
        return CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.ASTROLABE.get())).isPresent();
    }

    /**
     * Randomly pick 3 attributes the player doesn't already have, set base value to 1, store IDs.
     */
    public static void onEquip(Player player) {
        if (player.level().isClientSide()) return;

        // Gather all registered attributes the player does NOT already have
        List<ResourceLocation> candidates = new ArrayList<>();
        for (Attribute attr : ForgeRegistries.ATTRIBUTES) {
            if (player.getAttribute(attr) == null) {
                ResourceLocation id = ForgeRegistries.ATTRIBUTES.getKey(attr);
                if (id != null) {
                    candidates.add(id);
                }
            }
        }

        if (candidates.isEmpty()) return;

        // Shuffle and pick up to ATTRIBUTE_COUNT
        Collections.shuffle(candidates, new Random());
        int count = Math.min(ATTRIBUTE_COUNT, candidates.size());
        List<ResourceLocation> chosen = candidates.subList(0, count);

        // Apply
        StringBuilder sb = new StringBuilder();
        for (ResourceLocation id : chosen) {
            Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(id);
            if (attr == null) continue;
            AttributeInstance inst = player.getAttribute(attr);
            if (inst != null) {
                inst.setBaseValue(BASE_VALUE);
            }
            if (sb.length() > 0) sb.append(",");
            sb.append(id);
        }

        player.getPersistentData().putString(PKEY_ATTRIBUTES, sb.toString());
    }

    /**
     * Remove the stored attributes from the player (reset base value to 0).
     */
    public static void onUnequip(Player player) {
        if (player.level().isClientSide()) return;

        String stored = player.getPersistentData().getString(PKEY_ATTRIBUTES);
        if (stored.isEmpty()) return;

        for (String idStr : stored.split(",")) {
            ResourceLocation id = ResourceLocation.tryParse(idStr);
            if (id == null) continue;
            Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(id);
            if (attr == null) continue;
            AttributeInstance inst = player.getAttribute(attr);
            if (inst != null) {
                inst.setBaseValue(inst.getAttribute().getDefaultValue());
            }
        }

        player.getPersistentData().remove(PKEY_ATTRIBUTES);
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.astrolabe.1"));
    }
}
