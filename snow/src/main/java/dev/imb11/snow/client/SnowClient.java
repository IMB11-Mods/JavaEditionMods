package dev.imb11.snow.client;

import dev.imb11.snow.client.frost.FrostBackend;
import dev.imb11.snow.client.frost.FrostWeather;
import dev.imb11.snow.snowlogging.Snowlogging;
import dev.imb11.snow.snowlogging.SnowloggingAttachment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public final class SnowClient {
    private static @Nullable ClientLevel level;

    public static void init() {
        Snowlogging.setListener(SnowloggingClient.INSTANCE);
    }

    public static boolean isCurrent(Level level) {
        return level == SnowClient.level;
    }

    public static void levelChanged(@Nullable ClientLevel level) {
        SnowClient.level = level;
        SnowloggingClient.INSTANCE.levelChanged();
        FrostWeather.INSTANCE.levelChanged(level);
        FrostBackend.INSTANCE.levelChanged();
    }

    public static void chunkUnloaded(ClientLevel level, LevelChunk chunk) {
        if (isCurrent(level)) {
            SnowloggingClient.INSTANCE.plantsChanged(level, SnowloggingAttachment.get(chunk), null);
        }
    }

    public static void blockChanged(ClientLevel level, BlockPos pos, BlockState oldState, BlockState newState) {
        if (isCurrent(level)) {
            SnowloggingClient.INSTANCE.blockChanged(level, pos, oldState, newState);
        }
    }

    public static void tick(ClientLevel level) {
        if (isCurrent(level)) {
            SnowloggingClient.INSTANCE.tick(level);
            FrostWeather.INSTANCE.tick(level);
            FrostBackend.INSTANCE.tick(level);
        }
    }
}
