package dev.imb11.snow.snowlogging;

import dev.imb11.snow.Snow;
import dev.imb11.snow.config.SnowConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class Snowlogging {
    public static final TagKey<Block> SNOWLOGGABLE = TagKey.create(Registries.BLOCK, Snow.id("snowloggable"));
    private static SnowloggingListener listener = SnowloggingListener.NONE;

    public static SnowloggingListener listener() {
        return listener;
    }

    public static void setListener(SnowloggingListener listener) {
        Snowlogging.listener = listener;
    }

    public static boolean canSnowlog(BlockState state) {
        return SnowConfig.get().snowlogging && state.is(SNOWLOGGABLE);
    }

    public static boolean canPlaceInto(BlockState state, BlockPlaceContext context) {
        if (!context.getItemInHand().is(Items.SNOW) || !canSnowlog(state)) {
            return false;
        }

        return !context.getLevel().isClientSide() || SnowConfig.SYNC.serverPresent();
    }

    public static boolean canPlantInto(BlockState state, BlockPlaceContext context) {
        if (!state.is(Blocks.SNOW) || !(context.getItemInHand().getItem() instanceof BlockItem item) || !canSnowlog(item.getBlock().defaultBlockState())) {
            return false;
        }

        Level level = context.getLevel();
        if (level.isClientSide() && !SnowConfig.SYNC.serverPresent()) {
            return false;
        }

        return plantAt(level, context.getClickedPos()) == null;
    }

    public static boolean placeInto(BlockPlaceContext context, BlockState plant) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!canPlantInto(level.getBlockState(pos), context) || !canSnowlog(plant)) {
            return false;
        }

        if (level.isClientSide()) {
            listener.plantPredicted(level, pos, plant);
        } else {
            track(level.getChunk(pos), pos, plant);
        }

        return true;
    }

    public static boolean shouldSnow(Biome biome, LevelReader level, BlockPos pos) {
        if (!canSnowlog(level.getBlockState(pos)) || biome.getPrecipitationAt(pos, level.getSeaLevel()) != Biome.Precipitation.SNOW) {
            return false;
        }

        return level.isInsideBuildHeight(pos.getY())
                && level.getBrightness(LightLayer.BLOCK, pos) < 10
                && Blocks.SNOW.defaultBlockState().canSurvive(level, pos);
    }

    public static @Nullable BlockState plantAt(LevelAccessor level, BlockPos pos) {
        return SnowloggingAttachment.get(level.getChunk(pos)).get(pos);
    }

    public static void snowlog(LevelAccessor level, BlockPos pos) {
        ChunkAccess chunk = level.getChunk(pos);
        BlockState state = chunk.getBlockState(pos);
        if (canSnowlog(state)) {
            track(chunk, pos, state);
        }
    }

    public static BlockState setBlock(Level level, BlockPos pos, BlockState state, int flags) {
        if (!level.isInValidBounds(pos)) {
            return state;
        }

        LevelChunk chunk = level.getChunkAt(pos);
        BlockState plant = SnowloggingAttachment.get(chunk).get(pos);
        if (plant == null) {
            if (!level.isClientSide() && state.is(Blocks.SNOW)) {
                snowlog(level, pos);
            }

            return state;
        }

        return state.is(Blocks.SNOW) ? state : uncover(level, chunk, pos, plant, state, flags);
    }

    private static BlockState uncover(Level level, LevelChunk chunk, BlockPos pos, BlockState plant, BlockState state, int flags) {
        boolean snowRemoved = chunk.getBlockState(pos).is(Blocks.SNOW) && (flags & Block.UPDATE_NEIGHBORS) != 0;
        boolean plantRestored = snowRemoved && (state.isAir() || state == plant);
        if (!level.isClientSide()) {
            untrack(chunk, pos);
            if (snowRemoved && !plantRestored) {
                Block.dropResources(plant, level, pos);
            }
        }

        return plantRestored ? plant : state;
    }

    public static void destroy(Level level, BlockPos pos) {
        LevelChunk chunk = level.getChunkAt(pos);
        BlockState plant = SnowloggingAttachment.get(chunk).get(pos);
        if (plant != null) {
            untrack(chunk, pos);
            Block.dropResources(plant, level, pos);
        }
    }

    private static void track(ChunkAccess chunk, BlockPos pos, BlockState plant) {
        SnowloggedPlants plants = SnowloggingAttachment.get(chunk);
        SnowloggedPlants updated = plants.with(pos, plant);
        if (updated != plants) {
            SnowloggingAttachment.set(chunk, updated);
            chunk.markUnsaved();
        }
    }

    private static void untrack(ChunkAccess chunk, BlockPos pos) {
        SnowloggedPlants plants = SnowloggingAttachment.get(chunk);
        SnowloggedPlants updated = plants.without(pos);
        if (updated != plants) {
            SnowloggingAttachment.set(chunk, updated);
            chunk.markUnsaved();
        }
    }
}
