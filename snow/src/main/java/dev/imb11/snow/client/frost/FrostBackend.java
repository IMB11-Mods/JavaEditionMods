package dev.imb11.snow.client.frost;

import cc.cassian.mru.Platform;
import dev.imb11.snow.client.frost.compat.IrisCompat;
import dev.imb11.snow.client.frost.compat.SodiumFrostShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class FrostBackend {
    public static final FrostBackend INSTANCE = new FrostBackend();
    public static final int BAKED_STEPS = 8;
    private static final boolean SODIUM = Platform.INSTANCE.isLoaded("sodium");
    private final Set<Long> sections = ConcurrentHashMap.newKeySet();
    private volatile float bakedProgress;
    private volatile boolean shaderMode;

    public void levelChanged() {
        this.sections.clear();
        this.bakedProgress = quantize(FrostWeather.INSTANCE.progress());
        this.shaderMode = shaderAvailable();
    }

    public void tick(ClientLevel level) {
        boolean shader = shaderAvailable();
        float baked = quantize(FrostWeather.INSTANCE.progress());
        if (shader != this.shaderMode || (!shader && baked != this.bakedProgress)) {
            this.shaderMode = shader;
            this.bakedProgress = baked;
            this.rebuild(level);
        }
    }

    public boolean useShader() {
        return this.shaderMode;
    }

    public float bakedProgress() {
        return this.bakedProgress;
    }

    public void track(BlockPos pos) {
        this.sections.add(SectionPos.asLong(pos));
    }

    public int bake(int color, float weight, BlockPos pos) {
        this.track(pos);
        return FrostColor.apply(color, weight * this.bakedProgress);
    }

    private static boolean shaderAvailable() {
        return SODIUM && SodiumFrostShader.ready() && !IrisCompat.shaderPackInUse();
    }

    private static float quantize(float value) {
        return Math.round(value * BAKED_STEPS) / (float) BAKED_STEPS;
    }

    private void rebuild(ClientLevel level) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || this.sections.isEmpty()) {
            return;
        }

        List<Long> sections = new ArrayList<>(this.sections);
        this.sections.clear();
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
}
