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

    public static int encode(int color, float weight) {
        int brightest = Math.max(ARGB.red(color), Math.max(ARGB.green(color), ARGB.blue(color)));
        int weightBits = Mth.clamp(Math.round(weight * 15.0F), 1, 15);
        int tintBits = Mth.clamp(Math.round(brightest / 16.0F) - 1, 0, 14);
        return ARGB.color(weightBits << 4 | tintBits, ARGB.red(color), ARGB.green(color), ARGB.blue(color));
    }

    private static int whiten(int channel, float amount) {
        return Mth.clamp(Math.round(channel + (255 - channel) * amount), 0, 255);
    }
}
