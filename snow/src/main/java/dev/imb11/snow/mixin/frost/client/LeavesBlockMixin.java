package dev.imb11.snow.mixin.frost.client;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LeavesBlock.class)
public class LeavesBlockMixin {
    @Unique
    private static final int SNOW_PARTICLE_CHANCE = 8;

    @Inject(method = "animateTick", at = @At("TAIL"))
    private void snow$dropSnowThroughLeaves(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (random.nextInt(SNOW_PARTICLE_CHANCE) == 0 && level.getBlockState(pos.above()).is(Blocks.SNOW)) {
            ParticleUtils.spawnParticleBelow(level, pos, random, ParticleTypes.SNOWFLAKE);
        }
    }
}
