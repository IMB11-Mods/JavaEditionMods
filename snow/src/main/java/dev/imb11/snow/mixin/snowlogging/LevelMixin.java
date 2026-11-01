package dev.imb11.snow.mixin.snowlogging;

import com.llamalad7.mixinextras.sugar.Local;
import dev.imb11.snow.snowlogging.Snowlogging;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Level.class)
public class LevelMixin {
    @ModifyVariable(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At("HEAD"), argsOnly = true)
    private BlockState snow$snowlog(BlockState state, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true, ordinal = 0) int flags) {
        return Snowlogging.setBlock((Level) (Object) this, pos, state, flags);
    }
}
