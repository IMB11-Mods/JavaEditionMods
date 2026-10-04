/*? if neoforge {*/
/*package dev.imb11.fog.loaders.neoforge;

import dev.imb11.fog.client.FogClient;
import dev.imb11.fog.config.FogConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = "fog", dist = Dist.CLIENT)
public class FogClientNeoForge {
    public FogClientNeoForge(ModContainer container) {
        FogClient.initialize();
        container.registerExtensionPoint(IConfigScreenFactory.class,
                (client, parent) -> FogConfig.getInstance().getYetAnotherConfigLibInstance().generateScreen(parent));
    }
}

*//*?}*/
