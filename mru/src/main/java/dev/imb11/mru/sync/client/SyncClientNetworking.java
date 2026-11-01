//? fabric {
package dev.imb11.mru.sync.client;

import dev.imb11.mru.sync.SyncedConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

public final class SyncClientNetworking {
    private SyncClientNetworking() {
    }

    public static void init() {
        for (SyncedConfig<?> config : SyncedConfig.all()) {
            ClientPlayNetworking.registerGlobalReceiver(config.syncType(), (payload, context) -> receive(config, payload.json()));
        }

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> SyncedConfig.all().forEach(config -> config.setServerPresent(ClientPlayNetworking.canSend(config.updateType()))));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> SyncedConfig.all().forEach(SyncedConfig::clearRemote));
    }

    public static void receive(SyncedConfig<?> config, String json) {
        if (!Minecraft.getInstance().isLocalServer()) {
            config.receiveSync(json);
        }
    }

    public static <T> void requestUpdate(SyncedConfig<T> config, T value) {
        if (ClientPlayNetworking.canSend(config.updateType())) {
            ClientPlayNetworking.send(new SyncedConfig.Payload(config.updateType(), config.serialize(value)));
        }
    }

    public static boolean canEdit(SyncedConfig<?> config) {
        if (!config.isRemote()) {
            return true;
        }

        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.player != null && minecraft.player.canUseGameMasterBlocks();
    }
}
//?} else {
/*package dev.imb11.mru.sync.client;

import dev.imb11.mru.sync.SyncedConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

@EventBusSubscriber(modid = "mru", value = Dist.CLIENT)
public final class SyncClientNetworking {
    private SyncClientNetworking() {
    }

    public static void init() {
    }

    @SubscribeEvent
    public static void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        SyncedConfig.all().forEach(config -> config.setServerPresent(connection != null && connection.hasChannel(config.updateType().id())));
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        SyncedConfig.all().forEach(SyncedConfig::clearRemote);
    }

    public static void receive(SyncedConfig<?> config, String json) {
        if (!Minecraft.getInstance().isLocalServer()) {
            config.receiveSync(json);
        }
    }

    public static <T> void requestUpdate(SyncedConfig<T> config, T value) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null && connection.hasChannel(config.updateType().id())) {
            ClientPacketDistributor.sendToServer(new SyncedConfig.Payload(config.updateType(), config.serialize(value)));
        }
    }

    public static boolean canEdit(SyncedConfig<?> config) {
        if (!config.isRemote()) {
            return true;
        }

        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.player != null && minecraft.player.canUseGameMasterBlocks();
    }
}
*///?}
