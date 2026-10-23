package dev.imb11.snow;

import cc.cassian.mru.util.CommonUtils;
import dev.imb11.snow.config.SnowConfig;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Snow {
    public static final String MOD_ID = "snow";
    public static final Logger LOGGER = LoggerFactory.getLogger("Snow");

    public static Identifier id(String path) {
        return CommonUtils.id(MOD_ID, path);
    }

    public static void init() {
        SnowConfig.load();
    }
}
