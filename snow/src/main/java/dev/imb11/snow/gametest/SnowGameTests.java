package dev.imb11.snow.gametest;

import cc.cassian.mru.Platform;
import dev.imb11.snow.Snow;
import dev.imb11.snow.gametest.support.SnowTestEnvironments;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Rotation;
//? if >=26.3 {
/*import net.minecraft.world.level.Level;
*///?}

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class SnowGameTests {
    private static final int PADDING = 4;
    private static final List<TestDefinition> TESTS = Stream.of(AccumulationTests.all(), GenerationTests.all(), MeltingTests.all(), HarvestingTests.all())
            .flatMap(List::stream)
            .toList();

    public static boolean enabled() {
        return Platform.INSTANCE.isDeveloperEnvironment();
    }

    public static List<TestDefinition> all() {
        return TESTS;
    }

    public static void registerInstances(BiFunction<ResourceKey<TestEnvironmentDefinition<?>>, TestEnvironmentDefinition<?>, Holder<TestEnvironmentDefinition<?>>> environments, BiConsumer<Identifier, GameTestInstance> instances) {
        Map<ResourceKey<TestEnvironmentDefinition<?>>, Holder<TestEnvironmentDefinition<?>>> holders = new HashMap<>();
        SnowTestEnvironments.ALL.forEach((key, environment) -> holders.put(key, environments.apply(key, environment)));
        TESTS.forEach(test -> instances.accept(test.id(), test.instance(holders.get(test.environment()))));
    }

    public record TestDefinition(Identifier id, ResourceKey<TestEnvironmentDefinition<?>> environment, Identifier structure, int maxTicks, Consumer<GameTestHelper> function) {
        public TestDefinition(String name, ResourceKey<TestEnvironmentDefinition<?>> environment, Identifier structure, int maxTicks, Consumer<GameTestHelper> function) {
            this(Snow.id(name), environment, structure, maxTicks, function);
        }

        public GameTestInstance instance(Holder<TestEnvironmentDefinition<?>> environment) {
            //? if >=26.3 {
            /*TestData<Holder<TestEnvironmentDefinition<?>>> data = new TestData<>(environment, Level.OVERWORLD, this.structure, this.maxTicks, 0, true, Rotation.NONE, false, 1, 1, true, PADDING);
            *///?} else {
            TestData<Holder<TestEnvironmentDefinition<?>>> data = new TestData<>(environment, this.structure, this.maxTicks, 0, true, Rotation.NONE, false, 1, 1, true, PADDING);
            //?}
            return new FunctionGameTestInstance(ResourceKey.create(Registries.TEST_FUNCTION, this.id), data);
        }
    }
}
