package dev.imb11.client.renderer.projection;

import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.ProjectionType;
import dev.imb11.mixins.LevelRendererBufferAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.state.GameRenderState;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Util;
import net.minecraft.world.level.ChunkPos;
import org.joml.Matrix4f;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.imb11.mixins.ProjectionRendererAccessor;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Camera;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.renderer.DynamicUniforms;
import net.minecraft.client.renderer.GlobalSettingsUniform;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.chunk.SectionMesh;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Matrix4fc;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class ProjectionLevelRenderer extends LevelRenderer {
    private final Minecraft minecraft;
    private final RenderBuffers buffers;
    private final int renderDistance;
    private final GameRenderState state;
    private final FeatureRenderDispatcher features;
    private final FogRenderer fog = new FogRenderer();
    private final ProjectionMatrixBuffer projectionBuffer = new ProjectionMatrixBuffer("GLASS projection");
    private final GlobalSettingsUniform globals = new GlobalSettingsUniform();
    private ClientLevel projectionLevel;
    private SectionRenderDispatcher dispatcher;
    private ChunkPos gridCenter;

    public ProjectionLevelRenderer(Minecraft minecraft, RenderBuffers buffers, int renderDistance) {
        this(minecraft, buffers, renderDistance, new GameRenderState());
    }

    private ProjectionLevelRenderer(Minecraft minecraft, RenderBuffers buffers, int renderDistance, GameRenderState state) {
        this(minecraft, buffers, renderDistance, state, new FeatureRenderDispatcher(new SubmitNodeStorage(),
                minecraft.getModelManager(), buffers.bufferSource(), minecraft.getAtlasManager(),
                buffers.outlineBufferSource(), buffers.crumblingBufferSource(), minecraft.font, state));
    }

    private ProjectionLevelRenderer(Minecraft minecraft, RenderBuffers buffers, int renderDistance,
                                    GameRenderState state, FeatureRenderDispatcher features) {
        super(minecraft, minecraft.getEntityRenderDispatcher(), minecraft.getBlockEntityRenderDispatcher(), buffers, state, features);
        this.minecraft = minecraft;
        this.buffers = buffers;
        this.renderDistance = renderDistance;
        this.state = state;
        this.features = features;
        ((ProjectionRendererAccessor) this).glass$skyRenderer(new SkyRenderer(minecraft.getTextureManager(), minecraft.getAtlasManager()));
    }

    @Override
    public void setLevel(ClientLevel level) {
        LevelRendererBufferAccessor accessor = (LevelRendererBufferAccessor) this;
        ViewArea area = accessor.glass$getViewArea();
        if (area != null) {
            area.releaseAllBuffers();
            accessor.glass$setViewArea(null);
        }
        if (dispatcher != null) {
            dispatcher.dispose();
            dispatcher = null;
            accessor.glass$setSectionRenderDispatcher(null);
        }
        accessor.glass$getVisibleSections().clear();
        projectionLevel = level;
        accessor.glass$setLevel(level);
        gridCenter = null;
        if (level != null) {
            allChanged();
        }
    }

    @Override
    public void allChanged() {
        if (projectionLevel == null) {
            return;
        }
        LevelRendererBufferAccessor accessor = (LevelRendererBufferAccessor) this;
        if (accessor.glass$getViewArea() != null) {
            accessor.glass$getViewArea().releaseAllBuffers();
        }
        var models = minecraft.getModelManager();
        SectionCompiler compiler = new SectionCompiler(minecraft.options.ambientOcclusion().get(),
                minecraft.options.cutoutLeaves().get(), models.getBlockStateModelSet(), models.getFluidStateModelSet(),
                minecraft.getBlockColors(), minecraft.getBlockEntityRenderDispatcher());
        if (dispatcher == null) {
            dispatcher = new SectionRenderDispatcher(projectionLevel, this, Util.backgroundExecutor(), buffers, compiler);
            accessor.glass$setSectionRenderDispatcher(dispatcher);
        } else {
            dispatcher.clearCompileQueue();
            dispatcher.setLevel(projectionLevel, compiler);
        }
        accessor.glass$getVisibleSections().clear();
        accessor.glass$setViewArea(new ViewArea(dispatcher, projectionLevel, renderDistance, this));
        gridCenter = null;
    }

    public void prepareCamera(ProjectionCamera camera) {
        ChunkPos center = camera.gridCenter();
        if (!center.equals(gridCenter)) {
            ((LevelRendererBufferAccessor) this).glass$getViewArea().repositionCamera(
                    SectionPos.of(center.x(), SectionPos.blockToSectionCoord(camera.position().y), center.z()));
            gridCenter = center;
        }
        dispatcher.setCameraPosition(camera.position());
    }

    public void renderScene(ProjectionCamera camera, Matrix4f projection, DeltaTracker deltaTracker) {
        var savedProjection = RenderSystem.getProjectionMatrixBuffer();
        var savedFog = RenderSystem.getShaderFog();
        var savedGlobals = RenderSystem.getGlobalSettingsUniform();
        var options = state.optionsRenderState;
        var mainOptions = minecraft.gameRenderer.getGameRenderState().optionsRenderState;
        options.cloudStatus = mainOptions.cloudStatus;
        options.cloudRange = mainOptions.cloudRange;
        options.renderDistance = renderDistance;
        options.ambientOcclusion = mainOptions.ambientOcclusion;
        options.cutoutLeaves = mainOptions.cutoutLeaves;
        options.textureFiltering = mainOptions.textureFiltering;
        options.maxAnisotropyValue = mainOptions.maxAnisotropyValue;
        options.improvedTransparency = false;
        options.cameraType = mainOptions.cameraType;
        options.glintSpeed = mainOptions.glintSpeed;
        options.glintStrength = mainOptions.glintStrength;
        options.textBackgroundOpacity = mainOptions.textBackgroundOpacity;
        options.backgroundForChatOnly = mainOptions.backgroundForChatOnly;
        var cameraState = state.levelRenderState.cameraRenderState;
        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
        camera.extractRenderState(cameraState, partialTick);
        cameraState.projectionMatrix.set(projection);
        cameraState.viewRotationMatrix.set(camera.getViewRotationMatrix(new Matrix4f()));
        cameraState.cullFrustum = camera.getCullFrustum();
        cameraState.initialized = true;
        cameraState.depthFar = renderDistance * 64.0F;
        cameraState.fogData = fog.setupFog(camera, renderDistance, deltaTracker, 0.0F, projectionLevel);
        state.levelRenderState.reset();
        try {
            extractLevel(deltaTracker, camera, partialTick);
            state.levelRenderState.particlesRenderState.reset();
            fog.updateBuffer(cameraState.fogData);
            RenderSystem.setProjectionMatrix(projectionBuffer.getBuffer(projection), ProjectionType.PERSPECTIVE);
            var target = minecraft.getMainRenderTarget();
            globals.update(target.width, target.height, mainOptions.glintStrength, projectionLevel.getGameTime(),
                    deltaTracker, 0, camera.position(), false);
            renderProjectionLevel(deltaTracker);
        } finally {
            features.clearSubmitNodes();
            features.endFrame();
            endFrame();
            fog.endFrame();
            RenderSystem.setProjectionMatrix(savedProjection, ProjectionType.PERSPECTIVE);
            RenderSystem.setShaderFog(savedFog);
            RenderSystem.setGlobalSettingsUniform(savedGlobals);
            minecraft.getEntityRenderDispatcher().prepare(minecraft.gameRenderer.getMainCamera(), minecraft.crosshairPickEntity);
            minecraft.getBlockEntityRenderDispatcher().prepare(minecraft.gameRenderer.getMainCamera().position());
        }
    }

    private void renderProjectionLevel(DeltaTracker delta) {
        var access = (ProjectionRendererAccessor) this;
        var targets = access.glass$targets();
        var levelState = state.levelRenderState;
        var camera = levelState.cameraRenderState;
        var terrainFog = fog.getBuffer(FogRenderer.FogMode.WORLD);
        levelState.gameTime = projectionLevel.getGameTime();
        var modelView = RenderSystem.getModelViewStack();
        modelView.pushMatrix();
        modelView.mul(camera.viewRotationMatrix);
        try {
            var frame = new FrameGraphBuilder();
            var target = minecraft.getMainRenderTarget();
            targets.main = frame.importExternal("main", target);
            var clear = frame.addPass("clear");
            targets.main = clear.readsAndWrites(targets.main);
            clear.executes(() -> RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(
                    target.getColorTexture(), ARGB.colorFromFloat(1.0F, camera.fogData.color.x, camera.fogData.color.y,
                    camera.fogData.color.z), target.getDepthTexture(), 1.0));
            access.glass$skyPass(frame, camera, terrainFog);
            access.glass$mainPass(frame, camera.cullFrustum, camera.viewRotationMatrix, terrainFog, false,
                    levelState, delta, Profiler.get(), levelState.chunkSectionsToRender);
            if (state.optionsRenderState.cloudStatus != CloudStatus.OFF && ARGB.alpha(levelState.cloudColor) > 0) {
                access.glass$cloudsPass(frame, state.optionsRenderState.cloudStatus, camera.pos, levelState.gameTime,
                        delta.getGameTimeDeltaPartialTick(false), levelState.cloudColor, levelState.cloudHeight,
                        state.optionsRenderState.cloudRange);
            }
            access.glass$weatherPass(frame, terrainFog);
            frame.execute(GraphicsResourceAllocator.UNPOOLED);
        } finally {
            targets.clear();
            modelView.popMatrix();
            levelState.reset();
        }
    }

    public void extractProjectionBlockEntities(float partialTick, LevelRenderState levelState) {
        var dispatcher = minecraft.getBlockEntityRenderDispatcher();
        var hidden = ProjectionRenderContext.hiddenTerrainBlock();
        for (var section : ((LevelRendererBufferAccessor) this).glass$getVisibleSections()) {
            for (BlockEntity entity : section.getSectionMesh().getRenderableBlockEntities()) {
                if (!entity.getBlockPos().equals(hidden)) {
                    var entityState = dispatcher.tryExtractRenderState(entity, partialTick, null);
                    if (entityState != null) {
                        levelState.blockEntityRenderStates.add(entityState);
                    }
                }
            }
        }
        for (BlockEntity entity : projectionLevel.getGloballyRenderedBlockEntities()) {
            if (!entity.isRemoved() && !entity.getBlockPos().equals(hidden)) {
                var entityState = dispatcher.tryExtractRenderState(entity, partialTick, null);
                if (entityState != null) {
                    levelState.blockEntityRenderStates.add(entityState);
                }
            }
        }
    }

    @Override
    public void tick(Camera camera) {
        ((ProjectionRendererAccessor) this).glass$ticks((int) projectionLevel.getGameTime());
    }

    @Override
    public void addRecentlyCompiledSection(SectionRenderDispatcher.RenderSection section) {
    }

    @Override
    public void onChunkReadyToRender(ChunkPos pos) {
    }

    @Override
    public boolean isSectionCompiledAndVisible(BlockPos pos) {
        var section = ProjectionSections.find(this, SectionPos.blockToSectionCoord(pos.getX()),
                SectionPos.blockToSectionCoord(pos.getY()), SectionPos.blockToSectionCoord(pos.getZ()));
        return section != null && section.getSectionMesh() != net.minecraft.client.renderer.chunk.CompiledSectionMesh.UNCOMPILED;
    }

    @Override
    public void needsUpdate() {
    }

    @Override
    public SectionRenderDispatcher getSectionRenderDispatcher() {
        return dispatcher;
    }

    @Override
    public void close() {
        setLevel(null);
        super.close();
        features.close();
        fog.close();
        projectionBuffer.close();
        globals.close();
    }
    @Override
   public ChunkSectionsToRender prepareChunkRenders(final Matrix4fc modelViewMatrix) {
      var iterator = ((LevelRendererBufferAccessor) this).glass$getVisibleSections().listIterator(0);
      EnumMap<ChunkSectionLayer, Int2ObjectOpenHashMap<List<RenderPass.Draw<GpuBufferSlice[]>>>> drawGroups = new EnumMap<>(ChunkSectionLayer.class);
      int largestIndexCount = 0;

      for (ChunkSectionLayer layer : ChunkSectionLayer.values()) {
         drawGroups.put(layer, new Int2ObjectOpenHashMap());
      }

      List<DynamicUniforms.ChunkSectionInfo> sectionInfos = new ArrayList<>();
      GpuTextureView blockAtlas = this.minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).getTextureView();
      int textureAtlasWidth = blockAtlas.getWidth(0);
      int textureAtlasHeight = blockAtlas.getHeight(0);
      if (dispatcher != null) {
         dispatcher.lock();

         try {
            try (var ignored = Profiler.get().zone("Upload Global Buffers")) {
               dispatcher.uploadGlobalGeomBuffersToGPU();
            }

            while (iterator.hasNext()) {
               SectionRenderDispatcher.RenderSection section = (SectionRenderDispatcher.RenderSection)iterator.next();
               SectionMesh sectionMesh = section.getSectionMesh();
               BlockPos renderOffset = section.getRenderOrigin();
               long now = Util.getMillis();
               int uboIndex = -1;

               for (ChunkSectionLayer layer : ChunkSectionLayer.values()) {
                  SectionMesh.SectionDraw draw = sectionMesh.getSectionDraw(layer);
                  SectionRenderDispatcher.RenderSectionBufferSlice slice = dispatcher.getRenderSectionSlice(sectionMesh, layer);
                  if (slice != null && draw != null && (!draw.hasCustomIndexBuffer() || slice.indexBuffer() != null)) {
                     if (uboIndex == -1) {
                        uboIndex = sectionInfos.size();
                        sectionInfos.add(
                           new DynamicUniforms.ChunkSectionInfo(
                              new Matrix4f(modelViewMatrix),
                              renderOffset.getX(),
                              renderOffset.getY(),
                              renderOffset.getZ(),
                              section.getVisibility(now),
                              textureAtlasWidth,
                              textureAtlasHeight
                           )
                        );
                     }

                     int combinedHash = 173;
                     VertexFormat vertexFormat = layer.pipeline().getVertexFormat();
                     GpuBuffer vertexBuffer = slice.vertexBuffer();
                     if (layer != ChunkSectionLayer.TRANSLUCENT) {
                        combinedHash = 31 * combinedHash + vertexBuffer.hashCode();
                     }

                     int firstIndex = 0;
                     GpuBuffer indexBuffer;
                     VertexFormat.IndexType indexType;
                     if (!draw.hasCustomIndexBuffer()) {
                        if (draw.indexCount() > largestIndexCount) {
                           largestIndexCount = draw.indexCount();
                        }

                        indexBuffer = null;
                        indexType = null;
                     } else {
                        indexBuffer = slice.indexBuffer();
                        indexType = draw.indexType();
                        if (layer != ChunkSectionLayer.TRANSLUCENT) {
                           combinedHash = 31 * combinedHash + indexBuffer.hashCode();
                           combinedHash = 31 * combinedHash + indexType.hashCode();
                        }

                        firstIndex = (int)(slice.indexBufferOffset() / indexType.bytes);
                     }

                     int finalUboIndex = uboIndex;
                     int baseVertex = (int)(slice.vertexBufferOffset() / vertexFormat.getVertexSize());
                     List<RenderPass.Draw<GpuBufferSlice[]>> draws = (List<RenderPass.Draw<GpuBufferSlice[]>>)drawGroups.get(layer)
                        .computeIfAbsent(combinedHash, var0 -> new ArrayList());
                     draws.add(
                        new RenderPass.Draw<>(
                           0,
                           vertexBuffer,
                           indexBuffer,
                           indexType,
                           firstIndex,
                           draw.indexCount(),
                           baseVertex,
                           (sectionUbos, uploader) -> uploader.upload("ChunkSection", sectionUbos[finalUboIndex])
                        )
                     );
                  }
               }
            }
         } finally {
            dispatcher.unlock();
         }
      }

      GpuBufferSlice[] chunkSectionInfos = RenderSystem.getDynamicUniforms().writeChunkSections(sectionInfos.toArray(new DynamicUniforms.ChunkSectionInfo[0]));
      return new ChunkSectionsToRender(blockAtlas, drawGroups, largestIndexCount, chunkSectionInfos);
   }

}
