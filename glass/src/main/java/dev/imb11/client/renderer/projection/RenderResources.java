package dev.imb11.client.renderer.projection;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.SectionBufferBuilderPool;

public final class RenderResources {
    private RenderResources() {
    }

    public static void runOnRenderThread(Runnable operation) {
        if (RenderSystem.isOnRenderThread()) {
            operation.run();
        } else {
            net.minecraft.client.Minecraft.getInstance().execute(operation);
        }
    }

    public static void closeBufferBuilders(RenderBuffers renderBuffers) {
        renderBuffers.fixedBufferPack().close();
        renderBuffers.stagedVertexBuffer().close();
    }

    public static void closeAvailablePoolBuffers(SectionBufferBuilderPool pool) {
        SectionBufferBuilderPack pack;
        while ((pack = pool.acquire()) != null) {
            pack.close();
        }
    }
}
