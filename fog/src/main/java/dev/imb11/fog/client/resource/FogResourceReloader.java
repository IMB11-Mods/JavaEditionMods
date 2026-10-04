package dev.imb11.fog.client.resource;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import dev.imb11.fog.api.CustomFogDefinition;
import dev.imb11.fog.client.FogClient;
import dev.imb11.fog.client.registry.FogRegistry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

import static dev.imb11.fog.client.FogClient.MOD_ID;

public class FogResourceReloader extends SimplePreparableReloadListener<Void> {
	public static final @NotNull Identifier IDENTIFIER = Identifier.fromNamespaceAndPath(MOD_ID, "resource_reloader");
	private static final @NotNull String TAG_FOLDER_NAME = "tag";
	public static final @NotNull String FOG_DEFINITIONS_FOLDER_NAME = "fog_definitions";
	public static final @NotNull String STRUCTURE_FOLDER_NAME = "structure";
	public static final @NotNull String STRUCTURE_TAGS_FOLDER_NAME = String.format("%s/%s", TAG_FOLDER_NAME, STRUCTURE_FOLDER_NAME);
	public static final @NotNull String BIOME_FOLDER_NAME = "biome";
	public static final @NotNull String BIOME_TAGS_FOLDER_NAME = String.format("%s/%s", TAG_FOLDER_NAME, BIOME_FOLDER_NAME);

	private static final @NotNull Gson GSON = new Gson();
	private static final @NotNull String JSON_FILE_SUFFIX = ".json";

	/**
	 * The preparation stage, ran on worker threads.
	 */
	@Override
	protected @Nullable Void prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
		FogRegistry.resetCaches();

		loadTaggedFogs(resourceManager, STRUCTURE_TAGS_FOLDER_NAME, FogRegistry.getStructureTagFogRegistry(), Registries.STRUCTURE);
		loadTaggedFogs(resourceManager, BIOME_TAGS_FOLDER_NAME, FogRegistry.getBiomeTagFogRegistry(), Registries.BIOME);
		loadFogs(resourceManager, STRUCTURE_FOLDER_NAME, FogRegistry.getStructureFogRegistry());
		loadFogs(resourceManager, BIOME_FOLDER_NAME, FogRegistry.getBiomeFogRegistry());
		return null;
	}

	/**
	 * The apply stage, ran on the main thread.
	 */
	@Override
	protected void apply(@Nullable Void prepared, ResourceManager resourceManager, ProfilerFiller profiler) {
		// NO-OP
	}

	private <K> void loadFogs(@NotNull ResourceManager resourceManager, @NotNull String folderName, @NotNull Map<K, CustomFogDefinition> fogRegistry, @NotNull Function<String, K> keyGenerator) {
		fogRegistry.clear();

		@NotNull var jsonFogs = resourceManager.listResources(String.format("%s/%s", FOG_DEFINITIONS_FOLDER_NAME, folderName), identifier -> identifier.toString().endsWith(JSON_FILE_SUFFIX));
		for (@NotNull var jsonFog : jsonFogs.entrySet()) {
			@NotNull var jsonFogPath = jsonFog.getKey();
			@NotNull var jsonFogPathSplit = jsonFogPath.getPath().replace(JSON_FILE_SUFFIX, "").split("/");
			@NotNull var fogIdentifier = Identifier.fromNamespaceAndPath(jsonFogPath.getNamespace(), jsonFogPathSplit[jsonFogPathSplit.length - 1]);
			K fogKey = keyGenerator.apply(fogIdentifier.toString());

			if (fogRegistry.containsKey(fogKey)) {
				continue;
			}

			try {
				fogRegistry.put(fogKey,
						CustomFogDefinition.CODEC.parse(JsonOps.INSTANCE, GSON.fromJson(new String(jsonFog.getValue().open().readAllBytes()), JsonElement.class)).result().orElseThrow());
			} catch (IOException e) {
				FogClient.LOGGER.error("Exception thrown while deserializing a fog definition (identifier: {}): {}", fogIdentifier, e);
			}
		}
	}

	private <T> void loadTaggedFogs(@NotNull ResourceManager resourceManager, @NotNull String folderName, @NotNull Map<TagKey<T>, CustomFogDefinition> fogRegistry,
			ResourceKey<? extends Registry<T>> registryKey) {
		loadFogs(resourceManager, folderName, fogRegistry, identifier -> TagKey.create(registryKey, Identifier.tryParse(identifier)));
	}

	private void loadFogs(@NotNull ResourceManager resourceManager, @NotNull String folderName, @NotNull Map<Identifier, CustomFogDefinition> fogRegistry) {
		loadFogs(resourceManager, folderName, fogRegistry, Identifier::tryParse);
	}
}
