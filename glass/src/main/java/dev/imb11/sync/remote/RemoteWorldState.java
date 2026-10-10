package dev.imb11.sync.remote;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;

public record RemoteWorldState(ClientboundSetTimePacket time, float rainLevel, float thunderLevel, int skyFlashTime) {
    public RemoteWorldState {
        rainLevel = Math.clamp(rainLevel, 0.0F, 1.0F);
        thunderLevel = Math.clamp(thunderLevel, 0.0F, 1.0F);
        skyFlashTime = Math.max(0, skyFlashTime);
    }

    public void write(RegistryFriendlyByteBuf buf) {
        ClientboundSetTimePacket.STREAM_CODEC.encode(buf, time);
        buf.writeFloat(rainLevel);
        buf.writeFloat(thunderLevel);
        buf.writeVarInt(skyFlashTime);
    }

    public static RemoteWorldState read(RegistryFriendlyByteBuf buf) {
        return new RemoteWorldState(ClientboundSetTimePacket.STREAM_CODEC.decode(buf), buf.readFloat(), buf.readFloat(), buf.readVarInt());
    }
}
