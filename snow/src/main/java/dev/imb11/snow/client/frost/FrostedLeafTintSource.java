package dev.imb11.snow.client.frost;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Set;

public record FrostedLeafTintSource(BlockTintSource delegate) implements BlockTintSource {
    @Override
    public int color(BlockState state) {
        return this.delegate.color(state);
    }

    @Override
    public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        return this.frost(this.delegate.colorInWorld(state, level, pos), state, level, pos);
    }

    @Override
    public int colorAsTerrainParticle(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        return this.frost(this.delegate.colorAsTerrainParticle(state, level, pos), state, level, pos);
    }

    @Override
    public Set<Property<?>> relevantProperties() {
        return this.delegate.relevantProperties();
    }

    private int frost(int color, BlockState state, BlockAndTintGetter level, BlockPos pos) {
        float weight = FrostedLeaves.weight(level, state, pos);
        if (weight <= 0.0F) {
            return color;
        }

        if (level instanceof ClientLevel) {
            return FrostedLeaves.frost(color, weight * FrostedLeaves.progress());
        }

        FrostedLeaves.track(pos);
        return FrostedLeaves.frost(color, weight * FrostedLeaves.bakedProgress());
    }
}
