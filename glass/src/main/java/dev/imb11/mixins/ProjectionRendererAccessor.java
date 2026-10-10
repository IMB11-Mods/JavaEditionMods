package dev.imb11.mixins;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LevelRenderer.class)
public interface ProjectionRendererAccessor {
    @Accessor("targets")
    LevelTargetBundle glass$targets();

    @Accessor("skyRenderer")
    void glass$skyRenderer(SkyRenderer renderer);

    @Accessor("ticks")
    void glass$ticks(int ticks);

    @Invoker("addSkyPass")
    void glass$skyPass(FrameGraphBuilder frame, CameraRenderState camera, GpuBufferSlice fog);

    @Invoker("addMainPass")
    void glass$mainPass(FrameGraphBuilder frame, Frustum frustum, Matrix4fc modelView, GpuBufferSlice fog,
                        boolean outline, LevelRenderState state, DeltaTracker delta, ProfilerFiller profiler,
                        ChunkSectionsToRender chunks);

    @Invoker("addCloudsPass")
    void glass$cloudsPass(FrameGraphBuilder frame, CloudStatus status, Vec3 camera, long time,
                          float partialTick, int color, float height, int range);

    @Invoker("addWeatherPass")
    void glass$weatherPass(FrameGraphBuilder frame, GpuBufferSlice fog);
}
