package dev.imb11.snow.client.frost;

import cc.cassian.mru.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class FrostedLeaves {
    private static final float MIN_TEMPERATURE = -1.0F;
    private static final float MAX_TEMPERATURE = 1.0F;
    public static final Set<Block> LEAVES = Set.of(Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.MANGROVE_LEAVES);
    public static final ColorResolver TEMPERATURE = (biome, x, z) -> encodeTemperature(biome.hasPrecipitation() ? biome.getBaseTemperature() : MAX_TEMPERATURE);
    public static final int FADE_TICKS = 400;
    public static final int BAKED_STEPS = 8;
    private static final float SNOW_TEMPERATURE = 0.15F;
    private static final float FULL_FROST_TEMPERATURE = 0.05F;
    private static final boolean SODIUM = Platform.INSTANCE.isLoaded("sodium");
    private static final Set<Long> SECTIONS = ConcurrentHashMap.newKeySet();
    private static @Nullable ClientLevel level;
    private static volatile int seaLevel = 63;
    private static volatile float previousProgress;
    private static volatile float progress;
    private static volatile float bakedProgress;
    private static volatile boolean shaderMode;

    private FrostedLeaves() {
    }

    public static float weight(BlockAndTintGetter level, BlockState state, BlockPos pos) {
        if (!LEAVES.contains(state.getBlock()) || !exposed(level, pos)) {
            return 0.0F;
        }

        float temperature = decodeTemperature(level.getBlockTint(pos, TEMPERATURE));
        int snowLevel = seaLevel + 17;
        if (pos.getY() > snowLevel) {
            temperature -= (pos.getY() - snowLevel) * 0.05F / 40.0F;
        }

        return Mth.clamp((SNOW_TEMPERATURE - temperature) / (SNOW_TEMPERATURE - FULL_FROST_TEMPERATURE), 0.0F, 1.0F);
    }

    private static boolean exposed(BlockAndTintGetter level, BlockPos pos) {
        BlockPos.MutableBlockPos neighbour = new BlockPos.MutableBlockPos();
        if (level.canSeeSky(neighbour.setWithOffset(pos, Direction.UP))) {
            return true;
        }

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (level.canSeeSky(neighbour.setWithOffset(pos, direction))) {
                return true;
            }
        }

        return false;
    }

    public static int frost(int color, float amount) {
        if (amount <= 0.0F) {
            return color;
        }

        return ARGB.color(ARGB.alpha(color), whiten(ARGB.red(color), amount), whiten(ARGB.green(color), amount), whiten(ARGB.blue(color), amount));
    }

    private static int whiten(int channel, float amount) {
        return Mth.clamp(Math.round(channel + (255 - channel) * amount), 0, 255);
    }

    public static void track(BlockPos pos) {
        SECTIONS.add(SectionPos.asLong(pos));
    }

    public static boolean useShader() {
        return shaderMode;
    }

    public static float bakedProgress() {
        return bakedProgress;
    }

    public static float progress() {
        return progress(Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false));
    }

    public static float progress(float partialTick) {
        return Mth.lerp(partialTick, previousProgress, progress);
    }

    public static void levelChanged(@Nullable ClientLevel level) {
        FrostedLeaves.level = level;
        SECTIONS.clear();
        float target = level == null ? 0.0F : level.getRainLevel(1.0F);
        seaLevel = level == null ? 63 : level.getSeaLevel();
        progress = target;
        previousProgress = target;
        bakedProgress = quantize(target);
        shaderMode = shaderAvailable();
    }

    public static void tick(ClientLevel level) {
        if (FrostedLeaves.level != level) {
            return;
        }

        float target = level.getRainLevel(1.0F);
        previousProgress = progress;
        progress = Mth.approach(progress, target, 1.0F / FADE_TICKS);

        boolean shader = shaderAvailable();
        float baked = quantize(progress);
        if (shader != shaderMode || (!shader && baked != bakedProgress)) {
            shaderMode = shader;
            bakedProgress = baked;
            rebuild(level);
        }
    }

    private static boolean shaderAvailable() {
        return SODIUM && FrostShader.ready() && !IrisFrost.shaderPackInUse();
    }

    private static float quantize(float value) {
        return Math.round(value * BAKED_STEPS) / (float) BAKED_STEPS;
    }

    private static void rebuild(ClientLevel level) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || SECTIONS.isEmpty()) {
            return;
        }

        List<Long> sections = new ArrayList<>(SECTIONS);
        SECTIONS.clear();
        SectionPos centre = SectionPos.of(minecraft.player.blockPosition());
        int distance = minecraft.options.getEffectiveRenderDistance() + 1;
        for (long section : sections) {
            int x = SectionPos.x(section);
            int y = SectionPos.y(section);
            int z = SectionPos.z(section);
            if (Math.abs(x - centre.x()) <= distance && Math.abs(z - centre.z()) <= distance && y >= level.getMinSectionY() && y <= level.getMaxSectionY()) {
                minecraft.levelExtractor.setSectionDirty(x, y, z);
            }
        }
    }

    private static int encodeTemperature(float temperature) {
        int value = Math.round(Mth.clamp((temperature - MIN_TEMPERATURE) / (MAX_TEMPERATURE - MIN_TEMPERATURE), 0.0F, 1.0F) * 255.0F);
        return ARGB.color(255, value, value, value);
    }

    private static float decodeTemperature(int color) {
        return MIN_TEMPERATURE + ARGB.red(color) / 255.0F * (MAX_TEMPERATURE - MIN_TEMPERATURE);
    }
}
