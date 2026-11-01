//? fabric {
package dev.imb11.snow.fabric;

import dev.imb11.snow.gametest.SnowGameTests;
import dev.imb11.snow.gametest.support.SnowTestEnvironments;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;

public class SnowFabricGameTests {
    public static void register() {
        Registry.register(BuiltInRegistries.TEST_ENVIRONMENT_DEFINITION_TYPE, SnowTestEnvironments.CONFIG_TYPE, SnowTestEnvironments.ConfigEnvironment.CODEC);
        SnowGameTests.all().forEach(test -> Registry.register(BuiltInRegistries.TEST_FUNCTION, test.id(), test.function()));

        DynamicRegistrySetupCallback.EVENT.register(view -> view.<GameTestInstance>getOptional(Registries.TEST_INSTANCE).ifPresent(instances -> {
            Registry<TestEnvironmentDefinition<?>> environments = view.<TestEnvironmentDefinition<?>>getOptional(Registries.TEST_ENVIRONMENT).orElseThrow();
            SnowGameTests.registerInstances((key, environment) -> Registry.registerForHolder(environments, key, environment), (id, instance) -> Registry.register(instances, id, instance));
        }));
    }
}
//?}
