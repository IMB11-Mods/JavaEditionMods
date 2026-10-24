//? neoforge {
/*package dev.imb11.snow.neoforge;

import dev.imb11.snow.gametest.SnowGameTests;
import dev.imb11.snow.gametest.support.SnowTestEnvironments;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

public class SnowNeoForgeGameTests {
    public static void register(IEventBus bus) {
        bus.addListener(RegisterEvent.class, event -> {
            event.register(Registries.TEST_ENVIRONMENT_DEFINITION_TYPE, helper -> helper.register(SnowTestEnvironments.CONFIG_TYPE, SnowTestEnvironments.ConfigEnvironment.CODEC));
            event.register(Registries.TEST_FUNCTION, helper -> SnowGameTests.all().forEach(test -> helper.register(test.id(), test.function())));
        });

        bus.addListener(RegisterGameTestsEvent.class, event -> {
            SnowGameTests.registerInstances((key, environment) -> event.registerEnvironment(key.identifier(), environment), (id, instance) -> event.registerTest(id, instance));
        });
    }
}
*///?}
