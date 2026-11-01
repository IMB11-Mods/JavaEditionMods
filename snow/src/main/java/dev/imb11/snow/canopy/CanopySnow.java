package dev.imb11.snow.canopy;

import dev.imb11.snow.config.SnowConfig;
import dev.imb11.snow.snowlogging.Snowlogging;
import dev.imb11.snow.util.SnowColumn;
import dev.imb11.snow.util.SnowSurface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jspecify.annotations.Nullable;

public class CanopySnow {
    public static final int MAX_LAYERS = 1;

    public static void snowfall(ServerLevel level, BlockPos top) {
        BlockPos ground = ground(level, top);
        if (ground != null && SnowColumn.layers(level, ground) < MAX_LAYERS) {
            SnowColumn.place(level, ground, MAX_LAYERS, Block.UPDATE_ALL, true);
        }
    }

    public static void generate(WorldGenLevel level, BlockPos top) {
        BlockPos ground = ground(level, top);
        if (ground == null) {
            return;
        }

        Snowlogging.snowlog(level, ground);
        SnowColumn.place(level, ground, MAX_LAYERS, Block.UPDATE_CLIENTS, false);
        BlockPos below = ground.below();
        BlockState belowState = level.getBlockState(below);
        if (belowState.hasProperty(SnowyBlock.SNOWY)) {
            level.setBlock(below, belowState.setValue(SnowyBlock.SNOWY, true), Block.UPDATE_CLIENTS);
        }
    }

    private static @Nullable BlockPos ground(LevelReader level, BlockPos top) {
        if (!SnowConfig.get().snowUnderTrees) {
            return null;
        }

        BlockPos.MutableBlockPos leaves = top.mutable().move(Direction.DOWN);
        while (level.getBlockState(leaves).is(Blocks.SNOW)) {
            leaves.move(Direction.DOWN);
        }

        if (!level.getBlockState(leaves).is(BlockTags.LEAVES)) {
            return null;
        }

        BlockPos ground = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, top);
        return ground.getY() < leaves.getY() && SnowSurface.shouldSnow(level.getBiome(ground).value(), level, ground) ? ground : null;
    }
}
