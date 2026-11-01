package dev.imb11.snow.client.frost;

import dev.imb11.snow.client.SnowClient;
import dev.imb11.snow.compat.SeasonCompat;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class BiomeTemperatureResolver {
    private static final float MIN_TEMPERATURE = -1.0F;
    private static final float MAX_TEMPERATURE = 1.0F;
    private static final Map<Biome, Holder<Biome>> HOLDERS = new ConcurrentHashMap<>();
    public static final ColorResolver RESOLVER = (biome, x, z) -> encode(biome.hasPrecipitation() ? baseTemperature(biome) : MAX_TEMPERATURE);

    public static float temperatureAt(BlockAndTintGetter level, BlockPos pos, int seaLevel) {
        float temperature = decode(level.getBlockTint(pos, RESOLVER));
        int snowLine = seaLevel + 17;
        if (pos.getY() > snowLine) {
            temperature -= (pos.getY() - snowLine) * 0.05F / 40.0F;
        }

        return temperature;
    }

    public static void levelChanged() {
        HOLDERS.clear();
    }

    private static float baseTemperature(Biome biome) {
        ClientLevel level = SnowClient.level();
        if (!SeasonCompat.LOADED || level == null) {
            return biome.getBaseTemperature();
        }

        return SeasonCompat.baseTemperature(level, HOLDERS.computeIfAbsent(biome, key -> holder(level, key)));
    }

    private static Holder<Biome> holder(ClientLevel level, Biome biome) {
        Registry<Biome> registry = level.registryAccess().lookupOrThrow(Registries.BIOME);
        Optional<Holder.Reference<Biome>> reference = registry.getResourceKey(biome).flatMap(registry::get);
        return reference.isPresent() ? reference.get() : Holder.direct(biome);
    }

    private static int encode(float temperature) {
        int value = Math.round(Mth.clamp((temperature - MIN_TEMPERATURE) / (MAX_TEMPERATURE - MIN_TEMPERATURE), 0.0F, 1.0F) * 255.0F);
        return ARGB.color(255, value, value, value);
    }

    private static float decode(int color) {
        return MIN_TEMPERATURE + ARGB.red(color) / 255.0F * (MAX_TEMPERATURE - MIN_TEMPERATURE);
    }
}
