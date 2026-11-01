//? neoforge {
/*package dev.imb11.snow.neoforge;

import dev.imb11.snow.Snow;
import dev.imb11.snow.client.SnowClient;
import dev.imb11.snow.client.SnowloggedBlockStateModel;
import dev.imb11.snow.client.frost.BiomeTemperatureResolver;
import dev.imb11.snow.config.SnowConfig;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Snow.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Snow.MOD_ID, value = Dist.CLIENT)
public class SnowNeoForgeClient {
    public SnowNeoForgeClient(ModContainer container) {
        SnowClient.init();
        container.registerExtensionPoint(IConfigScreenFactory.class, (client, parent) -> SnowConfig.getYACL().generateScreen(parent));
    }

    @SubscribeEvent
    public static void registerColorResolvers(RegisterColorHandlersEvent.ColorResolvers event) {
        event.register(BiomeTemperatureResolver.RESOLVER);
    }

    @SubscribeEvent
    public static void wrapSnowModels(ModelEvent.ModifyBakingResult event) {
        event.getBakingResult().blockStateModels().replaceAll((state, model) -> state.is(Blocks.SNOW) ? new SnowloggedBlockStateModel(model) : model);
    }
}
*///?}
