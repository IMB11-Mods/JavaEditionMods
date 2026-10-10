package dev.imb11.mixins;

import dev.imb11.client.remote.ProjectionChunkStorage;
import dev.imb11.client.renderer.projection.ProjectionRenderManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
abstract class ClientPacketListenerMixin {
    @Inject(method = "enableChunkLight", at = @At("TAIL"))
    private void glass$mirrorChunkLoaded(LevelChunk chunk, int x, int z, CallbackInfo ci) {
        ProjectionRenderManager.onMainChunkLoaded(chunk.getPos());
    }

    @Redirect(method = "queueLightRemoval", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;queueLightUpdate(Ljava/lang/Runnable;)V"))
    private void glass$retainProjectionLight(ClientLevel level, Runnable removal, ClientboundForgetLevelChunkPacket packet) {
        level.queueLightUpdate(() -> {
            if (!ProjectionChunkStorage.of(level).retained(packet.pos())) {
                removal.run();
            }
        });
    }
}
