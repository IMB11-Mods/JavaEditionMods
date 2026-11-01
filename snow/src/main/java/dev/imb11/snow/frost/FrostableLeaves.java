package dev.imb11.snow.frost;

import dev.imb11.snow.Snow;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Set;

public final class FrostableLeaves {
    public static final TagKey<Block> TAG = TagKey.create(Registries.BLOCK, Snow.id("frostable_leaves"));
    public static final List<Block> DEFAULTS = List.of(
            Blocks.OAK_LEAVES,
            Blocks.SPRUCE_LEAVES,
            Blocks.BIRCH_LEAVES,
            Blocks.JUNGLE_LEAVES,
            Blocks.ACACIA_LEAVES,
            Blocks.MANGROVE_LEAVES
    );
    private static final Set<Block> DEFAULT_SET = Set.copyOf(DEFAULTS);

    public static boolean contains(BlockState state) {
        return BuiltInRegistries.BLOCK.get(TAG).isPresent() ? state.is(TAG) : DEFAULT_SET.contains(state.getBlock());
    }
}
