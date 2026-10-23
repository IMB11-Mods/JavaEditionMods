package dev.imb11.snow.generation;

import dev.imb11.snow.accumulation.SnowProfiles;
import dev.imb11.snow.util.SnowColumn;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;

public class SnowGeneration {
    public static boolean place(WorldGenLevel level, BlockPos pos, RandomSource random, int flags) {
        return SnowProfiles.get(level, level.getBiome(pos)).map(profile -> {
            int layers = Math.max(1, random.nextIntBetweenInclusive(profile.minLayers(), profile.maxLayers()));
            SnowColumn.place(level, pos.immutable(), layers, flags, false);
            return true;
        }).orElse(false);
    }
}
