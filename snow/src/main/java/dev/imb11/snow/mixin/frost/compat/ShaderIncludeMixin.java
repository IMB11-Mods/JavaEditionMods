package dev.imb11.snow.mixin.frost.compat;

import com.llamalad7.mixinextras.sugar.Local;
import dev.imb11.snow.client.frost.compat.SodiumFrostShader;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(targets = "com.mojang.renderpearl.api.pipeline.ShaderSource$CachedIncludeSource")
public class ShaderIncludeMixin {
    @ModifyVariable(method = "create", at = @At("HEAD"), argsOnly = true)
    private static String snow$patchFrostInclude(String source, @Local(argsOnly = true) Identifier id) {
        return SodiumFrostShader.patchGlobals(id, source);
    }
}
