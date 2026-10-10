package dev.imb11.mixins;

import dev.imb11.client.renderer.block.ProjectorBlockEntityRenderer;
import dev.imb11.client.GlassClient;
import dev.imb11.client.renderer.projection.ProjectionRenderContext;
import dev.imb11.client.renderer.projection.ProjectionRenderManager;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelRenderer.class, priority = 2000)
abstract class LevelRendererMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void glass$mirrorMainRendererTick(CallbackInfo callbackInfo) {
        ProjectionRenderManager.onMainRendererTick((LevelRenderer) (Object) this);
    }

    @Inject(method = "allChanged", at = @At("HEAD"))
    private void glass$invalidateRenderState(CallbackInfo callbackInfo) {
        GlassClient.invalidateRenderState();
    }

    @Inject(method = "allChanged", at = @At("TAIL"))
    private void glass$mirrorMainRendererRebuild(CallbackInfo callbackInfo) {
        ProjectorBlockEntityRenderer.onMainRendererRebuilt((LevelRenderer) (Object) this);
        ProjectionRenderManager.onMainRendererRebuilt((LevelRenderer) (Object) this);
    }

    @Inject(method = "setSectionDirty(IIIZ)V", at = @At("TAIL"))
    private void glass$mirrorSectionDirty(
            int sectionX,
            int sectionY,
            int sectionZ,
            boolean playerChanged,
            CallbackInfo callbackInfo
    ) {
        ProjectionRenderManager.onMainSectionDirty(
                (LevelRenderer) (Object) this,
                sectionX,
                sectionY,
                sectionZ
        );
    }

    @Inject(method = "onChunkReadyToRender", at = @At("TAIL"))
    private void glass$mirrorChunkLoaded(ChunkPos chunkPos, CallbackInfo callbackInfo) {
        ProjectionRenderManager.onMainChunkLoaded((LevelRenderer) (Object) this, chunkPos);
    }
    @Inject(method = "onResourceManagerReload", at = @At("HEAD"))
    private void glass$reload(CallbackInfo callbackInfo) {
        if ((Object) this == Minecraft.getInstance().levelRenderer) {
            GlassClient.onResourceReload();
        }
    }

    @Inject(method = "extractVisibleBlockEntities", at = @At("HEAD"), cancellable = true)
    private void glass$extractProjectionBlockEntities(Camera camera, float partialTick,
            net.minecraft.client.renderer.state.level.LevelRenderState state, CallbackInfo callbackInfo) {
        if ((Object) this instanceof dev.imb11.client.renderer.projection.ProjectionLevelRenderer renderer) {
            renderer.extractProjectionBlockEntities(partialTick, state);
            callbackInfo.cancel();
        }
    }
}
