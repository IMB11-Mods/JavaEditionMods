package dev.imb11.snow.client.frost;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ColorResolver;

public final class BiomeTemperatureResolver {
    private static final float MIN_TEMPERATURE = -1.0F;
    private static final float MAX_TEMPERATURE = 1.0F;
    public static final ColorResolver RESOLVER = (biome, x, z) -> encode(biome.hasPrecipitation() ? biome.getBaseTemperature() : MAX_TEMPERATURE);

    public static float temperatureAt(BlockAndTintGetter level, BlockPos pos, int seaLevel) {
        float temperature = decode(level.getBlockTint(pos, RESOLVER));
        int snowLine = seaLevel + 17;
        if (pos.getY() > snowLine) {
            temperature -= (pos.getY() - snowLine) * 0.05F / 40.0F;
        }

        return temperature;
    }

    private static int encode(float temperature) {
        int value = Math.round(Mth.clamp((temperature - MIN_TEMPERATURE) / (MAX_TEMPERATURE - MIN_TEMPERATURE), 0.0F, 1.0F) * 255.0F);
        return ARGB.color(255, value, value, value);
    }

    private static float decode(int color) {
        return MIN_TEMPERATURE + ARGB.red(color) / 255.0F * (MAX_TEMPERATURE - MIN_TEMPERATURE);
    }
}
