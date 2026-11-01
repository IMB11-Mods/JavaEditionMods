package dev.imb11.snow.accumulation;

import dev.imb11.snow.generation.SnowGeneration;
import dev.imb11.snow.profile.SnowProfile;
import dev.imb11.snow.profile.SnowProfiles;
import dev.imb11.snow.snowlogging.Snowlogging;
import dev.imb11.snow.util.SnowColumn;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class SnowAccumulation {
    private static final int SETTLE_RADIUS = 1;

    public static void accumulate(ServerLevel level, BlockPos base) {
        float rate = SnowProfiles.get(level, level.getBiome(base)).map(SnowProfile::accumulationRate).orElse(1f);
        int attempts = Mth.floor(rate) + (level.getRandom().nextFloat() < Mth.frac(rate) ? 1 : 0);
        for (int i = 0; i < attempts; i++) {
            Column column = settle(level, base);
            if (column == null) {
                return;
            }

            SnowColumn.place(level, column.pos, column.current + 1, Block.UPDATE_ALL, true);
        }
    }

    public static int target(ServerLevel level, BlockPos pos) {
        Optional<SnowProfile> profile = SnowProfiles.get(level, level.getBiome(pos));
        int gamerule = level.getGameRules().get(GameRules.MAX_SNOW_ACCUMULATION_HEIGHT);
        int cap = Math.min(gamerule, SnowColumn.MAX_LAYERS);
        if (profile.isEmpty()) {
            return cap;
        }

        int shaped = SnowGeneration.layers(level, pos, profile.get());
        return gamerule != GameRules.MAX_SNOW_ACCUMULATION_HEIGHT.defaultValue() ? Math.min(shaped, cap) : shaped;
    }

    private static @Nullable Column settle(ServerLevel level, BlockPos base) {
        Column best = column(level, base);
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -SETTLE_RADIUS; dx <= SETTLE_RADIUS; dx++) {
            for (int dz = -SETTLE_RADIUS; dz <= SETTLE_RADIUS; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }

                BlockPos pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, cursor.set(base.getX() + dx, base.getY(), base.getZ() + dz));
                if (Math.abs(pos.getY() - base.getY()) > 1 || !canSnow(level, pos)) {
                    continue;
                }

                Column candidate = column(level, pos);
                if (candidate != null && (best == null || candidate.fill() < best.fill())) {
                    best = candidate;
                }
            }
        }

        return best;
    }

    private static @Nullable Column column(ServerLevel level, BlockPos pos) {
        int target = target(level, pos);
        int current = SnowColumn.layers(level, pos);
        return current < target ? new Column(pos.immutable(), current, target) : null;
    }

    private static boolean canSnow(ServerLevel level, BlockPos pos) {
        Biome biome = level.getBiome(pos).value();
        return biome.shouldSnow(level, pos) || Snowlogging.shouldSnow(biome, level, pos);
    }

    private record Column(BlockPos pos, int current, int target) {
        private float fill() {
            return (float) this.current / this.target;
        }
    }
}
