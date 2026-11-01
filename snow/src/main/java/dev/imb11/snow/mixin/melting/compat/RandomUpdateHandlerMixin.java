package dev.imb11.snow.mixin.melting.compat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.imb11.snow.config.SnowConfig;
import dev.imb11.snow.melting.SnowMelting;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "sereneseasons.season.RandomUpdateHandler")
public class RandomUpdateHandlerMixin {
    @WrapOperation(method = "meltInChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private static boolean snow$meltGradually(ServerLevel level, BlockPos pos, BlockState state, Operation<Boolean> original) {
        BlockState current = level.getBlockState(pos);
        if (!current.is(Blocks.SNOW) || !SnowConfig.get().gradualMelting) {
            return original.call(level, pos, state);
        }

        SnowMelting.melt(level, pos, current);
        return true;
    }
}
