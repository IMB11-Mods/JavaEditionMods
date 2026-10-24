package dev.imb11.snow.profile;

import dev.imb11.snow.Snow;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public class SnowProfiles {
    public static final ResourceKey<Registry<SnowProfile>> KEY = ResourceKey.createRegistryKey(Snow.id("snow_profile"));

    public static Optional<SnowProfile> get(LevelReader level, Holder<Biome> biome) {
        return level.registryAccess().lookup(KEY).flatMap(registry -> resolve(registry.stream(), biome));
    }

    public static Optional<SnowProfile> resolve(Stream<SnowProfile> profiles, Holder<Biome> biome) {
        return profiles
                .filter(profile -> profile.biomes().contains(biome))
                .max(Comparator.comparingInt(SnowProfile::priority));
    }
}
