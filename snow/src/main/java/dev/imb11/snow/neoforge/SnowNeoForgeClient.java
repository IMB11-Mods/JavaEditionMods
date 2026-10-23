//? neoforge {
/*package dev.imb11.snow.neoforge;

import dev.imb11.snow.Snow;
import dev.imb11.snow.config.SnowConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Snow.MOD_ID, dist = Dist.CLIENT)
public class SnowNeoForgeClient {
    public SnowNeoForgeClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (client, parent) -> SnowConfig.getYACL().generateScreen(parent));
    }
}
*///?}
