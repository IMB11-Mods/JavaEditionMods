package dev.imb11.snow.generation;

import dev.imb11.snow.profile.SnowProfiles;
import dev.imb11.snow.util.SnowColumn;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;

public class SnowGeneration {
    public static boolean place(WorldGenLevel level, BlockPos pos, RandomSource random, int flags) {
        return SnowProfiles.get(level, level.getBiome(pos)).map(profile -> {
            SnowColumn.place(level, pos, random.nextIntBetweenInclusive(profile.minLayers(), profile.maxLayers()), flags, false);
            return true;
        }).orElse(false);
    }
}
