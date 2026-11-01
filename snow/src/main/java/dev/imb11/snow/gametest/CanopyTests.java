package dev.imb11.snow.gametest;

import dev.imb11.snow.canopy.CanopySnow;
import dev.imb11.snow.gametest.SnowGameTests.TestDefinition;
import dev.imb11.snow.gametest.support.SnowTestHelper;
import dev.imb11.snow.snowlogging.Snowlogging;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SnowyBlock;
import net.minecraft.world.level.block.state.BlockState;
//? if >=26.3 {
/*import net.minecraft.world.level.levelgen.feature.SnowAndFreezeFeature;
*///?} else {
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
//?}

import java.util.List;

import static dev.imb11.snow.gametest.support.SnowTestEnvironments.CANOPY_OFF;
import static dev.imb11.snow.gametest.support.SnowTestEnvironments.SNOWFALL;
import static dev.imb11.snow.gametest.support.SnowTestHelper.CHUNK_PLATFORM;
import static dev.imb11.snow.gametest.support.SnowTestHelper.PLATFORM;
import static dev.imb11.snow.gametest.support.SnowTestHelper.SURFACE;

public class CanopyTests {
    private static final BlockPos CHUNK_SURFACE = new BlockPos(8, 1, 8);

    public static List<TestDefinition> all() {
        return List.of(
                new TestDefinition("canopy/snowfall", SNOWFALL, PLATFORM, 100, CanopyTests::snowfall),
                new TestDefinition("canopy/generation", SNOWFALL, CHUNK_PLATFORM, 20, CanopyTests::generation),
                new TestDefinition("canopy/snowlogs_plant", SNOWFALL, PLATFORM, 100, CanopyTests::snowlogsPlant),
                new TestDefinition("canopy/disabled", CANOPY_OFF, PLATFORM, 100, CanopyTests::disabled)
        );
    }

    private static void snowfall(GameTestHelper helper) {
        SnowTestHelper.setBiome(helper, SnowTestHelper.biome(helper, Biomes.ICE_SPIKES));
        BlockPos leaves = canopy(helper, SURFACE, 2);
        whenRaining(helper, () -> {
            snow(helper, SURFACE);
            SnowTestHelper.assertLayers(helper, SURFACE, CanopySnow.MAX_LAYERS);
            helper.assertBlockPresent(Blocks.SNOW, leaves.above());
        });
    }

    private static void generation(GameTestHelper helper) {
        SnowTestHelper.setBiome(helper, SnowTestHelper.biome(helper, Biomes.ICE_SPIKES));
        helper.setBlock(CHUNK_SURFACE.below(), Blocks.GRASS_BLOCK);
        BlockPos leaves = canopy(helper, CHUNK_SURFACE, 1);
        ServerLevel level = helper.getLevel();
        BlockPos origin = helper.absolutePos(BlockPos.ZERO);
        //? if >=26.3 {
        /*new SnowAndFreezeFeature().place(level, level.getChunkSource().getGenerator(), level.getRandom(), origin);
        *///?} else {
        Feature.FREEZE_TOP_LAYER.place(NoneFeatureConfiguration.INSTANCE, level, level.getChunkSource().getGenerator(), level.getRandom(), origin);
        //?}

        SnowTestHelper.assertLayers(helper, CHUNK_SURFACE, CanopySnow.MAX_LAYERS);
        helper.assertBlockProperty(CHUNK_SURFACE.below(), SnowyBlock.SNOWY, true);
        helper.assertBlockPresent(Blocks.SNOW, leaves.above());
        helper.succeed();
    }

    private static void snowlogsPlant(GameTestHelper helper) {
        SnowTestHelper.setBiome(helper, SnowTestHelper.biome(helper, Biomes.ICE_SPIKES));
        helper.setBlock(SURFACE.below(), Blocks.GRASS_BLOCK);
        helper.setBlock(SURFACE, Blocks.SHORT_GRASS);
        canopy(helper, SURFACE, 2);
        whenRaining(helper, () -> {
            snow(helper, SURFACE);
            SnowTestHelper.assertLayers(helper, SURFACE, CanopySnow.MAX_LAYERS);
            BlockState plant = Snowlogging.plantAt(helper.getLevel(), helper.absolutePos(SURFACE));
            helper.assertTrue(plant != null && plant.is(Blocks.SHORT_GRASS), "Grass under the canopy should be snowlogged");
        });
    }

    private static void disabled(GameTestHelper helper) {
        SnowTestHelper.setBiome(helper, SnowTestHelper.biome(helper, Biomes.ICE_SPIKES));
        BlockPos leaves = canopy(helper, SURFACE, 2);
        whenRaining(helper, () -> {
            snow(helper, SURFACE);
            SnowTestHelper.assertLayers(helper, SURFACE, 0);
            helper.assertBlockPresent(Blocks.SNOW, leaves.above());
        });
    }

    private static BlockPos canopy(GameTestHelper helper, BlockPos ground, int gap) {
        BlockPos leaves = ground.above(gap);
        helper.setBlock(leaves, Blocks.SPRUCE_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true));
        return leaves;
    }

    private static void snow(GameTestHelper helper, BlockPos pos) {
        for (int i = 0; i < 64; i++) {
            helper.tickPrecipitation(pos);
        }
    }

    private static void whenRaining(GameTestHelper helper, Runnable assertions) {
        helper.startSequence()
                .thenWaitUntil(() -> helper.assertTrue(helper.getLevel().isRaining(), "Rain should be falling"))
                .thenExecute(assertions)
                .thenSucceed();
    }
}
