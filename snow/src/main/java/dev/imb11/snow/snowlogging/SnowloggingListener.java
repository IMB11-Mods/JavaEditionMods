package dev.imb11.snow.snowlogging;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public interface SnowloggingListener {
    SnowloggingListener NONE = new SnowloggingListener() {
    };

    default void plantsChanged(Level level, @Nullable SnowloggedPlants previous, @Nullable SnowloggedPlants current) {
    }

    default void plantPredicted(Level level, BlockPos pos, BlockState plant) {
    }
}
