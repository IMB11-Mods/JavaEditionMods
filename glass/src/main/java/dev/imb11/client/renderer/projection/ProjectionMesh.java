package dev.imb11.client.renderer.projection;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public final class ProjectionMesh implements AutoCloseable {
    private final GpuBuffer vertices;
    private final int indexCount;

    public ProjectionMesh(MeshData mesh) {
        try (mesh) {
            vertices = RenderSystem.getDevice().createBuffer(() -> "GLASS mesh", GpuBuffer.USAGE_VERTEX, mesh.vertexBuffer());
            indexCount = mesh.drawState().indexCount();
        }
    }

    public void draw(RenderPipeline pipeline, Matrix4f pose, Map<String, GpuTextureView> textures, GpuBuffer parameters) {
        var target = Minecraft.getInstance().getMainRenderTarget();
        var indices = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
        var indexBuffer = indices.getBuffer(indexCount);
        var transforms = RenderSystem.getDynamicUniforms().writeTransform(pose, new Vector4f(1), new Vector3f(), new Matrix4f());
        try (var pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "GLASS projection surface",
                target.getColorTextureView(), OptionalInt.empty(), target.getDepthTextureView(), OptionalDouble.empty())) {
            pass.setPipeline(pipeline);
            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("DynamicTransforms", transforms);
            if (parameters != null) {
                pass.setUniform("SurfaceParameters", parameters);
            }
            textures.forEach((name, texture) -> pass.bindTexture(name, texture, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR)));
            pass.setVertexBuffer(0, vertices);
            pass.setIndexBuffer(indexBuffer, indices.type());
            pass.drawIndexed(0, 0, indexCount, 1);
        }
    }

    @Override
    public void close() {
        vertices.close();
    }
}
