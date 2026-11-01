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
        int color = this.delegate.colorInWorld(state, level, pos);
        float weight = LeafFrost.weight(level, state, pos);
        if (weight <= 0.0F) {
            return color;
        }

        return level instanceof ClientLevel ? immediate(color, weight) : FrostBackend.INSTANCE.meshColor(color, weight, pos);
    }

    @Override
    public int colorAsTerrainParticle(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        return immediate(this.delegate.colorAsTerrainParticle(state, level, pos), LeafFrost.weight(level, state, pos));
    }

    @Override
    public Set<Property<?>> relevantProperties() {
        return this.delegate.relevantProperties();
    }

    private static int immediate(int color, float weight) {
        return FrostColor.apply(color, weight * FrostWeather.INSTANCE.interpolatedProgress());
    }
}
