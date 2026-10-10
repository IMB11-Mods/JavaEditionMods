package dev.imb11.mixins;

import dev.imb11.client.GlassClient;
import dev.imb11.client.renderer.block.ProjectorBlockEntityRenderer;
import dev.imb11.client.renderer.projection.ProjectionRenderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.extract.LevelExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelExtractor.class)
abstract class LevelExtractorMixin {
    @Inject(method = "allChanged", at = @At("HEAD"))
    private void glass$invalidateRenderState(CallbackInfo callbackInfo) {
        if ((Object) this == Minecraft.getInstance().levelExtractor) {
            GlassClient.invalidateRenderState();
        }
    }

    @Inject(method = "extract", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;invalidateCompiledGeometry(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/client/Options;Lnet/minecraft/client/Camera;Lnet/minecraft/client/color/block/BlockColors;)V", shift = At.Shift.AFTER))
    private void glass$mirrorMainRendererRebuild(CallbackInfo callbackInfo) {
        if ((Object) this == Minecraft.getInstance().levelExtractor) {
            ProjectorBlockEntityRenderer.onMainRendererRebuilt();
            ProjectionRenderManager.onMainRendererRebuilt();
        }
    }

    @Inject(method = "setSectionDirty(IIIZ)V", at = @At("TAIL"))
    private void glass$mirrorSectionDirty(int sectionX, int sectionY, int sectionZ, boolean playerChanged,
                                          CallbackInfo callbackInfo) {
        if ((Object) this == Minecraft.getInstance().levelExtractor) {
            ProjectionRenderManager.onMainSectionDirty(sectionX, sectionY, sectionZ);
        }
    }

    @Inject(method = "onResourceManagerReload", at = @At("HEAD"))
    private void glass$reload(CallbackInfo callbackInfo) {
        if ((Object) this == Minecraft.getInstance().levelExtractor) {
            GlassClient.onResourceReload();
        }
    }
}
