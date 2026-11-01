package dev.imb11.snow.mixin.frost.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.buffers.Std140Builder;
import dev.imb11.snow.client.frost.FrostBackend;
import dev.imb11.snow.client.frost.FrostWeather;
import net.minecraft.client.renderer.GlobalSettingsUniform;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GlobalSettingsUniform.class)
public class GlobalSettingsUniformMixin {
    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/buffers/Std140SizeCalculator;get()I"))
    private static int snow$reserveFrost(int size) {
        return Mth.roundToward(size + Float.BYTES, 16);
    }

    @ModifyExpressionValue(method = "update", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/buffers/Std140Builder;putInt(I)Lcom/mojang/blaze3d/buffers/Std140Builder;", ordinal = 1))
    private Std140Builder snow$appendFrost(Std140Builder builder) {
        return builder.putFloat(FrostBackend.INSTANCE.useShader() ? FrostWeather.INSTANCE.interpolatedProgress() : 0.0F);
    }
}
