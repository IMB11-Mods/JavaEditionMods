package dev.imb11.sync.remote;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.Objects;

public record C2SRemoteUnsubscribePacket(RemoteSubscriptionId subscription) implements CustomPacketPayload {
    public static final Type<C2SRemoteUnsubscribePacket> PACKET_ID = new Type<>(Identifier.fromNamespaceAndPath("glass", "remote_unsubscribe"));
    public static final StreamCodec<RegistryFriendlyByteBuf, C2SRemoteUnsubscribePacket> PACKET_CODEC = StreamCodec.ofMember(C2SRemoteUnsubscribePacket::write, C2SRemoteUnsubscribePacket::read);

    public C2SRemoteUnsubscribePacket {
        subscription = Objects.requireNonNull(subscription);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

    private void write(RegistryFriendlyByteBuf buf) {
        subscription.write(buf);
    }

    private static C2SRemoteUnsubscribePacket read(RegistryFriendlyByteBuf buf) {
        return new C2SRemoteUnsubscribePacket(RemoteSubscriptionId.read(buf));
    }
}
