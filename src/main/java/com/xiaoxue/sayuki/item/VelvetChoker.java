package com.xiaoxue.sayuki.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class VelvetChoker extends Item implements ICurioItem {

    private static final double SCAN_RANGE = 64.0;

    public VelvetChoker(Properties properties) {
        super(properties);
    }

    public static boolean isEquippedBy(Player player) {
        return CuriosApi.getCuriosInventory(player).resolve().flatMap(handler ->
                handler.findFirstCurio(stack -> stack.getItem() == ModItems.VELVET_CHOKER.get())).isPresent();
    }

    /**
     * Check if a living entity is owned by the given player (TamableAnimal, IronGolem, OwnableEntity).
     */
    public static boolean isOwnedBy(LivingEntity living, UUID playerUUID) {
        if (living instanceof TamableAnimal tamable) {
            return tamable.isTame() && playerUUID.equals(tamable.getOwnerUUID());
        }
        if (living instanceof IronGolem golem) {
            return golem.isPlayerCreated();
        }
        if (living instanceof OwnableEntity ownable) {
            return playerUUID.equals(ownable.getOwnerUUID());
        }
        return false;
    }

    /**
     * Copy all player attributes to a single owned entity.
     * Attributes not present on the target are silently skipped.
     */
    public static void syncAttributesTo(Player player, LivingEntity target) {
        for (Attribute attr : ForgeRegistries.ATTRIBUTES) {
            AttributeInstance playerInst = player.getAttribute(attr);
            if (playerInst == null) continue;
            AttributeInstance targetInst = target.getAttribute(attr);
            if (targetInst == null) continue;
            targetInst.setBaseValue(playerInst.getValue());
        }
    }

    /**
     * One-time scan: find all existing owned entities within range and sync attributes.
     * Called only on equip.
     */
    public static void syncExistingOwned(Player player) {
        Level level = player.level();
        if (level.isClientSide()) return;
        AABB area = new AABB(player.blockPosition()).inflate(SCAN_RANGE);
        UUID pid = player.getUUID();

        for (Entity entity : level.getEntities(player, area)) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (entity == player) continue;
            if (isOwnedBy(living, pid)) {
                syncAttributesTo(player, living);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sayuki.velvet_choker.1"));
    }
}
