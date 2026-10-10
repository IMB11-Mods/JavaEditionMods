package dev.imb11.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class RedstoneInfusedSandBlock extends ColoredFallingBlock {
    public static final ColorRGBA DUST_COLOR = new ColorRGBA(0xCF2929);
    public static final MapCodec<RedstoneInfusedSandBlock> CODEC = simpleCodec(RedstoneInfusedSandBlock::new);
    public static final int SIGNAL_STRENGTH = 1;

    public RedstoneInfusedSandBlock(Properties properties) {
        super(DUST_COLOR, properties);
    }

    @Override
    public MapCodec<? extends ColoredFallingBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return SIGNAL_STRENGTH;
    }
}
