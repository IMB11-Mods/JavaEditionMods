package dev.imb11.snow.snowlogging;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SnowloggingStorage {
    public static final String TAG = "snow:snowlogged_layers";
    private static final Codec<List<Layers>> CODEC = Layers.CODEC.listOf();

    public static List<Layers> store(ChunkAccess chunk, SerializableChunkData data) {
        List<Layers> layers = new ArrayList<>();
        for (Map.Entry<BlockPos, BlockState> entry : SnowloggingAttachment.get(chunk).plants().entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState snow = chunk.getBlockState(pos);
            LevelChunkSection section = section(data, pos);
            if (!snow.is(Blocks.SNOW) || section == null) {
                continue;
            }

            setBlockState(section, pos, entry.getValue());
            layers.add(new Layers(pos, snow.getValue(SnowLayerBlock.LAYERS)));
        }

        return layers;
    }

    public static void restore(SerializableChunkData data, List<Layers> layers) {
        for (Layers entry : layers) {
            LevelChunkSection section = section(data, entry.pos());
            if (section != null && getBlockState(section, entry.pos()).is(Snowlogging.SNOWLOGGABLE)) {
                setBlockState(section, entry.pos(), Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, entry.layers()));
            }
        }
    }

    public static void write(CompoundTag tag, List<Layers> layers) {
        if (!layers.isEmpty()) {
            tag.store(TAG, CODEC, layers);
        }
    }

    public static List<Layers> read(CompoundTag tag) {
        return tag.read(TAG, CODEC).orElse(List.of());
    }

    private static @Nullable LevelChunkSection section(SerializableChunkData data, BlockPos pos) {
        int sectionY = SectionPos.blockToSectionCoord(pos.getY());
        for (SerializableChunkData.SectionData section : data.sectionData()) {
            if (section.y() == sectionY) {
                return section.chunkSection();
            }
        }

        return null;
    }

    private static BlockState getBlockState(LevelChunkSection section, BlockPos pos) {
        return section.getBlockState(SectionPos.sectionRelative(pos.getX()), SectionPos.sectionRelative(pos.getY()), SectionPos.sectionRelative(pos.getZ()));
    }

    private static void setBlockState(LevelChunkSection section, BlockPos pos, BlockState state) {
        section.setBlockState(SectionPos.sectionRelative(pos.getX()), SectionPos.sectionRelative(pos.getY()), SectionPos.sectionRelative(pos.getZ()), state, false);
    }

    public record Layers(BlockPos pos, int layers) {
        private static final Codec<Layers> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPos.CODEC.fieldOf("pos").forGetter(Layers::pos),
                Codec.intRange(1, SnowLayerBlock.MAX_HEIGHT).fieldOf("layers").forGetter(Layers::layers)
        ).apply(instance, Layers::new));
    }
}
