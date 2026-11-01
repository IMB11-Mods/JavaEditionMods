package dev.imb11.snow.generation;

import dev.imb11.snow.profile.SnowProfile;
import dev.imb11.snow.profile.SnowProfiles;
import dev.imb11.snow.util.SnowColumn;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.SnowLayerBlock;

public class SnowGeneration {
    public static boolean place(WorldGenLevel level, BlockPos pos, int flags) {
        return SnowProfiles.get(level, level.getBiome(pos)).map(profile -> {
            SnowColumn.place(level, pos, layers(level, pos, profile), flags, false);
            return true;
        }).orElse(false);
    }

    public static int layers(WorldGenLevel level, BlockPos pos, SnowProfile profile) {
        long seed = level.getSeed();
        int x = pos.getX();
        int z = pos.getZ();
        float depth = Mth.clamp(0.5f + 0.75f * SnowNoise.DEPTH.sample(seed, x, z), 0f, 1f);
        float detail = 1.5f * SnowNoise.DETAIL.sample(seed, x, z);
        float surface = Mth.clamp(Mth.lerp(depth, profile.minLayers(), profile.maxLayers()) + detail, profile.minLayers(), profile.maxLayers());
        float driftScale = Mth.clamp(0.8f + 0.9f * SnowNoise.DRIFT.sample(seed, x, z), 0.2f, 1.4f);
        float drift = SnowDrift.sample(level, pos) * SnowLayerBlock.MAX_HEIGHT * profile.driftStrength() * driftScale;
        return Mth.clamp(Math.round(surface + drift), 1, SnowColumn.MAX_LAYERS);
    }
}
