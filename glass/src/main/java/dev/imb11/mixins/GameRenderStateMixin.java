package dev.imb11.mixins;

import dev.imb11.client.renderer.projection.ProjectionRenderContext;
import net.minecraft.client.renderer.state.GameRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderState.class)
abstract class GameRenderStateMixin {
    @Inject(method = "useShaderTransparency", at = @At("HEAD"), cancellable = true)
    private void glass$useForwardTransparency(CallbackInfoReturnable<Boolean> callbackInfo) {
        if (ProjectionRenderContext.isActive()) {
            callbackInfo.setReturnValue(false);
        }
    }
}
