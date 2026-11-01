package dev.imb11.snow.client.frost;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicIntegerArray;

public final class FrostGround {
    public static final FrostGround INSTANCE = new FrostGround();
    private static final Heightmap.Types SURFACE = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;
    private final Map<Long, AtomicIntegerArray> chunks = new ConcurrentHashMap<>();

    public void levelChanged() {
        this.chunks.clear();
    }

    public void chunkLoaded(ClientLevel level, ChunkPos pos) {
        LevelChunk chunk = level.getChunk(pos.x(), pos.z());
        AtomicIntegerArray heights = new AtomicIntegerArray(SectionPos.SECTION_SIZE * SectionPos.SECTION_SIZE);
        for (int z = 0; z < SectionPos.SECTION_SIZE; z++) {
            for (int x = 0; x < SectionPos.SECTION_SIZE; x++) {
                heights.set(index(x, z), ground(chunk, x, z));
            }
        }

        this.chunks.put(pos.pack(), heights);
    }

    public void chunkUnloaded(LevelChunk chunk) {
        this.chunks.remove(chunk.getPos().pack());
    }

    public void blockChanged(ClientLevel level, BlockPos pos) {
        AtomicIntegerArray heights = this.chunks.get(ChunkPos.pack(pos));
        if (heights == null) {
            return;
        }

        int x = SectionPos.sectionRelative(pos.getX());
        int z = SectionPos.sectionRelative(pos.getZ());
        int height = ground(level.getChunkAt(pos), x, z);
        int previous = heights.getAndSet(index(x, z), height);
        if (previous != height) {
            level.setSectionRangeDirty(
                    SectionPos.blockToSectionCoord(pos.getX() - 1),
                    SectionPos.blockToSectionCoord(Math.min(previous, height) - 1),
                    SectionPos.blockToSectionCoord(pos.getZ() - 1),
                    SectionPos.blockToSectionCoord(pos.getX() + 1),
                    SectionPos.blockToSectionCoord(Math.max(previous, height)),
                    SectionPos.blockToSectionCoord(pos.getZ() + 1)
            );
        }
    }

    public boolean aboveGround(BlockPos pos) {
        AtomicIntegerArray heights = this.chunks.get(ChunkPos.pack(pos));
        return heights == null || pos.getY() >= heights.get(index(SectionPos.sectionRelative(pos.getX()), SectionPos.sectionRelative(pos.getZ())));
    }

    private static int ground(LevelChunk chunk, int x, int z) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, chunk.getHeight(SURFACE, x, z), z);
        while (pos.getY() >= chunk.getMinY() && !isGround(chunk.getBlockState(pos))) {
            pos.move(Direction.DOWN);
        }

        return pos.getY() + 1;
    }

    private static boolean isGround(BlockState state) {
        return SURFACE.isOpaque().test(state) && !state.is(Blocks.SNOW);
    }

    private static int index(int x, int z) {
        return x + z * SectionPos.SECTION_SIZE;
    }
}
