package dev.imb11.snow.mixin.client;

import dev.imb11.snow.client.SnowClient;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void snow$tick(BooleanSupplier haveTime, CallbackInfo ci) {
        SnowClient.tick((ClientLevel) (Object) this);
    }

    @Inject(method = "unload", at = @At("HEAD"))
    private void snow$chunkUnloaded(LevelChunk chunk, CallbackInfo ci) {
        SnowClient.chunkUnloaded((ClientLevel) (Object) this, chunk);
    }

    @Inject(method = "setBlocksDirty", at = @At("HEAD"))
    private void snow$blockChanged(BlockPos pos, BlockState oldState, BlockState newState, CallbackInfo ci) {
        SnowClient.blockChanged((ClientLevel) (Object) this, pos, oldState, newState);
    }
}
