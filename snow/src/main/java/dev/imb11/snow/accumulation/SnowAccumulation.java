package dev.imb11.snow.accumulation;

import dev.imb11.snow.profile.SnowProfile;
import dev.imb11.snow.profile.SnowProfiles;
import dev.imb11.snow.util.SnowColumn;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.Optional;

public class SnowAccumulation {
    public static void accumulate(ServerLevel level, BlockPos base) {
        Optional<SnowProfile> profile = SnowProfiles.get(level, level.getBiome(base));
        int gamerule = level.getGameRules().get(GameRules.MAX_SNOW_ACCUMULATION_HEIGHT);
        int maxLayers = gamerule != GameRules.MAX_SNOW_ACCUMULATION_HEIGHT.defaultValue()
                ? Math.min(gamerule, SnowColumn.MAX_LAYERS)
                : profile.map(SnowProfile::maxLayers).orElse(gamerule);
        float rate = profile.map(SnowProfile::accumulationRate).orElse(1f);
        int attempts = Mth.floor(rate) + (level.getRandom().nextFloat() < Mth.frac(rate) ? 1 : 0);

        int current = SnowColumn.layers(level, base);
        int target = Math.min(maxLayers, current + attempts);
        if (target > current) {
            SnowColumn.place(level, base, target, Block.UPDATE_ALL, true);
        }
    }
}
