package dev.imb11.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.math.Axis;
import dev.imb11.blocks.GBlocks;
import dev.imb11.blocks.TerminalBlock;
import dev.imb11.blocks.entity.TerminalBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public final class TerminalBlockEntityRenderer implements BlockEntityRenderer<TerminalBlockEntity, TerminalBlockEntityRenderer.State> {
    private BlockStateModel bakedModel;
    private List<BakedQuad> frame = List.of();
    private List<BakedQuad> eye = List.of();
    private final Vector3f eyeCenter = new Vector3f();

    public TerminalBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(TerminalBlockEntity entity, State state, float partialTick, Vec3 camera,
                                   ModelFeatureRenderer.CrumblingOverlay breaking) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, camera, breaking);
        BlockStateModel model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(
                GBlocks.TERMINAL.defaultBlockState().setValue(TerminalBlock.FACING, Direction.SOUTH));
        if (model != bakedModel) {
            rebuild(model);
        }
        state.frame = frame;
        state.eye = eye;
        state.eyeCenter.set(eyeCenter);
        state.rotation.set(entity.getEyeRotation(partialTick));
        state.facing = entity.getBlockState().getValue(TerminalBlock.FACING);
    }

    @Override
    public void submit(State state, PoseStack matrices, SubmitNodeCollector collector, CameraRenderState camera) {
        matrices.pushPose();
        matrices.translate(0.5D, 0.5D, 0.5D);
        switch (state.facing) {
            case NORTH -> matrices.mulPose(Axis.YP.rotationDegrees(-180.0F));
            case WEST -> matrices.mulPose(Axis.YP.rotationDegrees(-90.0F));
            case EAST -> matrices.mulPose(Axis.YP.rotationDegrees(-270.0F));
            case UP -> matrices.mulPose(Axis.XP.rotationDegrees(-90.0F));
            case DOWN -> {
                matrices.mulPose(Axis.YP.rotationDegrees(-180.0F));
                matrices.mulPose(Axis.XP.rotationDegrees(-270.0F));
            }
            default -> {}
        }
        matrices.translate(-0.5D, -0.5D, -0.5D);
        submitQuads(state.frame, matrices, collector, state.lightCoords);
        matrices.translate(state.eyeCenter.x(), state.eyeCenter.y(), state.eyeCenter.z());
        matrices.mulPose(state.rotation);
        matrices.translate(-state.eyeCenter.x(), -state.eyeCenter.y(), -state.eyeCenter.z());
        submitQuads(state.eye, matrices, collector, state.lightCoords);
        matrices.popPose();
    }

    private void rebuild(BlockStateModel model) {
        List<BakedQuad> frame = new ArrayList<>();
        List<BakedQuad> eye = new ArrayList<>();
        List<BlockStateModelPart> parts = new ArrayList<>();
        model.collectParts(RandomSource.create(42L), parts);
        for (BlockStateModelPart part : parts) {
            List<BakedQuad> quads = new ArrayList<>(part.getQuads(null));
            for (Direction direction : Direction.values()) {
                quads.addAll(part.getQuads(direction));
            }
            for (BakedQuad quad : quads) {
                (quad.materialInfo().tintIndex() == 1 ? eye : frame).add(quad);
            }
        }
        eyeCenter.zero();
        for (BakedQuad quad : eye) {
            for (int vertex = 0; vertex < 4; vertex++) {
                eyeCenter.add(quad.position(vertex));
            }
        }
        if (!eye.isEmpty()) {
            eyeCenter.div(eye.size() * 4);
        }
        this.frame = List.copyOf(frame);
        this.eye = List.copyOf(eye);
        bakedModel = model;
    }

    private static void submitQuads(List<BakedQuad> quads, PoseStack matrices, SubmitNodeCollector collector, int light) {
        collector.submitCustomGeometry(matrices, RenderTypes.entityCutout(TextureAtlas.LOCATION_BLOCKS), (pose, vertices) -> {
            QuadInstance instance = new QuadInstance();
            instance.setColor(-1);
            instance.setLightCoords(light);
            instance.setOverlayCoords(OverlayTexture.NO_OVERLAY);
            for (BakedQuad quad : quads) {
                vertices.putBakedQuad(pose, quad, instance);
            }
        });
    }

    public static final class State extends BlockEntityRenderState {
        private List<BakedQuad> frame = List.of();
        private List<BakedQuad> eye = List.of();
        private final Vector3f eyeCenter = new Vector3f();
        private final Quaternionf rotation = new Quaternionf();
        private Direction facing = Direction.SOUTH;
    }
}
