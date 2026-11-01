package dev.imb11.snow.mixin.accumulation.client;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LeavesBlock.class)
public class LeavesBlockMixin {
    @Inject(method = "animateTick", at = @At("TAIL"))
    private void snow$dropSnowThroughLeaves(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (random.nextInt(8) == 0 && level.getBlockState(pos.above()).is(Blocks.SNOW)) {
            ParticleUtils.spawnParticleBelow(level, pos, random, ParticleTypes.SNOWFLAKE);
        }
    }
}
