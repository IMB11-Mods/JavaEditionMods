package dev.imb11.snow.mixin.snowlogging.client;

import dev.imb11.snow.client.SnowloggingClient;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Inject(method = "setBlocksDirty", at = @At("HEAD"))
    private void snow$predictPlant(BlockPos pos, BlockState oldState, BlockState newState, CallbackInfo ci) {
        SnowloggingClient.INSTANCE.blockChanged((ClientLevel) (Object) this, pos, oldState, newState);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void snow$expirePredictions(BooleanSupplier haveTime, CallbackInfo ci) {
        SnowloggingClient.INSTANCE.tick((ClientLevel) (Object) this);
    }
}
