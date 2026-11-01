package dev.imb11.snow.gametest;

import dev.imb11.snow.profile.SnowProfile;
import dev.imb11.snow.gametest.SnowGameTests.TestDefinition;
import dev.imb11.snow.gametest.support.SnowTestHelper;
import dev.imb11.snow.generation.SnowDrift;
import dev.imb11.snow.generation.SnowGeneration;
import dev.imb11.snow.util.SnowColumn;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
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
    private static final int CENTRE_MIN = 4;
    private static final int CENTRE_MAX = 11;
    private static final int PLATFORM_SIZE = 9;
    private static final int DRIFT_BASE_LAYERS = 2;
    private static final int STEP_X = 5;
    private static final BlockPos FLAT = new BlockPos(2, 1, 4);
    private static final BlockPos BELOW_STEP = new BlockPos(STEP_X - 1, 1, 4);
    private static final BlockPos CANOPY = new BlockPos(1, 3, 1);

    public static List<TestDefinition> all() {
        return List.of(
                new TestDefinition("generation/profiles", SNOWFALL, PLATFORM, 20, GenerationTests::profiles),
                new TestDefinition("generation/vanilla_fallback", SNOWFALL, PLATFORM, 20, GenerationTests::vanillaFallback),
                new TestDefinition("generation/feature_hook", SNOWFALL, CHUNK_PLATFORM, 20, GenerationTests::featureHook),
                new TestDefinition("generation/drift", SNOWFALL, PLATFORM, 20, GenerationTests::drift),
                new TestDefinition("generation/blocked_overflow", SNOWFALL, PLATFORM, 20, GenerationTests::blockedOverflow)
        );
    }

    private static void profiles(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(SURFACE);
        for (Holder<Biome> biome : SnowTestHelper.profiledBiomes(helper)) {
            SnowProfile profile = SnowTestHelper.setBiome(helper, biome);
            String name = biome.getRegisteredName();
            for (int i = 0; i < 32; i++) {
                SnowTestHelper.clear(helper, SURFACE);
                helper.assertTrue(SnowGeneration.place(level, pos, Block.UPDATE_ALL), name + " should generate snow from its profile");
                helper.assertValueInBetween(profile.minLayers(), SnowTestHelper.layers(helper, SURFACE), profile.maxLayers(), name + " generated layers");
            }
        }

        helper.succeed();
    }

    private static void vanillaFallback(GameTestHelper helper) {
        helper.setBiome(Biomes.PLAINS);
        ServerLevel level = helper.getLevel();
        helper.assertFalse(SnowGeneration.place(level, helper.absolutePos(SURFACE), Block.UPDATE_ALL), "Plains has no profile, so vanilla generation should run");
        SnowTestHelper.assertLayers(helper, SURFACE, 0);
        helper.succeed();
    }

    private static void featureHook(GameTestHelper helper) {
        SnowProfile profile = SnowTestHelper.setBiome(helper, SnowTestHelper.biome(helper, Biomes.ICE_SPIKES));
        ServerLevel level = helper.getLevel();
        BlockPos origin = helper.absolutePos(BlockPos.ZERO);
        //? if >=26.3 {
        /*new SnowAndFreezeFeature().place(level, level.getChunkSource().getGenerator(), level.getRandom(), origin);
        *///?} else {
        Feature.FREEZE_TOP_LAYER.place(NoneFeatureConfiguration.INSTANCE, level, level.getChunkSource().getGenerator(), level.getRandom(), origin);
        //?}

        for (int x = CENTRE_MIN; x <= CENTRE_MAX; x++) {
            for (int z = CENTRE_MIN; z <= CENTRE_MAX; z++) {
                helper.assertValueInBetween(profile.minLayers(), SnowTestHelper.layers(helper, new BlockPos(x, 1, z)), profile.maxLayers(), "generated layers at " + x + ", " + z);
            }
        }

        helper.succeed();
    }

    private static void drift(GameTestHelper helper) {
        SnowProfile profile = new SnowProfile(HolderSet.empty(), DRIFT_BASE_LAYERS, DRIFT_BASE_LAYERS, 1f, 4f, 0);
        ServerLevel level = helper.getLevel();
        for (int x = STEP_X; x < PLATFORM_SIZE; x++) {
            for (int z = 0; z < PLATFORM_SIZE; z++) {
                helper.setBlock(new BlockPos(x, 1, z), Blocks.STONE);
            }
        }

        helper.setBlock(CANOPY.below(), Blocks.OAK_LEAVES);

        helper.assertValueEqual(SnowDrift.sample(level, helper.absolutePos(FLAT)), 0f, "drift on flat ground");
        helper.assertTrue(SnowDrift.sample(level, helper.absolutePos(BELOW_STEP)) > 0f, "Snow should drift against the foot of a step");
        helper.assertTrue(SnowDrift.sample(level, helper.absolutePos(new BlockPos(STEP_X, 2, 4))) < 0f, "Snow should thin on the edge of a step");
        helper.assertValueEqual(SnowDrift.sample(level, helper.absolutePos(CANOPY)), 0f, "drift on leaves");

        int flat = SnowGeneration.layers(level, helper.absolutePos(FLAT), profile);
        int below = SnowGeneration.layers(level, helper.absolutePos(BELOW_STEP), profile);
        helper.assertTrue(below > flat, "The foot of a step should generate deeper snow than flat ground");
        helper.assertValueEqual(SnowGeneration.layers(level, helper.absolutePos(FLAT), profile), flat, "repeated generation layers");
        helper.succeed();
    }

    private static void blockedOverflow(GameTestHelper helper) {
        helper.setBlock(SURFACE.above(), Blocks.OAK_LEAVES);
        SnowTestHelper.place(helper, SURFACE, SnowColumn.MAX_LAYERS);
        SnowTestHelper.assertLayers(helper, SURFACE, SnowLayerBlock.MAX_HEIGHT);
        helper.assertBlockPresent(Blocks.OAK_LEAVES, SURFACE.above());
        helper.succeed();
    }
}
