package dev.imb11.snow.generation;

import dev.imb11.mru.yacl.EntryType;
import dev.isxander.yacl3.api.ConfigCategory;

import static dev.imb11.snow.config.SnowConfig.HELPER;

public class GenerationConfig {
    public boolean enabled = true;

    public ConfigCategory category(GenerationConfig defaults) {
        return ConfigCategory.createBuilder()
                .name(HELPER.getText(EntryType.CATEGORY_NAME, "generation"))
                .option(HELPER.get("generation.enabled", defaults.enabled, () -> enabled, value -> enabled = value))
                .build();
    }
}
