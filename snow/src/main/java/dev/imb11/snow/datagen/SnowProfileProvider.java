//? fabric {
package dev.imb11.snow.datagen;

import dev.imb11.snow.Snow;
import dev.imb11.snow.accumulation.SnowProfile;
import dev.imb11.snow.accumulation.SnowProfiles;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class SnowProfileProvider extends FabricDynamicRegistryProvider {
    public SnowProfileProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    public static void bootstrap(BootstrapContext<SnowProfile> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        register(context, biomes, "ice_spikes", 4, 12, 3f, Biomes.ICE_SPIKES);
        register(context, biomes, "snowy_plains", 2, 8, 2f, Biomes.SNOWY_PLAINS);
        register(context, biomes, "snowy_taiga", 1, 4, 1.5f, Biomes.SNOWY_TAIGA, Biomes.SNOWY_SLOPES);
        register(context, biomes, "frozen", 1, 2, 1f, Biomes.SNOWY_BEACH, Biomes.GROVE, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.FROZEN_RIVER, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN);
        register(context, biomes, "cold", 0, 2, 1f, Biomes.WINDSWEPT_HILLS, Biomes.STONY_SHORE, Biomes.TAIGA);
    }

    @SafeVarargs
    private static void register(BootstrapContext<SnowProfile> context, HolderGetter<Biome> biomes, String name, int minLayers, int maxLayers, float accumulationRate, ResourceKey<Biome>... keys) {
        HolderSet<Biome> holders = HolderSet.direct(Arrays.stream(keys).map(biomes::getOrThrow).toList());
        context.register(ResourceKey.create(SnowProfiles.KEY, Snow.id(name)), new SnowProfile(holders, minLayers, maxLayers, accumulationRate, 0));
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.addAll(registries.lookupOrThrow(SnowProfiles.KEY));
    }

    @Override
    public String getName() {
        return "Snow Profiles";
    }
}
//?}
