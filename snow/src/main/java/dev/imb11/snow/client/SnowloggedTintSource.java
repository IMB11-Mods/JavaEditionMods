package dev.imb11.snow.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SnowloggedTintSource implements BlockTintSource {
    private static final int WHITE = -1;

    @Override
    public int color(BlockState state) {
        return WHITE;
    }

    @Override
    public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        BlockState plant = SnowloggingClient.INSTANCE.plantAt(pos);
        if (plant == null) {
            return WHITE;
        }

        BlockTintSource source = Minecraft.getInstance().getBlockColors().getTintSource(plant, 0);
        return source == null ? WHITE : source.colorInWorld(plant, level, pos);
    }

    @Override
    public int colorAsTerrainParticle(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        return WHITE;
    }
}
