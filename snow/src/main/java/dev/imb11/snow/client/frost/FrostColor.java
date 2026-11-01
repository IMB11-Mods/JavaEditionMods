package dev.imb11.snow.client.frost;

import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public final class FrostColor {
    public static int apply(int color, float amount) {
        if (amount <= 0.0F) {
            return color;
        }

        return ARGB.color(ARGB.alpha(color), whiten(ARGB.red(color), amount), whiten(ARGB.green(color), amount), whiten(ARGB.blue(color), amount));
    }

    private static int whiten(int channel, float amount) {
        return Mth.clamp(Math.round(channel + (255 - channel) * amount), 0, 255);
    }
}
