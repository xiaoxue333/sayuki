/**
 * Sayuki — Network channel (sayuki:channel)
 * Compat: Goety-2 — uses goety:channel, no clash
 * Compat: IronsSpellbooks — uses irons_spellbooks namespace network, no clash
 */
package com.xiaoxue.sayuki.network;

import com.xiaoxue.sayuki.Sayuki;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    public static SimpleChannel INSTANCE;
    private static int id = 0;

    public static int nextID() {
        return id++;
    }

    public static void init() {
        INSTANCE = NetworkRegistry.newSimpleChannel(
                ResourceLocation.fromNamespaceAndPath(Sayuki.MOD_ID, "channel"),
                () -> "1.0",
                s -> true,
                s -> true
        );

        INSTANCE.registerMessage(nextID(), SPlayWorldSoundPacket.class,
                SPlayWorldSoundPacket::encode,
                SPlayWorldSoundPacket::decode,
                SPlayWorldSoundPacket::consume);

        INSTANCE.registerMessage(nextID(), C2SMidAirJumpPacket.class,
                C2SMidAirJumpPacket::encode,
                C2SMidAirJumpPacket::decode,
                C2SMidAirJumpPacket::consume);
    }

    public static <MSG> void sendTo(Player player, MSG msg) {
        ModNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), msg);
    }

    public static <MSG> void sendToServer(MSG msg) {
        ModNetwork.INSTANCE.sendToServer(msg);
    }

    public static <MSG> void sentToTrackingChunk(LevelChunk chunk, MSG msg) {
        ModNetwork.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), msg);
    }

    public static <MSG> void sentToTrackingEntity(Entity entity, MSG msg) {
        ModNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), msg);
    }

    public static <MSG> void sentToTrackingEntityAndPlayer(Entity entity, MSG msg) {
        ModNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), msg);
    }

    public static <MSG> void sendToALL(MSG msg) {
        ModNetwork.INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
    }

    public static <MSG> void sendToClient(ServerPlayer player, MSG msg) {
        ModNetwork.INSTANCE.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
