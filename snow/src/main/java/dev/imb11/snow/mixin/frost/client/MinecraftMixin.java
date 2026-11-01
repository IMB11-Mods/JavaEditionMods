package dev.imb11.snow.mixin.frost.client;

import dev.imb11.snow.client.frost.FrostedLeaves;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "updateLevelInEngines(Lnet/minecraft/client/multiplayer/ClientLevel;Z)V", at = @At("HEAD"))
    private void snow$resetFrost(@Nullable ClientLevel level, boolean stopSound, CallbackInfo ci) {
        FrostedLeaves.levelChanged(level);
    }
}
