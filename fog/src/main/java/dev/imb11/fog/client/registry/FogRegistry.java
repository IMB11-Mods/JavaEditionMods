package dev.imb11.fog.client.registry;

import dev.imb11.fog.api.CustomFogDefinition;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FogRegistry {
	private static final Map<TagKey<Structure>, CustomFogDefinition> STRUCTURE_TAG_FOG_REGISTRY = new ConcurrentHashMap<>();
	private static final Map<Identifier, CustomFogDefinition> STRUCTURE_FOG_REGISTRY = new ConcurrentHashMap<>();
	private static final Map<TagKey<Biome>, CustomFogDefinition> BIOME_TAG_FOG_REGISTRY = new ConcurrentHashMap<>();
	private static final Map<Identifier, CustomFogDefinition> BIOME_FOG_REGISTRY = new ConcurrentHashMap<>();
	private static final Map<Identifier, Identifier> TAGGED_BIOME_TO_FOG_CACHE = new ConcurrentHashMap<>();
	private static final Map<Identifier, Set<Identifier>> TAGGED_BIOME_SKIP_LIST = new ConcurrentHashMap<>();

	public static @NotNull Map<TagKey<Structure>, CustomFogDefinition> getStructureTagFogRegistry() {
		return STRUCTURE_TAG_FOG_REGISTRY;
	}

	public static @NotNull Map<Identifier, CustomFogDefinition> getStructureFogRegistry() {
		return STRUCTURE_FOG_REGISTRY;
	}

	public static @NotNull Map<TagKey<Biome>, CustomFogDefinition> getBiomeTagFogRegistry() {
		return BIOME_TAG_FOG_REGISTRY;
	}

	public static @NotNull Map<Identifier, CustomFogDefinition> getBiomeFogRegistry() {
		return BIOME_FOG_REGISTRY;
	}

	public static @NotNull CustomFogDefinition getFogDefinitionOrDefault(@NotNull Identifier biomeId, @NotNull ClientLevel world) {
		CustomFogDefinition biomeFogDefinition = getBiomeFogRegistry().get(biomeId);
		if (biomeFogDefinition != null) {
			return biomeFogDefinition;
		}

		Identifier cachedFogId = TAGGED_BIOME_TO_FOG_CACHE.get(biomeId);
		if (cachedFogId != null) {
			return getBiomeTagFogRegistry().get(TagKey.create(Registries.BIOME, cachedFogId));
		}

		Set<Identifier> skippedTags = TAGGED_BIOME_SKIP_LIST.getOrDefault(biomeId, new HashSet<>());

		Registry<Biome> biomeRegistry = world.registryAccess().lookupOrThrow(Registries.BIOME);

		for (var biomeTagFogEntry : getBiomeTagFogRegistry().entrySet()) {
			TagKey<Biome> tagKey = biomeTagFogEntry.getKey();
			Identifier tagId = tagKey.location();

			if (skippedTags.contains(tagId)) {
				continue;
			}

			var entryListOptional = biomeRegistry.get(tagKey);

			if (entryListOptional.isPresent()) {
				var entryList = entryListOptional.get();
				for (var entry : entryList) {
					//noinspection OptionalGetWithoutIsPresent
					if (entry.unwrapKey().orElseThrow().identifier().equals(biomeId)) {
						TAGGED_BIOME_TO_FOG_CACHE.put(biomeId, tagId);
						TAGGED_BIOME_SKIP_LIST.put(biomeId, skippedTags);
						return biomeTagFogEntry.getValue();
					}
				}
				skippedTags.add(tagId);
			}
		}

		TAGGED_BIOME_SKIP_LIST.put(biomeId, skippedTags);
		return CustomFogDefinition.getDefault(world);
	}

	public static void resetCaches() {
		TAGGED_BIOME_TO_FOG_CACHE.clear();
		TAGGED_BIOME_SKIP_LIST.clear();
	}
}
