package dev.imb11.snow;

import cc.cassian.mru.util.CommonUtils;
import dev.imb11.snow.config.SnowConfig;
import dev.imb11.snow.config.SnowStartupConfig;
import net.minecraft.resources.Identifier;

public class Snow {
    public static final String MOD_ID = "snow";

    public static Identifier id(String path) {
        return CommonUtils.id(MOD_ID, path);
    }

    public static void init() {
        SnowStartupConfig.load();
        SnowConfig.load();
    }
}
