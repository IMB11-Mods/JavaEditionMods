package dev.imb11.fog.client;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.imb11.fog.client.command.FogClientCommands;
import dev.imb11.fog.client.registry.FogRegistry;
import dev.imb11.fog.client.resource.FogResourceReloader;
import dev.imb11.fog.client.util.FogKeybinds;
import dev.imb11.fog.config.FogConfig;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class FogClient {
    public static final @NotNull String MOD_ID = "fog";
    public static final @NotNull String MOD_NAME = "Fog";
    public static final @NotNull Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Path getConfigPath(String configFileName, String configExtension) {
        return getConfigFolder().resolve(configFileName + "." + configExtension);
    }

    public static boolean isModInstalled(String modid) {
        /*? if fabric {*/
        return net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded(modid);
        /*?} elif neoforge {*/
        /*return net.neoforged.fml.loading.FMLLoader.getCurrent().getLoadingModList().getModFileById(modid) != null;
        *//*?}*/
    }

    public static Path getConfigFolder() {
        /*? if fabric {*/
        return net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
        /*?} elif neoforge {*/
        /*return net.neoforged.fml.loading.FMLLoader.getCurrent().getGameDir().resolve("config").resolve(MOD_ID);
        *//*?}*/
    }

    public static void initialize() {
        LOGGER.info("Loading {}.", MOD_NAME);
        FogConfig.load();
        FogClientCommands.register();
        FogKeybinds.init();
        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, new FogResourceReloader(), FogResourceReloader.IDENTIFIER);
        ClientTickEvent.CLIENT_LEVEL_POST.register(world -> FogManager.getInstance().onEndTick(world));
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(player -> {
            FogManager.INSTANCE = new FogManager();
            FogRegistry.resetCaches();
        });
    }
}
