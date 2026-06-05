/**
 * Sayuki — Stellar Jade Projectile (ThrowableProjectile, no gravity, item particle trail)
 * Compat: Goety-2 and IronsSpellbooks — entity ID: sayuki:stellar_jade_projectile, no name clash
 */
package com.xiaoxue.sayuki.entity;

import com.xiaoxue.sayuki.item.ModItems;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3f;

public class StellarJadeProjectile extends ThrowableProjectile {
    private static final float DAMAGE = 5.0F;

    public StellarJadeProjectile(EntityType<? extends ThrowableProjectile> type, Level level) {
        super(type, level);
    }

    public StellarJadeProjectile(Level level, LivingEntity shooter) {
        super(ModEntities.STELLAR_JADE_PROJECTILE.get(), shooter, level);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            ServerLevel serverLevel = (ServerLevel) this.level();

            if (result.getType() == HitResult.Type.ENTITY) {
                EntityHitResult entityResult = (EntityHitResult) result;
                entityResult.getEntity().hurt(
                        this.damageSources().thrown(this, this.getOwner()),
                        DAMAGE
                );
            }

            for (int i = 0; i < 12; i++) {
                double angle = (2.0 * Math.PI * i) / 12;
                double r = 0.4;
                serverLevel.sendParticles(
                        new DustParticleOptions(new Vector3f(0.9F, 0.7F, 0.2F), 1.5F),
                        getX() + Math.cos(angle) * r, getY() + Math.sin(angle) * r, getZ(),
                        1, 0.0, 0.0, 0.0, 0.05);
            }
            serverLevel.sendParticles(ParticleTypes.FLASH,
                    getX(), getY(), getZ(), 2, 0.0, 0.0, 0.0, 0.0);
            serverLevel.sendParticles(ParticleTypes.SMOKE,
                    getX(), getY(), getZ(), 4, 0.1, 0.15, 0.1, 0.03);

            this.level().playSound(null, getX(), getY(), getZ(),
                    SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.PLAYERS, 0.8F, 1.4F);

            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 motion = this.getDeltaMovement();
        if (motion.lengthSqr() > 1.0E-7D) {
            double horizontalDist = motion.horizontalDistance();
            this.setYRot((float) (Math.atan2(motion.x, motion.z) * (180.0D / Math.PI)));
            this.setXRot((float) (Math.atan2(motion.y, horizontalDist) * (180.0D / Math.PI)));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }
        if (this.level().isClientSide) {
            ItemStack itemStack = new ItemStack(ModItems.STELLAR_JADE.get());
            this.level().addParticle(
                    new ItemParticleOption(ParticleTypes.ITEM, itemStack),
                    this.getX(), this.getY(), this.getZ(),
                    0.0D, 0.0D, 0.0D
            );
        }
        if (!this.level().isClientSide) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            Vec3 vel = this.getDeltaMovement();
            Vec3 pos = this.position();
            serverLevel.sendParticles(
                    new DustParticleOptions(new Vector3f(0.95F, 0.8F, 0.15F), 0.8F),
                    pos.x, pos.y, pos.z,
                    2, 0.04, 0.04, 0.04, 0.01);
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                    pos.x - vel.x * 0.15, pos.y - vel.y * 0.15, pos.z - vel.z * 0.15,
                    1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
