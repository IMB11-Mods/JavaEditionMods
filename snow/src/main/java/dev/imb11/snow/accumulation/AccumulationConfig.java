package dev.imb11.snow.accumulation;

import dev.imb11.mru.yacl.EntryType;
import dev.isxander.yacl3.api.ConfigCategory;

import static dev.imb11.snow.config.SnowConfig.HELPER;

public class AccumulationConfig {
    public boolean enabled = true;

    public ConfigCategory category(AccumulationConfig defaults) {
        return ConfigCategory.createBuilder()
                .name(HELPER.getText(EntryType.CATEGORY_NAME, "accumulation"))
                .option(HELPER.get("accumulation.enabled", defaults.enabled, () -> enabled, value -> enabled = value))
                .build();
    }
}
