package dev.imb11.mixins;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.OptionsRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LevelRenderer.class)
public interface ProjectionRendererAccessor {
    @Accessor("targets")
    LevelTargetBundle glass$targets();

    @Accessor("submitNodeStorage")
    SubmitNodeStorage glass$submitNodeStorage();

    @Accessor("skyRenderer")
    void glass$skyRenderer(SkyRenderer renderer);

    @Mutable
    @Accessor("renderBuffers")
    void glass$renderBuffers(RenderBuffers buffers);

    @Mutable
    @Accessor("featureRenderDispatcher")
    void glass$featureRenderDispatcher(FeatureRenderDispatcher dispatcher);

    @Mutable
    @Accessor("levelRenderState")
    void glass$levelRenderState(LevelRenderState state);

    @Mutable
    @Accessor("optionsRenderState")
    void glass$optionsRenderState(OptionsRenderState state);

    @Invoker("submitFeatures")
    void glass$submitFeatures(LevelRenderState state, SubmitNodeCollector collector, boolean outline);

    @Invoker("addSkyPass")
    void glass$skyPass(FrameGraphBuilder frame, CameraRenderState camera, GpuBufferSlice fog);

    @Invoker("addMainPass")
    void glass$mainPass(FrameGraphBuilder frame, FeatureRenderDispatcher.PreparedFrame features, GpuBufferSlice fog,
                        LevelRenderState state, ProfilerFiller profiler, ChunkSectionsToRender chunks);

    @Invoker("addCloudsPass")
    void glass$cloudsPass(FrameGraphBuilder frame, CloudStatus status, Vec3 camera, long time,
                          float partialTick, int color, float height, int range);

    @Invoker("addWeatherPass")
    void glass$weatherPass(FrameGraphBuilder frame, GpuBufferSlice fog);
}
