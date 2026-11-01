package dev.imb11.snow.mixin.frost.compat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.imb11.snow.client.frost.FrostShader;
import dev.imb11.snow.client.frost.FrostedLeaves;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import net.caffeinemc.mods.sodium.client.render.chunk.vertex.builder.ChunkMeshBufferBuilder;
import net.caffeinemc.mods.sodium.client.render.chunk.vertex.format.ChunkVertexEncoder;
import net.caffeinemc.mods.sodium.client.render.model.MutableQuadViewImpl;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer", remap = false)
abstract class SodiumBlockRendererMixin {
    @Unique
    private @Nullable LevelSlice snow$level;
    @Unique
    private float snow$frost;

    @Inject(method = "prepare", at = @At("HEAD"))
    private void snow$captureLevel(ChunkBuildBuffers buffers, LevelSlice level, TranslucentGeometryCollector collector, CallbackInfo ci) {
        this.snow$level = level;
    }

    @Inject(method = "release", at = @At("HEAD"))
    private void snow$releaseLevel(CallbackInfo ci) {
        this.snow$level = null;
        this.snow$frost = 0.0F;
    }

    @Inject(method = "renderModel", at = @At("HEAD"))
    private void snow$measureFrost(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
        this.snow$frost = this.snow$level == null ? 0.0F : FrostedLeaves.weight(this.snow$level, state, pos);
        if (this.snow$frost > 0.0F) {
            FrostedLeaves.track(pos);
        }
    }

    @Inject(method = "tintQuad", at = @At("TAIL"))
    private void snow$bakeFrost(MutableQuadViewImpl quad, CallbackInfo ci) {
        if (this.snow$frost <= 0.0F || quad.getTintIndex() == -1 || FrostedLeaves.useShader()) {
            return;
        }

        float amount = this.snow$frost * FrostedLeaves.bakedProgress();
        if (amount > 0.0F) {
            for (int i = 0; i < 4; i++) {
                quad.setColor(i, FrostedLeaves.frost(quad.baseColor(i), amount));
            }
        }
    }

    @WrapOperation(method = "bufferQuad", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/vertex/builder/ChunkMeshBufferBuilder;push([Lnet/caffeinemc/mods/sodium/client/render/chunk/vertex/format/ChunkVertexEncoder$Vertex;I)V"))
    private void snow$flagFrost(ChunkMeshBufferBuilder buffer, ChunkVertexEncoder.Vertex[] vertices, int materialBits, Operation<Void> original, @Local(argsOnly = true) MutableQuadViewImpl quad) {
        if (this.snow$frost > 0.0F && quad.getTintIndex() != -1 && FrostedLeaves.useShader()) {
            for (ChunkVertexEncoder.Vertex vertex : vertices) {
                int shade = Mth.clamp(Math.round(vertex.ao * 255.0F), 0, 255);
                vertex.color = (vertex.color & 0x00FFFFFF) | (shade << 24);
            }
            materialBits |= FrostShader.encodeWeight(this.snow$frost);
        }

        original.call(buffer, vertices, materialBits);
    }
}
