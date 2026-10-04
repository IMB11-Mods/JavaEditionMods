package dev.imb11.fog.mixin.client.rendering;

import dev.imb11.fog.client.FogManager;
import dev.imb11.fog.client.compat.polytone.IrisCompat;
import dev.imb11.fog.client.util.math.EnvironmentCalculations;
import dev.imb11.fog.config.FogConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.imb11.fog.client.util.ChunkSectionUtil.CHUNK_SECTION_DIAMETER;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @Inject(method = "setupFog", at = @At("RETURN"), order = 900)
    private void fog$modifyFog(Camera camera, int renderDistance, DeltaTracker deltaTracker,
                              float darkenWorldAmount, ClientLevel level, CallbackInfoReturnable<FogData> cir) {
        if (!FogConfig.getInstance().enableMod || FogManager.isInDisabledBiome()
                || camera.getFluidInCamera() != FogType.NONE || IrisCompat.shouldDisableMod()) {
            return;
        }

        var manager = FogManager.getInstance();
        if (!manager.hasSetup) {
            return;
        }

        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
        var settings = manager.getFogSettings(partialTick, renderDistance);
        var client = Minecraft.getInstance();
        if (!level.dimensionType().hasFixedTime() || level.dimensionType().skybox() != DimensionType.Skybox.END) {
            settings = EnvironmentCalculations.apply(manager.getUndergroundFactor(client, partialTick), settings, partialTick);
        }

        var fog = cir.getReturnValue();
        fog.renderDistanceStart = (float) settings.fogStart() * CHUNK_SECTION_DIAMETER;
        fog.renderDistanceEnd = (float) settings.fogEnd() * CHUNK_SECTION_DIAMETER;
        fog.color.set(settings.fogRed(), settings.fogGreen(), settings.fogBlue(), 1.0F);
    }
}
