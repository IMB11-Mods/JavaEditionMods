package dev.imb11.snow.generation;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SnowDrift {
    private static final int RADIUS = 2;

    public static float sample(BlockGetter level, BlockPos pos) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        if (!isGround(level.getBlockState(cursor.set(pos.getX(), pos.getY() - 1, pos.getZ())))) {
            return 0f;
        }

        int total = 0;
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                if (dx != 0 || dz != 0) {
                    total += step(level, cursor, pos.getX() + dx, pos.getY(), pos.getZ() + dz);
                }
            }
        }

        return (float) total / ((RADIUS * 2 + 1) * (RADIUS * 2 + 1) - 1);
    }

    private static int step(BlockGetter level, BlockPos.MutableBlockPos cursor, int x, int y, int z) {
        if (isGround(level.getBlockState(cursor.set(x, y, z)))) {
            return 1;
        }

        return isGround(level.getBlockState(cursor.set(x, y - 1, z))) ? 0 : -1;
    }

    private static boolean isGround(BlockState state) {
        if (!state.getFluidState().isEmpty()) {
            return true;
        }

        return state.isSolid() && !state.is(Blocks.SNOW) && !state.is(BlockTags.LEAVES) && !state.is(BlockTags.LOGS);
    }
}
