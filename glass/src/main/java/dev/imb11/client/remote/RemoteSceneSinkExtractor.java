package dev.imb11.client.remote;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

final class RemoteSceneSinkExtractor extends LevelExtractor {
    private RemoteClientScene scene;

    RemoteSceneSinkExtractor(Minecraft minecraft) {
        super(minecraft, new LevelRenderState(), minecraft.levelRenderer);
    }

    void bind(RemoteClientScene scene) {
        this.scene = scene;
    }

    @Override
    public void blockChanged(BlockPos pos, int flags) {
        if (scene != null) {
            scene.dirtyBlock(pos);
        }
    }

    @Override
    public void setBlocksDirty(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        if (scene != null) {
            scene.dirtyBlocks(minX, minY, minZ, maxX, maxY, maxZ);
        }
    }

    @Override
    public void setBlockDirty(BlockPos pos, BlockState oldState, BlockState newState) {
        if (scene != null) {
            scene.dirtyBlock(pos);
        }
    }

    @Override
    public void setSectionDirtyWithNeighbors(int sectionX, int sectionY, int sectionZ) {
        if (scene != null) {
            scene.dirtySectionWithNeighbors(sectionX, sectionY, sectionZ);
        }
    }

    @Override
    public void setSectionRangeDirty(int minSectionX, int minSectionY, int minSectionZ,
                                     int maxSectionX, int maxSectionY, int maxSectionZ) {
        if (scene != null) {
            for (int x = minSectionX; x <= maxSectionX; x++) {
                for (int y = minSectionY; y <= maxSectionY; y++) {
                    for (int z = minSectionZ; z <= maxSectionZ; z++) {
                        scene.dirtySection(x, y, z);
                    }
                }
            }
        }
    }

    @Override
    public void setSectionDirty(int sectionX, int sectionY, int sectionZ) {
        if (scene != null) {
            scene.dirtySection(sectionX, sectionY, sectionZ);
        }
    }
}
