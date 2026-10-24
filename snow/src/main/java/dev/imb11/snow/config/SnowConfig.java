package dev.imb11.snow.config;

import cc.cassian.mru.Platform;
import com.google.gson.GsonBuilder;
import dev.imb11.mru.sync.SyncedConfig;
import dev.imb11.mru.sync.client.SyncClientNetworking;
import dev.imb11.mru.yacl.ConfigHelper;
import dev.imb11.snow.Snow;
import dev.imb11.snow.accumulation.AccumulationConfig;
import dev.imb11.snow.generation.GenerationConfig;
import dev.imb11.snow.harvesting.HarvestingConfig;
import dev.imb11.snow.melting.MeltingConfig;
import dev.isxander.yacl3.api.ConfigCategory;
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
    public AccumulationConfig accumulation = new AccumulationConfig();
    @SerialEntry
    public GenerationConfig generation = new GenerationConfig();
    @SerialEntry
    public MeltingConfig melting = new MeltingConfig();
    @SerialEntry
    public HarvestingConfig harvesting = new HarvestingConfig();

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
        config.melting = received.melting;
        config.harvesting = received.harvesting;
        HANDLER.save();
    }

    public static YetAnotherConfigLib getYACL() {
        SnowConfig config = SYNC.get();
        SnowConfig defaults = HANDLER.defaults();
        boolean remote = SYNC.isRemote();
        boolean editable = SyncClientNetworking.canEdit(SYNC);
        List<ConfigCategory> categories = List.of(
                config.accumulation.category(defaults.accumulation),
                config.generation.category(defaults.generation),
                config.melting.category(defaults.melting),
                config.harvesting.category(defaults.harvesting)
        );
        if (!editable) {
            categories.forEach(category -> category.groups().forEach(group -> group.options().forEach(option -> option.setAvailable(false))));
        }

        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("snow.config.title"))
                .categories(categories)
                .category(SnowStartupConfig.category())
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
