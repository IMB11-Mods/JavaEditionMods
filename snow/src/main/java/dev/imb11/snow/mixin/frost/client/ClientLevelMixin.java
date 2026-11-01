package dev.imb11.snow.mixin.frost.client;

import dev.imb11.snow.client.frost.FrostedLeaves;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void snow$tickFrost(BooleanSupplier haveTime, CallbackInfo ci) {
        FrostedLeaves.tick((ClientLevel) (Object) this);
    }
}
