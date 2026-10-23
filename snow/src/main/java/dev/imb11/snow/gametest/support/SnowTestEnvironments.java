package dev.imb11.snow.gametest.support;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.imb11.snow.Snow;
import dev.imb11.snow.config.SnowConfig;
import dev.imb11.snow.melting.MeltingConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gamerules.GameRuleMap;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.Map;

public class SnowTestEnvironments {
    public static final Identifier CONFIG_TYPE = Snow.id("config");
    public static final ResourceKey<TestEnvironmentDefinition<?>> SNOWFALL = key("snowfall");
    public static final ResourceKey<TestEnvironmentDefinition<?>> SNOWFALL_CAPPED = key("snowfall_capped");
    public static final ResourceKey<TestEnvironmentDefinition<?>> MELTING = key("melting");
    public static final ResourceKey<TestEnvironmentDefinition<?>> WARM_MELTING = key("warm_melting");

    public static final Map<ResourceKey<TestEnvironmentDefinition<?>>, TestEnvironmentDefinition<?>> ALL = Map.of(
            SNOWFALL, new TestEnvironmentDefinition.AllOf(rain(), rules(GameRules.MAX_SNOW_ACCUMULATION_HEIGHT.defaultValue())),
            SNOWFALL_CAPPED, new TestEnvironmentDefinition.AllOf(rain(), rules(5)),
            MELTING, new TestEnvironmentDefinition.AllOf(rules(GameRules.MAX_SNOW_ACCUMULATION_HEIGHT.defaultValue()), new ConfigEnvironment(true, false)),
            WARM_MELTING, new TestEnvironmentDefinition.AllOf(rules(GameRules.MAX_SNOW_ACCUMULATION_HEIGHT.defaultValue()), new ConfigEnvironment(true, true))
    );

    private static ResourceKey<TestEnvironmentDefinition<?>> key(String name) {
        return ResourceKey.create(Registries.TEST_ENVIRONMENT, Snow.id(name));
    }

    private static TestEnvironmentDefinition<?> rain() {
        return new TestEnvironmentDefinition.Weather(TestEnvironmentDefinition.Weather.Type.RAIN);
    }

    private static TestEnvironmentDefinition<?> rules(int maxSnowAccumulationHeight) {
        GameRuleMap rules = GameRuleMap.of();
        rules.set(GameRules.RANDOM_TICK_SPEED, 0);
        rules.set(GameRules.MAX_SNOW_ACCUMULATION_HEIGHT, maxSnowAccumulationHeight);
        return new TestEnvironmentDefinition.SetGameRules(rules);
    }

    public record ConfigEnvironment(boolean gradualMelting, boolean meltInWarmBiomes) implements TestEnvironmentDefinition<ConfigEnvironment> {
        public static final MapCodec<ConfigEnvironment> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.BOOL.fieldOf("gradual_melting").forGetter(ConfigEnvironment::gradualMelting),
                Codec.BOOL.fieldOf("melt_in_warm_biomes").forGetter(ConfigEnvironment::meltInWarmBiomes)
        ).apply(instance, ConfigEnvironment::new));

        @Override
        public ConfigEnvironment setup(ServerLevel level) {
            MeltingConfig config = SnowConfig.get().melting;
            ConfigEnvironment previous = new ConfigEnvironment(config.gradualMelting, config.meltInWarmBiomes);
            config.gradualMelting = this.gradualMelting;
            config.meltInWarmBiomes = this.meltInWarmBiomes;
            return previous;
        }

        @Override
        public void teardown(ServerLevel level, ConfigEnvironment previous) {
            previous.setup(level);
        }

        @Override
        public MapCodec<ConfigEnvironment> codec() {
            return CODEC;
        }
    }
}
