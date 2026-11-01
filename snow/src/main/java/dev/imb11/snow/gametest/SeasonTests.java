package dev.imb11.snow.gametest;

import dev.imb11.snow.accumulation.SnowAccumulation;
import dev.imb11.snow.gametest.SnowGameTests.TestDefinition;
import dev.imb11.snow.gametest.support.SnowTestHelper;
import dev.imb11.snow.snowlogging.Snowlogging;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import sereneseasons.api.season.Season;
import sereneseasons.season.SeasonHandler;
import sereneseasons.season.SeasonTime;

import java.util.List;

import static dev.imb11.snow.gametest.support.SnowTestEnvironments.SNOWFALL;
import static dev.imb11.snow.gametest.support.SnowTestEnvironments.WARM_MELTING;
import static dev.imb11.snow.gametest.support.SnowTestHelper.PLATFORM;
import static dev.imb11.snow.gametest.support.SnowTestHelper.SURFACE;

public class SeasonTests {
    public static List<TestDefinition> all() {
        return List.of(
                new TestDefinition("seasons/winter_plains_snow", SNOWFALL, PLATFORM, 100, SeasonTests::winterPlainsSnow),
                new TestDefinition("seasons/summer_plains_dry", SNOWFALL, PLATFORM, 100, SeasonTests::summerPlainsDry),
                new TestDefinition("seasons/layers_stack", SNOWFALL, PLATFORM, 100, SeasonTests::layersStack),
                new TestDefinition("seasons/winter_snowlogs", SNOWFALL, PLATFORM, 100, SeasonTests::winterSnowlogs),
                new TestDefinition("seasons/spring_melts", WARM_MELTING, PLATFORM, 20, SeasonTests::springMelts),
                new TestDefinition("seasons/winter_keeps_snow", WARM_MELTING, PLATFORM, 20, SeasonTests::winterKeepsSnow)
        );
    }

    private static void winterPlainsSnow(GameTestHelper helper) {
        helper.setBiome(Biomes.PLAINS);
        season(helper, Season.SubSeason.MID_WINTER);
        whenRaining(helper, () -> {
            snow(helper);
            SnowTestHelper.assertLayers(helper, SURFACE, 1);
        });
    }

    private static void summerPlainsDry(GameTestHelper helper) {
        helper.setBiome(Biomes.PLAINS);
        season(helper, Season.SubSeason.MID_SUMMER);
        whenRaining(helper, () -> {
            snow(helper);
            SnowTestHelper.assertLayers(helper, SURFACE, 0);
        });
    }

    private static void layersStack(GameTestHelper helper) {
        SnowTestHelper.setBiome(helper, SnowTestHelper.biome(helper, Biomes.ICE_SPIKES));
        season(helper, Season.SubSeason.MID_WINTER);
        int target = SnowAccumulation.target(helper.getLevel(), helper.absolutePos(SURFACE));
        helper.assertTrue(target > 1, "Ice Spikes should accumulate more than one layer");
        whenRaining(helper, () -> {
            snow(helper);
            SnowTestHelper.assertLayers(helper, SURFACE, target);
        });
    }

    private static void winterSnowlogs(GameTestHelper helper) {
        helper.setBiome(Biomes.PLAINS);
        season(helper, Season.SubSeason.MID_WINTER);
        helper.setBlock(SURFACE.below(), Blocks.GRASS_BLOCK);
        helper.setBlock(SURFACE, Blocks.POPPY);
        whenRaining(helper, () -> {
            snow(helper);
            SnowTestHelper.assertLayers(helper, SURFACE, 1);
            BlockState plant = Snowlogging.plantAt(helper.getLevel(), helper.absolutePos(SURFACE));
            helper.assertTrue(plant != null && plant.is(Blocks.POPPY), "Poppy should be snowlogged by winter snowfall");
        });
    }

    private static void springMelts(GameTestHelper helper) {
        helper.setBiome(Biomes.PLAINS);
        season(helper, Season.SubSeason.LATE_SPRING);
        SnowTestHelper.place(helper, SURFACE, 3);
        helper.randomTick(SURFACE);
        SnowTestHelper.assertLayers(helper, SURFACE, 2);
        helper.succeed();
    }

    private static void winterKeepsSnow(GameTestHelper helper) {
        helper.setBiome(Biomes.PLAINS);
        season(helper, Season.SubSeason.MID_WINTER);
        SnowTestHelper.place(helper, SURFACE, 3);
        helper.randomTick(SURFACE);
        SnowTestHelper.assertLayers(helper, SURFACE, 3);
        helper.succeed();
    }

    private static void season(GameTestHelper helper, Season.SubSeason subSeason) {
        SeasonHandler.getSeasonSavedData(helper.getLevel()).seasonCycleTicks = subSeason.ordinal() * SeasonTime.ZERO.getSubSeasonDuration();
    }

    private static void snow(GameTestHelper helper) {
        for (int i = 0; i < 1024; i++) {
            helper.tickPrecipitation(SURFACE);
        }
    }

    private static void whenRaining(GameTestHelper helper, Runnable assertions) {
        helper.startSequence()
                .thenWaitUntil(() -> helper.assertTrue(helper.getLevel().isRaining(), "Rain should be falling"))
                .thenExecute(assertions)
                .thenSucceed();
    }
}
