package dev.imb11.snow.melting;

import dev.imb11.snow.config.SnowConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
//? if >=26.3 {
/*import net.minecraft.world.level.gameevent.GameEvent;
*///?}

public class SnowMelting {
    public static final int MELT_LIGHT_LEVEL = 11;

    public static boolean randomTick(BlockState state, ServerLevel level, BlockPos pos) {
        MeltingConfig config = SnowConfig.get().melting;
        if (!config.gradualMelting && !config.meltInWarmBiomes) {
            return false;
        }

        if (!shouldMelt(level, pos, config) || level.getBlockState(pos.above()).is(Blocks.SNOW)) {
            return true;
        }

        int layers = state.getValue(SnowLayerBlock.LAYERS);
        if (config.gradualMelting && layers > 1) {
            level.setBlockAndUpdate(pos, state.setValue(SnowLayerBlock.LAYERS, layers - 1));
        } else {
            Block.dropResources(state, level, pos);
            level.removeBlock(pos, false);
            //? if >=26.3 {
            /*level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(state));
            *///?}
        }

        return true;
    }

    private static boolean shouldMelt(ServerLevel level, BlockPos pos, MeltingConfig config) {
        if (level.getBrightness(LightLayer.BLOCK, pos) > MELT_LIGHT_LEVEL) {
            return true;
        }

        return config.meltInWarmBiomes && level.getBiome(pos).value().warmEnoughToRain(pos, level.getSeaLevel());
    }
}
