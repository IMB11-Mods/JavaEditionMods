package dev.imb11.snow.gametest.support;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.imb11.snow.Snow;
import dev.imb11.snow.config.SnowConfig;
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
    public static final ResourceKey<TestEnvironmentDefinition<?>> HARVESTING_OFF = key("harvesting_off");

    public static final Map<ResourceKey<TestEnvironmentDefinition<?>>, TestEnvironmentDefinition<?>> ALL = Map.of(
            SNOWFALL, new TestEnvironmentDefinition.AllOf(rain(), rules(), ConfigEnvironment.DEFAULT),
            SNOWFALL_CAPPED, new TestEnvironmentDefinition.AllOf(rain(), rules(5), ConfigEnvironment.DEFAULT),
            MELTING, new TestEnvironmentDefinition.AllOf(rules(), ConfigEnvironment.DEFAULT),
            WARM_MELTING, new TestEnvironmentDefinition.AllOf(rules(), ConfigEnvironment.DEFAULT.withMeltInWarmBiomes(true)),
            HARVESTING_OFF, new TestEnvironmentDefinition.AllOf(rules(), ConfigEnvironment.DEFAULT.withShovelHarvesting(false))
    );

    private static ResourceKey<TestEnvironmentDefinition<?>> key(String name) {
        return ResourceKey.create(Registries.TEST_ENVIRONMENT, Snow.id(name));
    }

    private static TestEnvironmentDefinition<?> rain() {
        return new TestEnvironmentDefinition.Weather(TestEnvironmentDefinition.Weather.Type.RAIN);
    }

    private static TestEnvironmentDefinition<?> rules() {
        return rules(GameRules.MAX_SNOW_ACCUMULATION_HEIGHT.defaultValue());
    }

    private static TestEnvironmentDefinition<?> rules(int maxSnowAccumulationHeight) {
        GameRuleMap rules = GameRuleMap.of();
        rules.set(GameRules.RANDOM_TICK_SPEED, 0);
        rules.set(GameRules.MAX_SNOW_ACCUMULATION_HEIGHT, maxSnowAccumulationHeight);
        return new TestEnvironmentDefinition.SetGameRules(rules);
    }

    public record ConfigEnvironment(boolean accumulation, boolean generation, boolean gradualMelting, boolean meltInWarmBiomes, boolean shovelHarvesting) implements TestEnvironmentDefinition<ConfigEnvironment> {
        public static final MapCodec<ConfigEnvironment> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.BOOL.fieldOf("accumulation").forGetter(ConfigEnvironment::accumulation),
                Codec.BOOL.fieldOf("generation").forGetter(ConfigEnvironment::generation),
                Codec.BOOL.fieldOf("gradual_melting").forGetter(ConfigEnvironment::gradualMelting),
                Codec.BOOL.fieldOf("melt_in_warm_biomes").forGetter(ConfigEnvironment::meltInWarmBiomes),
                Codec.BOOL.fieldOf("shovel_harvesting").forGetter(ConfigEnvironment::shovelHarvesting)
        ).apply(instance, ConfigEnvironment::new));
        public static final ConfigEnvironment DEFAULT = new ConfigEnvironment(true, true, true, false, true);

        public ConfigEnvironment withMeltInWarmBiomes(boolean meltInWarmBiomes) {
            return new ConfigEnvironment(this.accumulation, this.generation, this.gradualMelting, meltInWarmBiomes, this.shovelHarvesting);
        }

        public ConfigEnvironment withShovelHarvesting(boolean shovelHarvesting) {
            return new ConfigEnvironment(this.accumulation, this.generation, this.gradualMelting, this.meltInWarmBiomes, shovelHarvesting);
        }

        @Override
        public ConfigEnvironment setup(ServerLevel level) {
            SnowConfig config = SnowConfig.get();
            ConfigEnvironment previous = new ConfigEnvironment(config.accumulation.enabled, config.generation.enabled, config.melting.gradualMelting, config.melting.meltInWarmBiomes, config.harvesting.shovelHarvesting);
            config.accumulation.enabled = this.accumulation;
            config.generation.enabled = this.generation;
            config.melting.gradualMelting = this.gradualMelting;
            config.melting.meltInWarmBiomes = this.meltInWarmBiomes;
            config.harvesting.shovelHarvesting = this.shovelHarvesting;
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
