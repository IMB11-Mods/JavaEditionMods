package dev.imb11.snow.gametest;

import dev.imb11.snow.accumulation.SnowProfile;
import dev.imb11.snow.gametest.SnowGameTests.TestDefinition;
import dev.imb11.snow.gametest.support.SnowTestHelper;
import dev.imb11.snow.generation.SnowGeneration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
//? if >=26.3 {
/*import net.minecraft.world.level.levelgen.feature.SnowAndFreezeFeature;
*///?} else {
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
//?}

import java.util.List;

import static dev.imb11.snow.gametest.support.SnowTestEnvironments.SNOWFALL;
import static dev.imb11.snow.gametest.support.SnowTestHelper.CHUNK_PLATFORM;
import static dev.imb11.snow.gametest.support.SnowTestHelper.PLATFORM;
import static dev.imb11.snow.gametest.support.SnowTestHelper.SURFACE;

public class GenerationTests {
    private static final int SAMPLES = 32;

    public static List<TestDefinition> all() {
        return List.of(
                new TestDefinition("generation/profiles", SNOWFALL, PLATFORM, 20, GenerationTests::profiles),
                new TestDefinition("generation/vanilla_fallback", SNOWFALL, PLATFORM, 20, GenerationTests::vanillaFallback),
                new TestDefinition("generation/feature_hook", SNOWFALL, CHUNK_PLATFORM, 20, GenerationTests::featureHook)
        );
    }

    private static void profiles(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(SURFACE);
        for (Holder<Biome> biome : SnowTestHelper.profiledBiomes(helper)) {
            SnowProfile profile = SnowTestHelper.setBiome(helper, biome);
            String name = biome.getRegisteredName();
            for (int i = 0; i < SAMPLES; i++) {
                SnowTestHelper.clear(helper, SURFACE);
                helper.assertTrue(SnowGeneration.place(level, pos, level.getRandom(), Block.UPDATE_ALL), name + " should generate snow from its profile");
                helper.assertValueInBetween(Math.max(1, profile.minLayers()), SnowTestHelper.layers(helper, SURFACE), profile.maxLayers(), name + " generated layers");
            }
        }

        helper.succeed();
    }

    private static void vanillaFallback(GameTestHelper helper) {
        helper.setBiome(Biomes.PLAINS);
        ServerLevel level = helper.getLevel();
        helper.assertFalse(SnowGeneration.place(level, helper.absolutePos(SURFACE), level.getRandom(), Block.UPDATE_ALL), "Plains has no profile, so vanilla generation should run");
        SnowTestHelper.assertLayers(helper, SURFACE, 0);
        helper.succeed();
    }

    private static void featureHook(GameTestHelper helper) {
        helper.setBiome(Biomes.ICE_SPIKES);
        ServerLevel level = helper.getLevel();
        BlockPos origin = helper.absolutePos(BlockPos.ZERO);
        //? if >=26.3 {
        /*new SnowAndFreezeFeature().place(level, level.getChunkSource().getGenerator(), level.getRandom(), origin);
        *///?} else {
        Feature.FREEZE_TOP_LAYER.place(NoneFeatureConfiguration.INSTANCE, level, level.getChunkSource().getGenerator(), level.getRandom(), origin);
        //?}

        int deepest = 0;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                deepest = Math.max(deepest, SnowTestHelper.layers(helper, new BlockPos(x, 1, z)));
            }
        }

        helper.assertTrue(deepest > 1, "The world generation feature should place multi-layer snow in Ice Spikes");
        helper.succeed();
    }
}
