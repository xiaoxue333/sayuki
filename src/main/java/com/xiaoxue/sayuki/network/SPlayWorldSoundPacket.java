/**
 * Sayuki — Network packet: server -> client world sound
 * Compat: Goety-2 — uses separate channel, no packet ID clash
 * Compat: IronsSpellbooks — uses separate channel, no packet ID clash
 */
package com.xiaoxue.sayuki.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class SPlayWorldSoundPacket {
    private final SoundEvent sound;
    private final double x;
    private final double y;
    private final double z;
    private final float volume;
    private final float pitch;

    public SPlayWorldSoundPacket(SoundEvent sound, double x, double y, double z, float volume, float pitch) {
        this.sound = sound;
        this.x = x;
        this.y = y;
        this.z = z;
        this.volume = volume;
        this.pitch = pitch;
    }

    public static void encode(SPlayWorldSoundPacket msg, FriendlyByteBuf buf) {
        buf.writeResourceLocation(ForgeRegistries.SOUND_EVENTS.getKey(msg.sound));
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
        buf.writeFloat(msg.volume);
        buf.writeFloat(msg.pitch);
    }

    public static SPlayWorldSoundPacket decode(FriendlyByteBuf buf) {
        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(buf.readResourceLocation());
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        float volume = buf.readFloat();
        float pitch = buf.readFloat();
        return new SPlayWorldSoundPacket(sound, x, y, z, volume, pitch);
    }

    public static void consume(SPlayWorldSoundPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (Minecraft.getInstance().level != null && msg.sound != null) {
                Minecraft.getInstance().level.playLocalSound(
                        msg.x, msg.y, msg.z,
                        msg.sound,
                        net.minecraft.sounds.SoundSource.PLAYERS,
                        msg.volume, msg.pitch,
                        false
                );
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
