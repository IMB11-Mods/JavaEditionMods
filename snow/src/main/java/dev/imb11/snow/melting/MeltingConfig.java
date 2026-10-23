package dev.imb11.snow.melting;

import dev.imb11.mru.yacl.EntryType;
import dev.isxander.yacl3.api.ConfigCategory;

import static dev.imb11.snow.config.SnowConfig.HELPER;

public class MeltingConfig {
    public boolean gradualMelting = true;
    public boolean meltInWarmBiomes = false;

    public ConfigCategory category(MeltingConfig defaults) {
        return ConfigCategory.createBuilder()
                .name(HELPER.getText(EntryType.CATEGORY_NAME, "melting"))
                .option(HELPER.get("melting.gradual_melting", defaults.gradualMelting, () -> gradualMelting, value -> gradualMelting = value))
                .option(HELPER.get("melting.melt_in_warm_biomes", defaults.meltInWarmBiomes, () -> meltInWarmBiomes, value -> meltInWarmBiomes = value))
                .build();
    }
}
