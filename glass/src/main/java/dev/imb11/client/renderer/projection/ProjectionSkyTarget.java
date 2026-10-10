package dev.imb11.client.renderer.projection;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.Minecraft;

final class ProjectionSkyTarget extends RenderTarget {
    ProjectionSkyTarget() {
        super("GLASS projection sky", true, GpuFormat.RGBA8_UNORM);
    }

    private static RenderTarget current() {
        return Minecraft.getInstance().gameRenderer.mainRenderTarget();
    }

    @Override
    public GpuTexture getColorTexture() {
        return current().getColorTexture();
    }

    @Override
    public GpuTextureView getColorTextureView() {
        return current().getColorTextureView();
    }

    @Override
    public GpuTexture getDepthTexture() {
        return current().getDepthTexture();
    }

    @Override
    public GpuTextureView getDepthTextureView() {
        return current().getDepthTextureView();
    }
}
