//? neoforge {
/*package dev.imb11.snow.neoforge;

import dev.imb11.snow.Snow;
import dev.imb11.snow.client.SnowloggedBlockStateModel;
import dev.imb11.snow.client.SnowloggingClient;
import dev.imb11.snow.config.SnowConfig;
import dev.imb11.snow.snowlogging.SnowloggingAttachment;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.level.ChunkEvent;

@Mod(value = Snow.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Snow.MOD_ID, value = Dist.CLIENT)
public class SnowNeoForgeClient {
    public SnowNeoForgeClient(ModContainer container) {
        SnowloggingClient.init();
        container.registerExtensionPoint(IConfigScreenFactory.class, (client, parent) -> SnowConfig.getYACL().generateScreen(parent));
    }

    @SubscribeEvent
    public static void wrapSnowModels(ModelEvent.ModifyBakingResult event) {
        event.getBakingResult().blockStateModels().replaceAll((state, model) -> state.is(Blocks.SNOW) ? new SnowloggedBlockStateModel(model) : model);
    }

    @SubscribeEvent
    public static void forgetUnloadedChunk(ChunkEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            SnowloggingClient.INSTANCE.plantsChanged(event.getChunk().getLevel(), SnowloggingAttachment.get(event.getChunk()), null);
        }
    }
}
*///?}
