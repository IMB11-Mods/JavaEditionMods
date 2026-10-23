//? neoforge {
/*package dev.imb11.snow.neoforge;

import dev.imb11.snow.Snow;
import dev.imb11.snow.accumulation.SnowProfile;
import dev.imb11.snow.accumulation.SnowProfiles;
import dev.imb11.snow.gametest.SnowGameTests;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
//? if >=26.3 {
/^import net.neoforged.neoforge.registries.NewDatapackRegistryEvent;
^///?} else {
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
//?}

@Mod(Snow.MOD_ID)
public class SnowNeoForge {
    public SnowNeoForge(IEventBus bus) {
        //? if >=26.3 {
        /^bus.addListener(NewDatapackRegistryEvent.class, event -> event.worldRegistry(SnowProfiles.KEY, SnowProfile.CODEC));
        ^///?} else {
        bus.addListener(DataPackRegistryEvent.NewRegistry.class, event -> event.dataPackRegistry(SnowProfiles.KEY, SnowProfile.CODEC));
        //?}
        Snow.init();
        if (SnowGameTests.enabled()) {
            SnowNeoForgeGameTests.register(bus);
        }
    }
}
*///?}
