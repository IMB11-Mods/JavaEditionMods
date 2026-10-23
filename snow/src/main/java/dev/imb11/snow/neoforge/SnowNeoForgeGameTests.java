//? neoforge {
/*package dev.imb11.snow.neoforge;

import dev.imb11.snow.gametest.SnowGameTests;
import dev.imb11.snow.gametest.support.SnowTestEnvironments;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.Map;

public class SnowNeoForgeGameTests {
    public static void register(IEventBus bus) {
        bus.addListener(RegisterEvent.class, event -> {
            event.register(Registries.TEST_ENVIRONMENT_DEFINITION_TYPE, helper -> helper.register(SnowTestEnvironments.CONFIG_TYPE, SnowTestEnvironments.ConfigEnvironment.CODEC));
            event.register(Registries.TEST_FUNCTION, helper -> SnowGameTests.all().forEach(test -> helper.register(test.id(), test.function())));
        });

        bus.addListener(RegisterGameTestsEvent.class, event -> {
            Map<ResourceKey<TestEnvironmentDefinition<?>>, Holder<TestEnvironmentDefinition<?>>> holders = new HashMap<>();
            SnowTestEnvironments.ALL.forEach((key, environment) -> holders.put(key, event.registerEnvironment(key.identifier(), environment)));
            SnowGameTests.all().forEach(test -> event.registerTest(test.id(), test.instance(holders.get(test.environment()))));
        });
    }
}
*///?}
