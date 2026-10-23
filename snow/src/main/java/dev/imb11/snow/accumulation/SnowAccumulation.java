package dev.imb11.snow.accumulation;

import dev.imb11.snow.util.SnowColumn;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.Optional;

public class SnowAccumulation {
    private static final int DEFAULT_GAMERULE_HEIGHT = 1;

    public static void accumulate(ServerLevel level, BlockPos base) {
        Optional<SnowProfile> profile = SnowProfiles.get(level, level.getBiome(base));
        int maxLayers = maxLayers(level, profile);
        float rate = profile.map(SnowProfile::accumulationRate).orElse(1f);
        int attempts = Mth.floor(rate) + (level.getRandom().nextFloat() < Mth.frac(rate) ? 1 : 0);

        int current = SnowColumn.layers(level, base);
        int target = Math.min(maxLayers, current + attempts);
        if (target > current) {
            SnowColumn.place(level, base, target, Block.UPDATE_ALL, true);
        }
    }

    private static int maxLayers(ServerLevel level, Optional<SnowProfile> profile) {
        int gamerule = level.getGameRules().get(GameRules.MAX_SNOW_ACCUMULATION_HEIGHT);
        if (gamerule != DEFAULT_GAMERULE_HEIGHT) {
            return Math.min(gamerule, SnowColumn.MAX_LAYERS);
        }

        return profile.map(SnowProfile::maxLayers).orElse(Math.min(gamerule, SnowLayerBlock.MAX_HEIGHT));
    }
}
