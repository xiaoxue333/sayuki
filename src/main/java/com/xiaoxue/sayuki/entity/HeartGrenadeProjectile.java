/**
 * Sayuki — Heart Grenade Projectile (ThrowableItemProjectile, explosive on hit)
 * Compat: Goety-2 and IronsSpellbooks — entity ID: sayuki:heart_grenade_projectile, no name clash
 */
package com.xiaoxue.sayuki.entity;

import com.xiaoxue.sayuki.item.ModItems;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3f;

public class HeartGrenadeProjectile extends ThrowableItemProjectile {

    public HeartGrenadeProjectile(EntityType<? extends HeartGrenadeProjectile> type, Level level) {
        super(type, level);
    }

    public HeartGrenadeProjectile(Level level, LivingEntity shooter) {
        super(ModEntities.HEART_GRENADE_PROJECTILE.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.HEART_GRENADE.get();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            Vec3 pos = this.position();
            serverLevel.sendParticles(ParticleTypes.SMOKE,
                    pos.x, pos.y, pos.z,
                    1, 0.02, 0.02, 0.02, 0.01);
            if (tickCount % 3 == 0) {
                serverLevel.sendParticles(
                        new DustParticleOptions(new Vector3f(1.0F, 0.3F, 0.15F), 0.8F),
                        pos.x, pos.y, pos.z,
                        2, 0.05, 0.05, 0.05, 0.02);
            }
            if (tickCount % 4 == 0) {
                serverLevel.sendParticles(ParticleTypes.FLAME,
                        pos.x, pos.y, pos.z,
                        1, 0.03, 0.03, 0.03, 0.01);
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            ServerLevel serverLevel = (ServerLevel) this.level();

            for (int i = 0; i < 20; i++) {
                double angle = (2.0 * Math.PI * i) / 20;
                double r = 1.5;
                serverLevel.sendParticles(
                        new DustParticleOptions(new Vector3f(1.0F, 0.3F, 0.1F), 2.5F),
                        getX() + Math.cos(angle) * r, getY() + 0.5, getZ() + Math.sin(angle) * r,
                        1, 0.0, 0.1, 0.0, 0.08);
                serverLevel.sendParticles(ParticleTypes.FLAME,
                        getX() + Math.cos(angle) * r * 0.6, getY(), getZ() + Math.sin(angle) * r * 0.6,
                        2, 0.1, 0.05, 0.1, 0.06);
                serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                        getX() + Math.cos(angle) * r * 0.3, getY() + 0.3, getZ() + Math.sin(angle) * r * 0.3,
                        1, 0.0, 0.0, 0.0, 0.0);
            }

            serverLevel.sendParticles(ParticleTypes.FLASH,
                    getX(), getY(), getZ(), 1, 0.0, 0.0, 0.0, 0.0);
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    getX(), getY(), getZ(), 8, 0.2, 0.2, 0.2, 0.05);

            this.level().playSound(null, getX(), getY(), getZ(),
                    SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.5F, 1.0F);
            this.level().playSound(null, getX(), getY(), getZ(),
                    SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.PLAYERS, 0.8F, 1.3F);

            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 6.0F, Level.ExplosionInteraction.TNT);
            this.discard();
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
