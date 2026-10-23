//? fabric {
package dev.imb11.snow.fabric;

import dev.imb11.snow.Snow;
import net.fabricmc.api.ModInitializer;

public class SnowFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Snow.init();
    }
}
//?}
