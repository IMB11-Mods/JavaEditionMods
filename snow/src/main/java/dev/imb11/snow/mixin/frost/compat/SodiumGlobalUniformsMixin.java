package dev.imb11.snow.mixin.frost.compat;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.buffers.Std140Builder;
import dev.imb11.snow.client.frost.FrostedLeaves;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.UniformBufferManager$GlobalUniforms", remap = false)
abstract class SodiumGlobalUniformsMixin {
    @ModifyExpressionValue(method = "write", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/buffers/Std140Builder;putInt(I)Lcom/mojang/blaze3d/buffers/Std140Builder;"))
    private Std140Builder snow$appendFrost(Std140Builder builder) {
        return builder.putFloat(FrostedLeaves.useShader() ? FrostedLeaves.progress() : 0.0F);
    }
}
