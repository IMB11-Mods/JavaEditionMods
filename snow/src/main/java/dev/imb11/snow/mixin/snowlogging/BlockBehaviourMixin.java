package dev.imb11.snow.mixin.snowlogging;

import dev.imb11.snow.snowlogging.Snowlogging;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiConsumer;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {
    @Inject(method = "canBeReplaced(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/item/context/BlockPlaceContext;)Z", at = @At("HEAD"), cancellable = true)
    private void snow$replaceWithSnow(BlockState state, BlockPlaceContext context, CallbackInfoReturnable<Boolean> cir) {
        if (Snowlogging.canPlaceInto(state, context)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "onExplosionHit", at = @At("HEAD"))
    private void snow$destroyPlant(BlockState state, ServerLevel level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> onHit, CallbackInfo ci) {
        if (state.is(Blocks.SNOW)) {
            Snowlogging.destroy(level, pos);
        }
    }
}
