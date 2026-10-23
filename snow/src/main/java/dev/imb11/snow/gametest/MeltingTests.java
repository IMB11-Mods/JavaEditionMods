package dev.imb11.snow.gametest;

import dev.imb11.snow.gametest.SnowGameTests.TestDefinition;
import dev.imb11.snow.gametest.support.SnowTestHelper;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

import static dev.imb11.snow.gametest.support.SnowTestEnvironments.MELTING;
import static dev.imb11.snow.gametest.support.SnowTestEnvironments.WARM_MELTING;
import static dev.imb11.snow.gametest.support.SnowTestHelper.PLATFORM;
import static dev.imb11.snow.gametest.support.SnowTestHelper.SURFACE;

public class MeltingTests {
    public static List<TestDefinition> all() {
        return List.of(
                new TestDefinition("melting/gradual", MELTING, PLATFORM, 20, MeltingTests::gradual),
                new TestDefinition("melting/top_down", WARM_MELTING, PLATFORM, 20, MeltingTests::topDown),
                new TestDefinition("melting/warm_biome_off", MELTING, PLATFORM, 20, MeltingTests::warmBiomeOff),
                new TestDefinition("melting/warm_biome_on", WARM_MELTING, PLATFORM, 20, MeltingTests::warmBiomeOn)
        );
    }

    private static void gradual(GameTestHelper helper) {
        helper.setBiome(Biomes.ICE_SPIKES);
        SnowTestHelper.place(helper, SURFACE, 5);
        helper.setBlock(SURFACE.east(), Blocks.GLOWSTONE);
        helper.startSequence()
                .thenWaitUntil(() -> helper.assertTrue(helper.getLevel().getBrightness(LightLayer.BLOCK, helper.absolutePos(SURFACE)) > 11, "Snow should be lit by the glowstone"))
                .thenExecute(() -> {
                    helper.randomTick(SURFACE);
                    SnowTestHelper.assertLayers(helper, SURFACE, 4);
                })
                .thenSucceed();
    }

    private static void topDown(GameTestHelper helper) {
        helper.setBiome(Biomes.PLAINS);
        SnowTestHelper.place(helper, SURFACE, 12);
        helper.randomTick(SURFACE);
        SnowTestHelper.assertLayers(helper, SURFACE, 12);
        helper.randomTick(SURFACE.above());
        SnowTestHelper.assertLayers(helper, SURFACE, 11);
        helper.succeed();
    }

    private static void warmBiomeOff(GameTestHelper helper) {
        helper.setBiome(Biomes.PLAINS);
        SnowTestHelper.place(helper, SURFACE, 3);
        helper.randomTick(SURFACE);
        SnowTestHelper.assertLayers(helper, SURFACE, 3);
        helper.succeed();
    }

    private static void warmBiomeOn(GameTestHelper helper) {
        helper.setBiome(Biomes.PLAINS);
        SnowTestHelper.place(helper, SURFACE, 3);
        helper.randomTick(SURFACE);
        SnowTestHelper.assertLayers(helper, SURFACE, 2);
        helper.succeed();
    }
}
