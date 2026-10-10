package dev.imb11.client.renderer.projection;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import org.jetbrains.annotations.Nullable;

public final class ProjectionSections {
    private ProjectionSections() {
    }

    public static @Nullable SectionRenderDispatcher.RenderSection find(LevelRenderer renderer, int x, int y, int z) {
        ViewArea area = renderer.viewArea();
        if (area == null) {
            return null;
        }
        BlockPos origin = SectionPos.of(x, y, z).origin();
        SectionRenderDispatcher.RenderSection section = area.getRenderSectionAt(origin);
        return section != null && section.getRenderOrigin().equals(origin) ? section : null;
    }

    public static void markDirty(LevelRenderer renderer, int x, int y, int z) {
        if (renderer instanceof ProjectionLevelRenderer projection && find(renderer, x, y, z) != null) {
            projection.setSectionDirty(x, y, z);
        }
    }

    public static void markColumnDirty(LevelRenderer renderer, ClientLevel level, ChunkPos chunk) {
        for (int y = level.getMinSectionY(); y <= level.getMaxSectionY(); y++) {
            markDirty(renderer, chunk.x(), y, chunk.z());
        }
    }

    public static void resetColumn(LevelRenderer renderer, ClientLevel level, ChunkPos chunk) {
        for (int y = level.getMinSectionY(); y <= level.getMaxSectionY(); y++) {
            SectionRenderDispatcher.RenderSection section = find(renderer, chunk.x(), y, chunk.z());
            if (section != null) {
                section.reset();
                markDirty(renderer, chunk.x(), y, chunk.z());
            }
        }
    }
}
