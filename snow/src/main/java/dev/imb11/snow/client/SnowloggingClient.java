package dev.imb11.snow.client;

import dev.imb11.snow.config.SnowConfig;
import dev.imb11.snow.snowlogging.SnowloggedPlants;
import dev.imb11.snow.snowlogging.Snowlogging;
import dev.imb11.snow.snowlogging.SnowloggingListener;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SnowloggingClient implements SnowloggingListener {
    public static final SnowloggingClient INSTANCE = new SnowloggingClient();
    private final Map<Long, BlockState> plants = new ConcurrentHashMap<>();
    private final Map<Long, Prediction> predictions = new ConcurrentHashMap<>();

    public @Nullable BlockState plantAt(BlockPos pos) {
        Prediction prediction = this.predictions.get(pos.asLong());
        return prediction != null ? prediction.plant() : this.plants.get(pos.asLong());
    }

    public void levelChanged() {
        this.plants.clear();
        this.predictions.clear();
    }

    @Override
    public void plantsChanged(Level level, @Nullable SnowloggedPlants previous, @Nullable SnowloggedPlants current) {
        if (!SnowClient.isCurrent(level)) {
            return;
        }

        ClientLevel clientLevel = (ClientLevel) level;
        if (previous != null) {
            previous.plants().keySet().forEach(pos -> {
                if (current == null || current.get(pos) == null) {
                    this.plants.remove(pos.asLong());
                    setDirty(clientLevel, pos);
                }
            });
        }

        if (current != null) {
            current.plants().forEach((pos, plant) -> {
                BlockState shown = this.plantAt(pos);
                this.predictions.remove(pos.asLong());
                this.plants.put(pos.asLong(), plant);
                if (shown != plant) {
                    setDirty(clientLevel, pos);
                }
            });
        }
    }

    @Override
    public void plantPredicted(Level level, BlockPos pos, BlockState plant) {
        if (SnowClient.isCurrent(level)) {
            this.predict(level, pos, plant);
            setDirty((ClientLevel) level, pos);
        }
    }

    public void blockChanged(ClientLevel level, BlockPos pos, BlockState oldState, BlockState newState) {
        if (newState.is(Blocks.SNOW) && !oldState.is(Blocks.SNOW) && SnowConfig.SYNC.serverPresent() && Snowlogging.canSnowlog(oldState)) {
            if (!this.plants.containsKey(pos.asLong())) {
                this.predict(level, pos, oldState);
            }
        } else if (oldState.is(Blocks.SNOW) && !newState.is(Blocks.SNOW)) {
            this.predictions.remove(pos.asLong());
        }
    }

    public void tick(ClientLevel level) {
        if (this.predictions.isEmpty()) {
            return;
        }

        long time = level.getGameTime();
        this.predictions.entrySet().removeIf(entry -> {
            if (entry.getValue().expiresAt() > time) {
                return false;
            }

            setDirty(level, BlockPos.of(entry.getKey()));
            return true;
        });
    }

    private void predict(Level level, BlockPos pos, BlockState plant) {
        this.predictions.put(pos.asLong(), new Prediction(plant, level.getGameTime() + 60));
    }

    private static void setDirty(ClientLevel level, BlockPos pos) {
        level.setSectionDirtyWithNeighbors(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getY()), SectionPos.blockToSectionCoord(pos.getZ()));
    }

    private record Prediction(BlockState plant, long expiresAt) {
    }
}
