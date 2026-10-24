package dev.imb11.snow.gametest;

import dev.imb11.snow.gametest.SnowGameTests.TestDefinition;
import dev.imb11.snow.gametest.support.SnowTestHelper;
import dev.imb11.snow.melting.SnowMelting;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.function.Consumer;

import static dev.imb11.snow.gametest.support.SnowTestEnvironments.MELTING;
import static dev.imb11.snow.gametest.support.SnowTestEnvironments.WARM_MELTING;
import static dev.imb11.snow.gametest.support.SnowTestHelper.PLATFORM;
import static dev.imb11.snow.gametest.support.SnowTestHelper.SURFACE;

public class MeltingTests {
    private static final int MAX_POLLED_TASKS = 64;

    public static List<TestDefinition> all() {
        return List.of(
                new TestDefinition("melting/gradual", MELTING, PLATFORM, 100, MeltingTests::gradual),
                new TestDefinition("melting/top_down", WARM_MELTING, PLATFORM, 20, MeltingTests::topDown),
                new TestDefinition("melting/warm_biome_off", MELTING, PLATFORM, 20, warmBiome(3)),
                new TestDefinition("melting/warm_biome_on", WARM_MELTING, PLATFORM, 20, warmBiome(2))
        );
    }

    private static void gradual(GameTestHelper helper) {
        helper.setBiome(Biomes.ICE_SPIKES);
        SnowTestHelper.place(helper, SURFACE, 5);
        helper.setBlock(SURFACE.east(), Blocks.GLOWSTONE);
        helper.startSequence()
                .thenWaitUntil(() -> {
                    for (int i = 0; i < MAX_POLLED_TASKS; i++) {
                        helper.getLevel().getChunkSource().pollTask();
                    }
                    helper.assertTrue(helper.getLevel().getBrightness(LightLayer.BLOCK, helper.absolutePos(SURFACE)) > SnowMelting.MELT_LIGHT_LEVEL, "Snow should be lit by the glowstone");
                })
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

    private static Consumer<GameTestHelper> warmBiome(int expected) {
        return helper -> {
            helper.setBiome(Biomes.PLAINS);
            SnowTestHelper.place(helper, SURFACE, 3);
            helper.randomTick(SURFACE);
            SnowTestHelper.assertLayers(helper, SURFACE, expected);
            helper.succeed();
        };
    }
}
