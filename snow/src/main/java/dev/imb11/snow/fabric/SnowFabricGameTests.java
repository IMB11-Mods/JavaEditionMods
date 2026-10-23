//? fabric {
package dev.imb11.snow.fabric;

import dev.imb11.snow.gametest.SnowGameTests;
import dev.imb11.snow.gametest.support.SnowTestEnvironments;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.ResourceKey;

import java.util.HashMap;
import java.util.Map;

public class SnowFabricGameTests {
    public static void register() {
        Registry.register(BuiltInRegistries.TEST_ENVIRONMENT_DEFINITION_TYPE, SnowTestEnvironments.CONFIG_TYPE, SnowTestEnvironments.ConfigEnvironment.CODEC);
        SnowGameTests.all().forEach(test -> Registry.register(BuiltInRegistries.TEST_FUNCTION, test.id(), test.function()));

        DynamicRegistrySetupCallback.EVENT.register(view -> view.<GameTestInstance>getOptional(Registries.TEST_INSTANCE).ifPresent(instances -> {
            Registry<TestEnvironmentDefinition<?>> environments = view.<TestEnvironmentDefinition<?>>getOptional(Registries.TEST_ENVIRONMENT).orElseThrow();
            Map<ResourceKey<TestEnvironmentDefinition<?>>, Holder<TestEnvironmentDefinition<?>>> holders = new HashMap<>();
            SnowTestEnvironments.ALL.forEach((key, environment) -> holders.put(key, Registry.registerForHolder(environments, key, environment)));
            SnowGameTests.all().forEach(test -> Registry.register(instances, test.id(), test.instance(holders.get(test.environment()))));
        }));
    }
}
//?}
