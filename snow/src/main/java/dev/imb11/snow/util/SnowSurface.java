package dev.imb11.snow.util;

import dev.imb11.snow.compat.SeasonCompat;
import dev.imb11.snow.snowlogging.Snowlogging;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SnowSurface {
    public static boolean shouldSnow(Biome biome, LevelReader level, BlockPos pos) {
        return shouldSnow(biome, level, pos, false);
    }

    public static boolean shouldSnow(Biome biome, LevelReader level, BlockPos pos, boolean vanilla) {
        if (vanilla) {
            return true;
        }

        if (SeasonCompat.precipitationAt(level, biome, pos) != Biome.Precipitation.SNOW || !level.isInsideBuildHeight(pos.getY()) || level.getBrightness(LightLayer.BLOCK, pos) >= 10) {
            return false;
        }

        BlockState state = level.getBlockState(pos);
        return (state.isAir() || state.is(Blocks.SNOW) || Snowlogging.canSnowlog(state)) && Blocks.SNOW.defaultBlockState().canSurvive(level, pos);
    }
}
