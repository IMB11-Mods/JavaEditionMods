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
    public static final int PREDICTION_TICKS = 60;
    private final Map<Long, BlockState> plants = new ConcurrentHashMap<>();
    private final Map<Long, Prediction> predictions = new ConcurrentHashMap<>();
    private @Nullable ClientLevel level;

    private SnowloggingClient() {
    }

    public static void init() {
        Snowlogging.setListener(INSTANCE);
    }

    public @Nullable BlockState plantAt(BlockPos pos) {
        Prediction prediction = this.predictions.get(pos.asLong());
        return prediction != null ? prediction.plant() : this.plants.get(pos.asLong());
    }

    public void levelChanged(@Nullable ClientLevel level) {
        this.level = level;
        this.plants.clear();
        this.predictions.clear();
    }

    @Override
    public void plantsChanged(Level level, @Nullable SnowloggedPlants previous, @Nullable SnowloggedPlants current) {
        if (level != this.level) {
            return;
        }

        if (previous != null) {
            previous.plants().keySet().forEach(pos -> {
                if (current == null || current.get(pos) == null) {
                    this.plants.remove(pos.asLong());
                    this.setDirty(pos);
                }
            });
        }

        if (current != null) {
            current.plants().forEach((pos, plant) -> {
                BlockState shown = this.plantAt(pos);
                this.predictions.remove(pos.asLong());
                this.plants.put(pos.asLong(), plant);
                if (shown != plant) {
                    this.setDirty(pos);
                }
            });
        }
    }

    @Override
    public void plantPredicted(Level level, BlockPos pos, BlockState plant) {
        if (level == this.level) {
            this.predict(pos, plant);
            this.setDirty(pos);
        }
    }

    public void blockChanged(ClientLevel level, BlockPos pos, BlockState oldState, BlockState newState) {
        if (level != this.level) {
            return;
        }

        if (newState.is(Blocks.SNOW) && !oldState.is(Blocks.SNOW) && SnowConfig.SYNC.serverPresent() && Snowlogging.canSnowlog(oldState)) {
            if (!this.plants.containsKey(pos.asLong())) {
                this.predict(pos, oldState);
            }
        } else if (oldState.is(Blocks.SNOW) && !newState.is(Blocks.SNOW)) {
            this.predictions.remove(pos.asLong());
        }
    }

    public void tick(ClientLevel level) {
        if (level != this.level || this.predictions.isEmpty()) {
            return;
        }

        long time = level.getGameTime();
        this.predictions.entrySet().removeIf(entry -> {
            if (entry.getValue().expiresAt() > time) {
                return false;
            }

            this.setDirty(BlockPos.of(entry.getKey()));
            return true;
        });
    }

    private void predict(BlockPos pos, BlockState plant) {
        if (this.level != null) {
            this.predictions.put(pos.asLong(), new Prediction(plant, this.level.getGameTime() + PREDICTION_TICKS));
        }
    }

    private void setDirty(BlockPos pos) {
        if (this.level != null) {
            this.level.setSectionDirtyWithNeighbors(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getY()), SectionPos.blockToSectionCoord(pos.getZ()));
        }
    }

    private record Prediction(BlockState plant, long expiresAt) {
    }
}
