package dev.imb11.snow.snowlogging;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public record SnowloggedPlants(Map<BlockPos, BlockState> plants) {
    public static final SnowloggedPlants EMPTY = new SnowloggedPlants(Map.of());
    public static final MapCodec<SnowloggedPlants> MAP_CODEC = Entry.CODEC.listOf().fieldOf("plants").xmap(SnowloggedPlants::fromEntries, SnowloggedPlants::entries);
    public static final Codec<SnowloggedPlants> CODEC = MAP_CODEC.codec();
    public static final StreamCodec<RegistryFriendlyByteBuf, SnowloggedPlants> STREAM_CODEC = ByteBufCodecs.<RegistryFriendlyByteBuf, BlockPos, BlockState, Map<BlockPos, BlockState>>map(HashMap::new, BlockPos.STREAM_CODEC, ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY))
            .map(SnowloggedPlants::new, SnowloggedPlants::plants);

    public SnowloggedPlants {
        plants = Map.copyOf(plants);
    }

    public @Nullable BlockState get(BlockPos pos) {
        return this.plants.get(pos);
    }

    public boolean isEmpty() {
        return this.plants.isEmpty();
    }

    public SnowloggedPlants with(BlockPos pos, BlockState plant) {
        if (Objects.equals(this.plants.get(pos), plant)) {
            return this;
        }

        Map<BlockPos, BlockState> plants = new HashMap<>(this.plants);
        plants.put(pos.immutable(), plant);
        return new SnowloggedPlants(plants);
    }

    public SnowloggedPlants without(BlockPos pos) {
        if (!this.plants.containsKey(pos)) {
            return this;
        }

        Map<BlockPos, BlockState> plants = new HashMap<>(this.plants);
        plants.remove(pos);
        return new SnowloggedPlants(plants);
    }

    private static SnowloggedPlants fromEntries(List<Entry> entries) {
        return new SnowloggedPlants(entries.stream().collect(Collectors.toMap(Entry::pos, Entry::plant, (first, second) -> second)));
    }

    private List<Entry> entries() {
        return this.plants.entrySet().stream().map(entry -> new Entry(entry.getKey(), entry.getValue())).toList();
    }

    private record Entry(BlockPos pos, BlockState plant) {
        private static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPos.CODEC.fieldOf("pos").forGetter(Entry::pos),
                BlockState.CODEC.fieldOf("plant").forGetter(Entry::plant)
        ).apply(instance, Entry::new));
    }
}
