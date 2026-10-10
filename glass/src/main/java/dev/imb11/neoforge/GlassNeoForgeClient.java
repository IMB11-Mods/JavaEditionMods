//? neoforge {
/*package dev.imb11.neoforge;

import dev.imb11.Glass;
import dev.imb11.blocks.GBlocks;
import dev.imb11.blocks.entity.ProjectorBlockEntity;
import dev.imb11.blocks.entity.TerminalBlockEntity;
import dev.imb11.client.GlassClient;
import dev.imb11.client.gui.ProjectorBlockGUI;
import dev.imb11.client.gui.ProjectorBlockScreen;
import dev.imb11.client.gui.TerminalBlockGUI;
import dev.imb11.client.gui.TerminalBlockScreen;
import dev.imb11.client.renderer.block.ProjectorBlockEntityRenderer;
import dev.imb11.client.renderer.block.TerminalBlockEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;


@Mod(value = Glass.MOD_ID, dist = Dist.CLIENT)
public final class GlassNeoForgeClient {
    public GlassNeoForgeClient(IEventBus modBus) {
        GlassClient.initialize();
        modBus.addListener(GlassNeoForgeClient::registerRenderers);
        modBus.addListener(GlassNeoForgeClient::registerScreens);
        NeoForge.EVENT_BUS.addListener(GlassNeoForgeClient::tick);
        NeoForge.EVENT_BUS.addListener(GlassNeoForgeClient::loggingIn);
        NeoForge.EVENT_BUS.addListener(GlassNeoForgeClient::loggingOut);
        NeoForge.EVENT_BUS.addListener(GlassNeoForgeClient::chunkUnloaded);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(TerminalBlockEntity.BLOCK_ENTITY_TYPE, TerminalBlockEntityRenderer::new);
    }

    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(TerminalBlockGUI.SCREEN_HANDLER_TYPE, TerminalBlockScreen::new);
        event.register(ProjectorBlockGUI.SCREEN_HANDLER_TYPE, ProjectorBlockScreen::new);
    }

    private static void tick(ClientTickEvent.Pre event) {
        GlassClient.tick(Minecraft.getInstance());
    }

    private static void loggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        GlassClient.clearConnection();
    }

    private static void loggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        GlassClient.clearConnection();
    }

    private static void chunkUnloaded(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof ClientLevel level && event.getChunk() instanceof LevelChunk chunk) {
            GlassClient.onChunkUnload(level, chunk);
        }
    }
}
*///?}
