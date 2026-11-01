//? fabric {
package dev.imb11.snow.fabric;

import dev.imb11.snow.client.SnowloggedBlockStateModel;
import dev.imb11.snow.client.SnowloggingClient;
import dev.imb11.snow.client.frost.FrostedLeaves;
import dev.imb11.snow.snowlogging.SnowloggingAttachment;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.ColorResolverRegistry;
import net.minecraft.world.level.block.Blocks;

public class SnowFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SnowloggingClient.init();
        ColorResolverRegistry.register(FrostedLeaves.TEMPERATURE);
        ModelLoadingPlugin.register(context -> context.modifyBlockModelAfterBake().register((model, modelContext) -> modelContext.state().is(Blocks.SNOW) ? new SnowloggedBlockStateModel(model) : model));

        ClientChunkEvents.CHUNK_LOAD.register((level, chunk) -> {
            SnowloggingClient.INSTANCE.plantsChanged(level, null, chunk.getAttached(SnowloggingAttachment.TYPE));
            chunk.onAttachedSet(SnowloggingAttachment.TYPE).register((previous, current) -> SnowloggingClient.INSTANCE.plantsChanged(level, previous, current));
        });
        ClientChunkEvents.CHUNK_UNLOAD.register((level, chunk) -> SnowloggingClient.INSTANCE.plantsChanged(level, chunk.getAttached(SnowloggingAttachment.TYPE), null));
    }
}
//?}
