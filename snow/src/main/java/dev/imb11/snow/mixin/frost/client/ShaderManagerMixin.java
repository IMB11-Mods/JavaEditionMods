package dev.imb11.snow.mixin.frost.client;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
//? if >=26.3 {
/*import com.mojang.renderpearl.api.pipeline.ShaderType;
*///?} else {
import com.mojang.blaze3d.shaders.ShaderType;
//?}
import dev.imb11.snow.client.frost.FrostShader;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShaderManager.class)
public class ShaderManagerMixin {
    @WrapOperation(method = "loadShader", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;"))
    private static ImmutableMap.Builder<?, ?> snow$patchFrostShader(ImmutableMap.Builder<Object, Object> output, Object key, Object source, Operation<ImmutableMap.Builder<?, ?>> original, @Local(argsOnly = true) Identifier location, @Local(argsOnly = true) ShaderType type) {
        return original.call(output, key, FrostShader.patchShader(type.idConverter().fileToId(location), type == ShaderType.VERTEX, (String) source));
    }
}
