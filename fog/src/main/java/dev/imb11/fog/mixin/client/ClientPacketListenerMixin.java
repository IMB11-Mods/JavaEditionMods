package dev.imb11.fog.mixin.client;

import dev.imb11.fog.client.FogManager;
import dev.imb11.fog.client.registry.FogRegistry;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
	@Inject(method = "handleLogin", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;setServerRenderDistance(I)V", shift = At.Shift.AFTER))
	private void handleLogin(ClientboundLoginPacket packet, CallbackInfo ci) {
		FogManager.INSTANCE = new FogManager();
		FogRegistry.resetCaches();
	}
}
