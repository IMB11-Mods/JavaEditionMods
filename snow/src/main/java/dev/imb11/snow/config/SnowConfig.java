package dev.imb11.snow.config;

import cc.cassian.mru.Platform;
import com.google.gson.GsonBuilder;
import dev.imb11.mru.yacl.ConfigHelper;
import dev.imb11.snow.Snow;
import dev.imb11.snow.accumulation.AccumulationConfig;
import dev.imb11.snow.generation.GenerationConfig;
import dev.imb11.snow.harvesting.HarvestingConfig;
import dev.imb11.snow.melting.MeltingConfig;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.minecraft.network.chat.Component;

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

    @SerialEntry
    public AccumulationConfig accumulation = new AccumulationConfig();
    @SerialEntry
    public GenerationConfig generation = new GenerationConfig();
    @SerialEntry
    public MeltingConfig melting = new MeltingConfig();
    @SerialEntry
    public HarvestingConfig harvesting = new HarvestingConfig();

    public static SnowConfig get() {
        return HANDLER.instance();
    }

    public static void load() {
        HANDLER.load();
    }

    public static YetAnotherConfigLib getYACL() {
        return YetAnotherConfigLib.create(HANDLER, (defaults, config, builder) -> builder
                .title(Component.translatable("snow.config.title"))
                .category(config.accumulation.category(defaults.accumulation))
                .category(config.generation.category(defaults.generation))
                .category(config.melting.category(defaults.melting))
                .category(config.harvesting.category(defaults.harvesting))
                .save(HANDLER::save));
    }
}
