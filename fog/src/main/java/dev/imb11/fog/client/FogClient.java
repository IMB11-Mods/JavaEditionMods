package dev.imb11.fog.client;

import cc.cassian.mru.Platform;
import dev.imb11.fog.client.command.FogClientCommands;
import dev.imb11.fog.client.resource.FogResourceReloader;
import dev.imb11.fog.client.util.FogKeybinds;
import dev.imb11.fog.config.FogConfig;
//? fabric {
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
//?}
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
        return Platform.INSTANCE.configPath().resolve(configFileName + "." + configExtension);
    }

    public static boolean isModInstalled(String modid) {
        return Platform.INSTANCE.isLoaded(modid);
    }

    public static void initialize() {
        LOGGER.info("Loading {}.", MOD_NAME);
        FogConfig.load();

        FogKeybinds.init();
        //? fabric {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, context) -> {
            FogClientCommands.register(dispatcher);
        });
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(FogResourceReloader.IDENTIFIER, new  FogResourceReloader());
        ClientTickEvents.END_LEVEL_TICK.register((level)->FogManager.getInstance().onEndTick(level));
        //?}
    }
}
