package dev.imb11.mru.sync;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class SyncedConfig<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger("MRU");
    private static final Gson GSON = new Gson();
    private static final int MAX_LENGTH = 32000;
    private static final Map<Identifier, SyncedConfig<?>> CONFIGS = new LinkedHashMap<>();
    private static @Nullable MinecraftServer server;

    private final Identifier id;
    private final Class<T> type;
    private final Supplier<T> local;
    private final Consumer<T> apply;
    private final CustomPacketPayload.Type<Payload> syncType;
    private final CustomPacketPayload.Type<Payload> updateType;
    private final StreamCodec<RegistryFriendlyByteBuf, Payload> syncCodec;
    private final StreamCodec<RegistryFriendlyByteBuf, Payload> updateCodec;
    private BooleanSupplier clientRequired = () -> false;
    private volatile @Nullable T remote;
    private volatile boolean serverPresent;

    private SyncedConfig(Identifier id, Class<T> type, Supplier<T> local, Consumer<T> apply) {
        this.id = id;
        this.type = type;
        this.local = local;
        this.apply = apply;
        this.syncType = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath() + "/sync"));
        this.updateType = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath() + "/update"));
        this.syncCodec = codec(this.syncType);
        this.updateCodec = codec(this.updateType);
    }

    public static <T> SyncedConfig<T> register(Identifier id, Class<T> type, Supplier<T> local, Consumer<T> apply) {
        if (CONFIGS.containsKey(id)) {
            throw new IllegalStateException("Synced config " + id + " is already registered");
        }

        SyncedConfig<T> config = new SyncedConfig<>(id, type, local, apply);
        CONFIGS.put(id, config);
        SyncNetworking.register(config);
        return config;
    }

    public SyncedConfig<T> requireClient(BooleanSupplier required) {
        this.clientRequired = required;
        return this;
    }

    public T get() {
        T value = this.remote;
        return value != null ? value : this.local.get();
    }

    public boolean isRemote() {
        return this.remote != null;
    }

    public boolean serverPresent() {
        return this.serverPresent;
    }

    public void broadcast() {
        MinecraftServer current = server;
        if (current != null) {
            current.execute(() -> current.getPlayerList().getPlayers().forEach(this::send));
        }
    }

    public Identifier id() {
        return this.id;
    }

    public CustomPacketPayload.Type<Payload> syncType() {
        return this.syncType;
    }

    public CustomPacketPayload.Type<Payload> updateType() {
        return this.updateType;
    }

    public StreamCodec<RegistryFriendlyByteBuf, Payload> syncCodec() {
        return this.syncCodec;
    }

    public StreamCodec<RegistryFriendlyByteBuf, Payload> updateCodec() {
        return this.updateCodec;
    }

    public static Collection<SyncedConfig<?>> all() {
        return Collections.unmodifiableCollection(CONFIGS.values());
    }

    public static void setServer(@Nullable MinecraftServer current) {
        server = current;
    }

    public static void onServerJoin(ServerPlayer player) {
        for (SyncedConfig<?> config : CONFIGS.values()) {
            if (SyncNetworking.canSend(player, config.syncType)) {
                config.send(player);
            } else if (config.clientRequired.getAsBoolean()) {
                player.connection.disconnect(Component.literal("This server requires the " + config.id.getNamespace() + " mod to be installed."));
                return;
            }
        }
    }

    public void receiveUpdate(ServerPlayer player, String json) {
        if (!player.canUseGameMasterBlocks()) {
            LOGGER.warn("{} tried to change synced config {} without permission", player.getName().getString(), this.id);
            return;
        }

        T value = this.parse(json);
        if (value != null) {
            this.apply.accept(value);
            LOGGER.info("{} changed synced config {}", player.getName().getString(), this.id);
            this.broadcast();
        }
    }

    public void receiveSync(String json) {
        this.remote = this.parse(json);
    }

    public void setServerPresent(boolean present) {
        this.serverPresent = present;
    }

    public void clearRemote() {
        this.remote = null;
        this.serverPresent = false;
    }

    public String serialize(T value) {
        return GSON.toJson(value);
    }

    private void send(ServerPlayer player) {
        if (SyncNetworking.canSend(player, this.syncType)) {
            SyncNetworking.send(player, new Payload(this.syncType, this.serialize(this.local.get())));
        }
    }

    private @Nullable T parse(String json) {
        try {
            return GSON.fromJson(json, this.type);
        } catch (JsonParseException e) {
            LOGGER.error("Failed to read synced config {}", this.id, e);
            return null;
        }
    }

    private static StreamCodec<RegistryFriendlyByteBuf, Payload> codec(CustomPacketPayload.Type<Payload> type) {
        return ByteBufCodecs.stringUtf8(MAX_LENGTH).<RegistryFriendlyByteBuf>cast().map(json -> new Payload(type, json), Payload::json);
    }

    public record Payload(CustomPacketPayload.Type<Payload> type, String json) implements CustomPacketPayload {
    }
}
