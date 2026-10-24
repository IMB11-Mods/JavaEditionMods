package dev.imb11.snow.gametest.support;

import dev.imb11.snow.Snow;
import dev.imb11.snow.accumulation.SnowProfile;
import dev.imb11.snow.accumulation.SnowProfiles;
import dev.imb11.snow.util.SnowColumn;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;

import java.util.List;

public class SnowTestHelper {
    public static final Identifier PLATFORM = Snow.id("gametest/platform");
    public static final Identifier CHUNK_PLATFORM = Snow.id("gametest/chunk_platform");
    public static final BlockPos SURFACE = new BlockPos(4, 1, 4);

    public static List<Holder<Biome>> profiledBiomes(GameTestHelper helper) {
        List<Holder<Biome>> biomes = helper.getLevel().registryAccess().lookupOrThrow(SnowProfiles.KEY).stream()
                .flatMap(profile -> profile.biomes().stream())
                .distinct()
                .toList();
        helper.assertFalse(biomes.isEmpty(), "No snow profiles are loaded");
        return biomes;
    }

    public static SnowProfile setBiome(GameTestHelper helper, Holder<Biome> biome) {
        helper.setBiome(biome.unwrapKey().orElseThrow());
        return SnowProfiles.get(helper.getLevel(), biome).orElseThrow();
    }

    public static int layers(GameTestHelper helper, BlockPos pos) {
        return SnowColumn.layers(helper.getLevel(), helper.absolutePos(pos));
    }

    public static double surfaceY(GameTestHelper helper, BlockPos pos) {
        BlockPos top = helper.absolutePos(layers(helper, pos) > SnowLayerBlock.MAX_HEIGHT ? pos.above() : pos);
        return top.getY() + helper.getLevel().getBlockState(top).getCollisionShape(helper.getLevel(), top).max(Direction.Axis.Y);
    }

    public static void assertLayers(GameTestHelper helper, BlockPos pos, int expected) {
        helper.assertValueEqual(layers(helper, pos), expected, "snow layers");
    }

    public static void place(GameTestHelper helper, BlockPos pos, int layers) {
        SnowColumn.place(helper.getLevel(), helper.absolutePos(pos), layers, Block.UPDATE_ALL, false);
    }

    public static void clear(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos.above(), Blocks.AIR);
        helper.setBlock(pos, Blocks.AIR);
    }
}
