/*? if neoforge {*/
/*package dev.imb11.fog.loaders.neoforge;

import dev.imb11.fog.client.FogClient;
import dev.imb11.fog.client.FogManager;
import dev.imb11.fog.client.command.FogClientCommands;
import dev.imb11.fog.client.resource.FogResourceReloader;
import dev.imb11.fog.client.util.FogKeybinds;
import dev.imb11.fog.config.FogConfig;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber
@Mod(value = "fog", dist = Dist.CLIENT)
public class FogClientNeoForge {
    public FogClientNeoForge(ModContainer container) {
        FogClient.initialize();
        container.registerExtensionPoint(IConfigScreenFactory.class,
                (client, parent) -> FogConfig.getInstance().getYetAnotherConfigLibInstance().generateScreen(parent));
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(FogKeybinds.toggleKeybind);

    }

    @SubscribeEvent
    public static void tickLevel(ClientTickEvent.Post event) {
        FogKeybinds.tickKeyMapping();
        FogManager.getInstance().onEndTick(Minecraft.getInstance().level);
    }

    @SubscribeEvent
    public static void registerReloadListener(AddClientReloadListenersEvent event) {
        event.addListener(FogResourceReloader.IDENTIFIER, new FogResourceReloader());
    }

    @SubscribeEvent
    public static void registerClientCommands(RegisterClientCommandsEvent event) {
        FogClientCommands.register(event.getDispatcher());
    }
}

*//*?}*/
