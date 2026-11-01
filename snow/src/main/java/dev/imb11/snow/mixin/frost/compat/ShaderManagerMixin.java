package dev.imb11.snow.mixin.frost.compat;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
//? if >=26.3 {
/*import com.mojang.renderpearl.api.pipeline.ShaderType;
*///?} else {
import com.mojang.blaze3d.shaders.ShaderType;
import org.spongepowered.asm.mixin.injection.ModifyArg;
//?}
import dev.imb11.snow.client.frost.compat.SodiumFrostShader;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShaderManager.class)
public class ShaderManagerMixin {
    //? if >=26.3 {
    /*@WrapOperation(method = "loadShader", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;"))
    private static ImmutableMap.Builder<?, ?> snow$patchFrostVertex(ImmutableMap.Builder<Object, Object> output, Object key, Object source, Operation<ImmutableMap.Builder<?, ?>> original, @Local(argsOnly = true) Identifier location, @Local(argsOnly = true) ShaderType type) {
        return original.call(output, key, SodiumFrostShader.patchVertex(type.idConverter().fileToId(location), type == ShaderType.VERTEX, (String) source));
    }
    *///?} else {
    @ModifyArg(method = "loadShader", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/preprocessor/GlslPreprocessor;process(Ljava/lang/String;)Ljava/util/List;"))
    private static String snow$patchFrostVertex(String source, @Local(argsOnly = true) Identifier location, @Local(argsOnly = true) ShaderType type) {
        return SodiumFrostShader.patchVertex(type.idConverter().fileToId(location), type == ShaderType.VERTEX, source);
    }

    @WrapOperation(method = "loadShader", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;"))
    private static ImmutableMap.Builder<?, ?> snow$patchFrostGlobals(ImmutableMap.Builder<Object, Object> output, Object key, Object source, Operation<ImmutableMap.Builder<?, ?>> original, @Local(argsOnly = true) Identifier location, @Local(argsOnly = true) ShaderType type) {
        return original.call(output, key, SodiumFrostShader.patchGlobals(type.idConverter().fileToId(location), (String) source));
    }
    //?}
}
