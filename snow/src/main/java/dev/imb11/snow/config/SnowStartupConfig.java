package dev.imb11.snow.config;

import cc.cassian.mru.Platform;
import com.google.gson.GsonBuilder;
import dev.imb11.mru.yacl.EntryType;
import dev.imb11.snow.Snow;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionFlag;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;

import static dev.imb11.snow.config.SnowConfig.HELPER;

public class SnowStartupConfig {
    private static final ConfigClassHandler<SnowStartupConfig> HANDLER = ConfigClassHandler
            .createBuilder(SnowStartupConfig.class)
            .id(Snow.id("startup"))
            .serializer(config -> GsonConfigSerializerBuilder
                    .create(config)
                    .setPath(Platform.INSTANCE.configPath().resolve(Snow.MOD_ID + "-startup.json"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .build())
            .build();

    @SerialEntry
    public boolean snowlogging = true;

    public static SnowStartupConfig get() {
        return HANDLER.instance();
    }

    public static void load() {
        HANDLER.load();
    }

    public static void save() {
        HANDLER.save();
    }

    public static ConfigCategory category() {
        SnowStartupConfig config = HANDLER.instance();
        SnowStartupConfig defaults = HANDLER.defaults();
        return ConfigCategory.createBuilder()
                .name(HELPER.getText(EntryType.CATEGORY_NAME, "startup"))
                .option(Option.<Boolean>createBuilder()
                        .name(HELPER.getText(EntryType.OPTION_NAME, "startup.snowlogging"))
                        .description(HELPER.get("startup.snowlogging", false))
                        .binding(defaults.snowlogging, () -> config.snowlogging, value -> config.snowlogging = value)
                        .controller(option -> BooleanControllerBuilder.create(option).coloured(true).trueFalseFormatter())
                        .flag(OptionFlag.GAME_RESTART)
                        .build())
                .build();
    }
}
