package dev.imb11.snow.config;

import cc.cassian.mru.Platform;
import com.google.gson.GsonBuilder;
import dev.imb11.mru.sync.SyncedConfig;
import dev.imb11.mru.sync.client.SyncClientNetworking;
import dev.imb11.mru.yacl.ConfigHelper;
import dev.imb11.snow.Snow;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.minecraft.network.chat.Component;

import java.util.List;

public class SnowConfig {
    public static final ConfigHelper HELPER = new ConfigHelper(Snow.MOD_ID, "config");
    private static final ConfigClassHandler<SnowConfig> HANDLER = ConfigClassHandler
            .createBuilder(SnowConfig.class)
            .id(Snow.id("config"))
            .serializer(config -> GsonConfigSerializerBuilder
                    .create(config)
                    .setPath(Platform.INSTANCE.configPath().resolve(Snow.MOD_ID + ".json"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .build())
            .build();
    public static final SyncedConfig<SnowConfig> SYNC = SyncedConfig
            .register(Snow.id("config"), SnowConfig.class, HANDLER::instance, SnowConfig::applyFromOperator)
            .requireClient(() -> SnowStartupConfig.get().snowlogging);

    @SerialEntry
    public boolean accumulation = true;
    @SerialEntry
    public boolean generation = true;
    @SerialEntry
    public boolean gradualMelting = true;
    @SerialEntry
    public boolean meltInWarmBiomes = false;
    @SerialEntry
    public boolean shovelHarvesting = true;

    public static SnowConfig get() {
        return SYNC.get();
    }

    public static void load() {
        HANDLER.load();
    }

    private static void applyFromOperator(SnowConfig received) {
        SnowConfig config = HANDLER.instance();
        config.accumulation = received.accumulation;
        config.generation = received.generation;
        config.gradualMelting = received.gradualMelting;
        config.meltInWarmBiomes = received.meltInWarmBiomes;
        config.shovelHarvesting = received.shovelHarvesting;
        HANDLER.save();
    }

    public static YetAnotherConfigLib getYACL() {
        SnowConfig config = SYNC.get();
        SnowConfig defaults = HANDLER.defaults();
        boolean remote = SYNC.isRemote();
        boolean editable = SyncClientNetworking.canEdit(SYNC);
        List<Option<Boolean>> options = List.of(
                HELPER.get("accumulation.enabled", defaults.accumulation, () -> config.accumulation, value -> config.accumulation = value),
                HELPER.get("generation.enabled", defaults.generation, () -> config.generation, value -> config.generation = value),
                HELPER.get("melting.gradual_melting", defaults.gradualMelting, () -> config.gradualMelting, value -> config.gradualMelting = value),
                HELPER.get("melting.melt_in_warm_biomes", defaults.meltInWarmBiomes, () -> config.meltInWarmBiomes, value -> config.meltInWarmBiomes = value),
                HELPER.get("harvesting.shovel_harvesting", defaults.shovelHarvesting, () -> config.shovelHarvesting, value -> config.shovelHarvesting = value)
        );
        if (!editable) {
            options.forEach(option -> option.setAvailable(false));
        }

        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("snow.config.title"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("snow.config.title"))
                        .options(options)
                        .option(SnowStartupConfig.option())
                        .build())
                .save(() -> {
                    if (remote) {
                        if (editable) {
                            SyncClientNetworking.requestUpdate(SYNC, config);
                        }
                    } else {
                        HANDLER.save();
                        SYNC.broadcast();
                    }
                    SnowStartupConfig.save();
                })
                .build();
    }
}
