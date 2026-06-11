package com.xiaoxue.sayuki.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SMidAirJumpPacket {

    public C2SMidAirJumpPacket() {}

    public static void encode(C2SMidAirJumpPacket msg, FriendlyByteBuf buf) {}

    public static C2SMidAirJumpPacket decode(FriendlyByteBuf buf) {
        return new C2SMidAirJumpPacket();
    }

    public static void consume(C2SMidAirJumpPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (player != null && !player.onGround()) {
                int jumps = player.getPersistentData().getInt("SayukiWingedBootsJumps");
                if (jumps < 2) {
                    player.jumpFromGround();
                    player.getPersistentData().putInt("SayukiWingedBootsJumps", jumps + 1);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
