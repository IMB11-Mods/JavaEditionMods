package dev.imb11.snow.mixin.snowlogging;

import dev.imb11.snow.snowlogging.Snowlogging;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SnowLayerBlock.class)
public class SnowLayerBlockMixin {
    @Inject(method = "canBeReplaced", at = @At("HEAD"), cancellable = true)
    private void snow$replaceWithPlant(BlockState state, BlockPlaceContext context, CallbackInfoReturnable<Boolean> cir) {
        if (Snowlogging.canPlantInto(state, context)) {
            cir.setReturnValue(true);
        }
    }
}
