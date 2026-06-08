/**
 * Sayuki — ThrownAxeEntity: thrown axe projectile (ThrowingAxe relic)
 * Extends AbstractArrow like ThrownTrident; sticks in blocks/entities.
 * Damage = the axe item's attack damage.
 */
package com.xiaoxue.sayuki.entity;

import com.xiaoxue.sayuki.item.ModItems;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class ThrownAxeEntity extends AbstractArrow {
    private static final EntityDataAccessor<ItemStack> DATA_AXE_STACK =
            SynchedEntityData.defineId(ThrownAxeEntity.class, EntityDataSerializers.ITEM_STACK);

    private boolean dealtDamage;

    public ThrownAxeEntity(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
    }

    public ThrownAxeEntity(Level level, LivingEntity shooter, ItemStack axeStack) {
        super(ModEntities.THROWN_AXE.get(), shooter, level);
        this.entityData.set(DATA_AXE_STACK, axeStack.copy());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_AXE_STACK, ItemStack.EMPTY);
    }

    @Override
    protected ItemStack getPickupItem() {
        return this.entityData.get(DATA_AXE_STACK).copy();
    }

    @Nullable
    public ItemStack getAxeStack() {
        ItemStack stack = this.entityData.get(DATA_AXE_STACK);
        return stack.isEmpty() ? null : stack;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.inGround && this.inGroundTime > 80) {
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (this.dealtDamage) return;
        this.dealtDamage = true;

        ItemStack axeStack = this.entityData.get(DATA_AXE_STACK);
        float damage = 1.0F;
        if (!axeStack.isEmpty()) {
            var attrs = axeStack.getAttributeModifiers(net.minecraft.world.entity.EquipmentSlot.MAINHAND);
            var entries = attrs.get(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
            if (entries != null) {
                double sum = 0;
                for (var mod : entries) {
                    sum += mod.getAmount();
                }
                damage = (float) sum + 1.0F; // base 1.0 + item modifiers
                if (damage <= 0) damage = 1.0F;
            }
        }
        super.setBaseDamage(damage);
        super.onHitEntity(result);
    }

    @Override
    public void playerTouch(Player player) {
        if (!this.level().isClientSide && (this.inGround || this.dealtDamage)) {
            if (player instanceof ServerPlayer sp) {
                ItemStack axe = this.entityData.get(DATA_AXE_STACK);
                if (!axe.isEmpty() && !sp.getInventory().add(axe)) {
                    sp.drop(axe, false);
                }
            }
            this.discard();
        }
    }

    @Override
    protected boolean tryPickup(Player player) {
        return super.tryPickup(player);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected float getWaterInertia() {
        return 0.99F;
    }
}
