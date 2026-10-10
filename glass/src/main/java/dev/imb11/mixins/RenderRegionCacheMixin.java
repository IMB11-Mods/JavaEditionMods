package dev.imb11.mixins;

import dev.imb11.client.renderer.projection.ProjectionRenderContext;
import dev.imb11.client.renderer.projection.ProjectionRenderRegion;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderRegionCache.class)
abstract class RenderRegionCacheMixin {
    @Inject(method = "createRegion", at = @At("RETURN"))
    private void glass$captureProjectionTerminal(
            CallbackInfoReturnable<RenderSectionRegion> callbackInfo
    ) {
        BlockPos hiddenTerrainBlock = ProjectionRenderContext.hiddenTerrainBlock();
        RenderSectionRegion region = callbackInfo.getReturnValue();
        if (hiddenTerrainBlock != null && region != null) {
            ((ProjectionRenderRegion) region).glass$setHiddenTerrainBlock(hiddenTerrainBlock);
        }
    }
}
