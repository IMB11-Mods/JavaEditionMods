package dev.imb11.mixins;

import dev.imb11.client.renderer.block.ProjectorBlockEntityRenderer;
import dev.imb11.client.renderer.projection.ProjectionRenderContext;
import dev.imb11.client.renderer.projection.ProjectionRenderManager;
import dev.imb11.client.renderer.projection.ProjectionSurfaceRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import dev.imb11.client.renderer.projection.ProjectionLightmap;
import com.mojang.blaze3d.textures.GpuTextureView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
abstract class GameRendererMixin {
    @Inject(method = {"lightmap", "levelLightmap"}, at = @At("HEAD"), cancellable = true)
    private void glass$projectionProjectionLightmap(CallbackInfoReturnable<GpuTextureView> callbackInfo) {
        ProjectionLightmap lightTexture = ProjectionRenderContext.lightTexture();
        if (lightTexture != null) {
            callbackInfo.setReturnValue(lightTexture.getTextureView());
        }
    }

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void glass$renderProjectionBeforeMain(DeltaTracker deltaTracker, CallbackInfo callbackInfo) {
        ProjectionRenderManager.renderBeforeMain((GameRenderer) (Object) this, deltaTracker);
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/renderer/state/level/CameraRenderState;Lorg/joml/Matrix4fc;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;ZLnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;)V", shift = At.Shift.AFTER))
    private void glass$renderProjectors(DeltaTracker deltaTracker, CallbackInfo callbackInfo) {
        var state = ((GameRenderer) (Object) this).getGameRenderState().levelRenderState.cameraRenderState;
        ProjectorBlockEntityRenderer.renderAll(new org.joml.Matrix4f(state.viewRotationMatrix));
    }

    @Inject(method = "resetData", at = @At("HEAD"))
    private void glass$resetProjectionRenderer(CallbackInfo callbackInfo) {
        ProjectionRenderManager.reset();
        ProjectionSurfaceRenderer.reset();
        ProjectorBlockEntityRenderer.reset();
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void glass$closeProjectionRenderer(CallbackInfo callbackInfo) {
        ProjectionRenderManager.reset();
        ProjectionSurfaceRenderer.reset();
        ProjectorBlockEntityRenderer.reset();
        ProjectorBlockEntityRenderer.clearLoaded();
    }
}
