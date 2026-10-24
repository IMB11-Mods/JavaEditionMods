//? fabric {
package dev.imb11.snow.fabric;

import dev.imb11.snow.Snow;
import dev.imb11.snow.profile.SnowProfile;
import dev.imb11.snow.profile.SnowProfiles;
import dev.imb11.snow.gametest.SnowGameTests;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;

public class SnowFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        DynamicRegistries.register(SnowProfiles.KEY, SnowProfile.CODEC);
        Snow.init();
        if (SnowGameTests.enabled()) {
            SnowFabricGameTests.register();
        }
    }
}
//?}
