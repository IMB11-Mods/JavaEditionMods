package dev.imb11.snow.harvesting;

import dev.imb11.mru.yacl.EntryType;
import dev.isxander.yacl3.api.ConfigCategory;

import static dev.imb11.snow.config.SnowConfig.HELPER;

public class HarvestingConfig {
    public boolean shovelHarvesting = true;

    public ConfigCategory category(HarvestingConfig defaults) {
        return ConfigCategory.createBuilder()
                .name(HELPER.getText(EntryType.CATEGORY_NAME, "harvesting"))
                .option(HELPER.get("harvesting.shovel_harvesting", defaults.shovelHarvesting, () -> shovelHarvesting, value -> shovelHarvesting = value))
                .build();
    }
}
