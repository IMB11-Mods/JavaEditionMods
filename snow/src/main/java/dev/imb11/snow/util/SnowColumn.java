package dev.imb11.snow.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SnowColumn {
    public static final int MAX_LAYERS = 12;

    public static int layers(BlockGetter level, BlockPos base) {
        BlockState state = level.getBlockState(base);
        if (!state.is(Blocks.SNOW)) {
            return 0;
        }

        int layers = state.getValue(SnowLayerBlock.LAYERS);
        BlockState above = level.getBlockState(base.above());
        if (layers == SnowLayerBlock.MAX_HEIGHT && above.is(Blocks.SNOW)) {
            layers += above.getValue(SnowLayerBlock.LAYERS);
        }

        return layers;
    }

    public static void place(LevelAccessor level, BlockPos base, int layers, int flags, boolean pushEntities) {
        setLayers(level, base, Math.min(layers, SnowLayerBlock.MAX_HEIGHT), flags, pushEntities);
        if (layers > SnowLayerBlock.MAX_HEIGHT) {
            setLayers(level, base.above(), layers - SnowLayerBlock.MAX_HEIGHT, flags, pushEntities);
        }
    }

    private static void setLayers(LevelAccessor level, BlockPos pos, int layers, int flags, boolean pushEntities) {
        BlockState state = level.getBlockState(pos);
        BlockState newState = Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, layers);
        if (pushEntities) {
            Block.pushEntitiesUp(state, newState, level, pos);
        }

        level.setBlock(pos, newState, flags);
    }
}
