package dev.imb11.snow.compat;

import cc.cassian.mru.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.init.ModConfig;
import sereneseasons.init.ModTags;
import sereneseasons.season.SeasonHooks;

public final class SeasonCompat {
    public static final boolean LOADED = Platform.INSTANCE.isLoaded("sereneseasons");

    public static Biome.Precipitation precipitationAt(LevelReader level, Biome biome, BlockPos pos) {
        return LOADED ? Access.precipitationAt(level, biome, pos) : biome.getPrecipitationAt(pos, level.getSeaLevel());
    }

    public static boolean warmEnoughToRain(LevelReader level, Biome biome, BlockPos pos) {
        return LOADED ? Access.warmEnoughToRain(level, biome, pos) : biome.warmEnoughToRain(pos, level.getSeaLevel());
    }

    public static float baseTemperature(Level level, Holder<Biome> biome) {
        float base = biome.value().getBaseTemperature();
        return LOADED ? Access.baseTemperature(level, biome, base) : base;
    }

    private static final class Access {
        private static Biome.Precipitation precipitationAt(LevelReader level, Biome biome, BlockPos pos) {
            return SeasonHooks.getPrecipitationAtTickIceAndSnowHook(level, biome, pos, level.getSeaLevel());
        }

        private static boolean warmEnoughToRain(LevelReader level, Biome biome, BlockPos pos) {
            return SeasonHooks.shouldFreezeWarmEnoughToRainHook(biome, pos, level.getSeaLevel(), level);
        }

        private static float baseTemperature(Level level, Holder<Biome> biome, float base) {
            if (!SeasonHelper.hasSeasons(level) || base > 0.8F || biome.is(ModTags.Biomes.TROPICAL_BIOMES) || biome.is(ModTags.Biomes.BLACKLISTED_BIOMES)) {
                return base;
            }

            Season.SubSeason subSeason = SeasonHelper.getSeasonState(level).getSubSeason();
            return Mth.clamp(base + ModConfig.seasons.getSeasonProperties(subSeason).biomeTempAdjustment(), -0.5F, 2.0F);
        }
    }
}
