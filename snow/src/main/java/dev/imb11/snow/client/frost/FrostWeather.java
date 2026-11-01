package dev.imb11.snow.client.frost;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

public final class FrostWeather {
    public static final FrostWeather INSTANCE = new FrostWeather();
    private static final int DEFAULT_SEA_LEVEL = 63;
    private volatile int seaLevel = DEFAULT_SEA_LEVEL;
    private volatile float previousProgress;
    private volatile float progress;

    public void levelChanged(@Nullable ClientLevel level) {
        this.seaLevel = level == null ? DEFAULT_SEA_LEVEL : level.getSeaLevel();
        this.progress = level == null ? 0.0F : level.getRainLevel(1.0F);
        this.previousProgress = this.progress;
    }

    public void tick(ClientLevel level) {
        this.previousProgress = this.progress;
        this.progress = Mth.approach(this.progress, level.getRainLevel(1.0F), 1.0F / 400);
    }

    public int seaLevel() {
        return this.seaLevel;
    }

    public float progress() {
        return this.progress;
    }

    public float interpolatedProgress() {
        return Mth.lerp(Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false), this.previousProgress, this.progress);
    }
}
