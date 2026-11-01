package dev.imb11.snow.client.frost;

import dev.imb11.snow.frost.FrostableLeaves;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public final class LeafFrost {
    private static final float SNOW_TEMPERATURE = 0.15F;

    public static float weight(BlockAndTintGetter level, BlockState state, BlockPos pos) {
        if (!FrostableLeaves.contains(state)) {
            return 0.0F;
        }

        float temperature = BiomeTemperatureResolver.temperatureAt(level, pos, FrostWeather.INSTANCE.seaLevel());
        return Mth.clamp((SNOW_TEMPERATURE - temperature) / (SNOW_TEMPERATURE - 0.05F), 0.0F, 1.0F);
    }
}
