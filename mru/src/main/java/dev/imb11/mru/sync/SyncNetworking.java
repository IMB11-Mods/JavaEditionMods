//? fabric {
package dev.imb11.mru.sync;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public final class SyncNetworking {
    private static boolean initialized;

    private SyncNetworking() {
    }

    static void register(SyncedConfig<?> config) {
        init();
        PayloadTypeRegistry.clientboundPlay().register(config.syncType(), config.syncCodec());
        PayloadTypeRegistry.serverboundPlay().register(config.updateType(), config.updateCodec());
        ServerPlayNetworking.registerGlobalReceiver(config.updateType(), (payload, context) -> config.receiveUpdate(context.player(), payload.json()));
    }

    private static void init() {
        if (initialized) {
            return;
        }

        initialized = true;
        ServerLifecycleEvents.SERVER_STARTED.register(SyncedConfig::setServer);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> SyncedConfig.setServer(null));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> SyncedConfig.onServerJoin(handler.getPlayer()));
    }

    public static boolean canSend(ServerPlayer player, CustomPacketPayload.Type<?> type) {
        return ServerPlayNetworking.canSend(player, type);
    }

    public static void send(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }
}
//?} else {
/*package dev.imb11.mru.sync;

import dev.imb11.mru.sync.client.SyncClientNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = "mru")
public final class SyncNetworking {
    private SyncNetworking() {
    }

    static void register(SyncedConfig<?> config) {
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();
        for (SyncedConfig<?> config : SyncedConfig.all()) {
            registrar.playToClient(config.syncType(), config.syncCodec(), (payload, context) -> SyncClientNetworking.receive(config, payload.json()));
            registrar.playToServer(config.updateType(), config.updateCodec(), (payload, context) -> config.receiveUpdate((ServerPlayer) context.player(), payload.json()));
        }
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        SyncedConfig.setServer(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        SyncedConfig.setServer(null);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SyncedConfig.onServerJoin(player);
        }
    }

    public static boolean canSend(ServerPlayer player, CustomPacketPayload.Type<?> type) {
        return player.connection.hasChannel(type.id());
    }

    public static void send(ServerPlayer player, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }
}
*///?}
