package dev.imb11.snow.gametest;

import dev.imb11.snow.accumulation.SnowAccumulation;
import dev.imb11.snow.profile.SnowProfile;
import dev.imb11.snow.profile.SnowProfiles;
import dev.imb11.snow.gametest.SnowGameTests.TestDefinition;
import dev.imb11.snow.gametest.support.SnowTestHelper;
import dev.imb11.snow.util.SnowColumn;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.List;
import java.util.stream.Stream;

import static dev.imb11.snow.gametest.support.SnowTestEnvironments.SNOWFALL;
import static dev.imb11.snow.gametest.support.SnowTestEnvironments.SNOWFALL_CAPPED;
import static dev.imb11.snow.gametest.support.SnowTestHelper.PLATFORM;
import static dev.imb11.snow.gametest.support.SnowTestHelper.SURFACE;

public class AccumulationTests {
    private static final int FILL_ATTEMPTS = 1024;
    private static final double TOLERANCE = 1.0E-5;

    public static List<TestDefinition> all() {
        return List.of(
                new TestDefinition("accumulation/profiles", SNOWFALL, PLATFORM, 20, AccumulationTests::profiles),
                new TestDefinition("accumulation/snowfall_hook", SNOWFALL, PLATFORM, 100, AccumulationTests::snowfallHook),
                new TestDefinition("accumulation/vanilla_fallback", SNOWFALL, PLATFORM, 20, AccumulationTests::vanillaFallback),
                new TestDefinition("accumulation/gamerule_cap", SNOWFALL_CAPPED, PLATFORM, 20, AccumulationTests::gameruleCap),
                new TestDefinition("accumulation/pushes_entities", SNOWFALL, PLATFORM, 20, AccumulationTests::pushesEntities),
                new TestDefinition("accumulation/priority", SNOWFALL, PLATFORM, 20, AccumulationTests::priority)
        );
    }

    private static void profiles(GameTestHelper helper) {
        for (Holder<Biome> biome : SnowTestHelper.profiledBiomes(helper)) {
            SnowProfile profile = SnowTestHelper.setBiome(helper, biome);
            String name = biome.getRegisteredName();
            SnowTestHelper.clear(helper, SURFACE);
            int target = target(helper);
            helper.assertValueInBetween(profile.minLayers(), target, SnowColumn.MAX_LAYERS, name + " accumulation target");

            SnowAccumulation.accumulate(helper.getLevel(), helper.absolutePos(SURFACE));
            int highest = Math.min(target, Mth.ceil(profile.accumulationRate()));
            helper.assertValueInBetween(1, SnowTestHelper.layers(helper, SURFACE), highest, name + " layers after one snowfall tick");

            fill(helper);
            int expectedMax = profile.accumulationRate() > 0 ? target : 0;
            helper.assertValueEqual(SnowTestHelper.layers(helper, SURFACE), expectedMax, name + " maximum layers");
        }

        helper.succeed();
    }

    private static void snowfallHook(GameTestHelper helper) {
        SnowProfile profile = SnowTestHelper.setBiome(helper, SnowTestHelper.biome(helper, Biomes.ICE_SPIKES));
        helper.assertTrue(profile.minLayers() > 1, "Ice Spikes should accumulate more than one layer");
        int target = target(helper);
        helper.startSequence()
                .thenWaitUntil(() -> helper.assertTrue(helper.getLevel().isRaining(), "Rain should be falling"))
                .thenExecute(() -> {
                    for (int i = 0; i < FILL_ATTEMPTS; i++) {
                        helper.tickPrecipitation(SURFACE);
                    }

                    SnowTestHelper.assertLayers(helper, SURFACE, target);
                })
                .thenSucceed();
    }

    private static void vanillaFallback(GameTestHelper helper) {
        helper.setBiome(Biomes.PLAINS);
        fill(helper);
        SnowTestHelper.assertLayers(helper, SURFACE, 1);
        helper.succeed();
    }

    private static void gameruleCap(GameTestHelper helper) {
        SnowProfile profile = SnowTestHelper.setBiome(helper, SnowTestHelper.biome(helper, Biomes.ICE_SPIKES));
        int target = target(helper);
        helper.assertValueInBetween(profile.minLayers(), target, 5, "capped accumulation target");
        fill(helper);
        SnowTestHelper.assertLayers(helper, SURFACE, target);
        helper.succeed();
    }

    private static void pushesEntities(GameTestHelper helper) {
        SnowTestHelper.setBiome(helper, SnowTestHelper.biome(helper, Biomes.ICE_SPIKES));
        Pig pig = helper.spawnWithNoFreeWill(EntityTypes.PIG, SURFACE);
        fill(helper);
        double surface = SnowTestHelper.surfaceY(helper, SURFACE);
        helper.assertValueInBetween(surface - TOLERANCE, pig.getY(), surface + TOLERANCE, "pig height");
        helper.succeed();
    }

    private static void priority(GameTestHelper helper) {
        Holder<Biome> plains = SnowTestHelper.biome(helper, Biomes.PLAINS);
        SnowProfile low = new SnowProfile(HolderSet.direct(plains), 1, 2, 1f, SnowProfile.DEFAULT_DRIFT_STRENGTH, 0);
        SnowProfile high = new SnowProfile(HolderSet.direct(plains), 1, 6, 1f, SnowProfile.DEFAULT_DRIFT_STRENGTH, 5);
        SnowProfile resolved = SnowProfiles.resolve(Stream.of(low, high), plains).orElseThrow();
        helper.assertValueEqual(resolved, high, "resolved profile");
        helper.succeed();
    }

    private static int target(GameTestHelper helper) {
        return SnowAccumulation.target(helper.getLevel(), helper.absolutePos(SURFACE));
    }

    private static void fill(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(SURFACE);
        for (int i = 0; i < FILL_ATTEMPTS; i++) {
            SnowAccumulation.accumulate(helper.getLevel(), pos);
        }
    }
}
