//? fabric {
package dev.imb11.snow.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.imb11.snow.config.SnowConfig;

public class ModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> SnowConfig.getYACL().generateScreen(parent);
    }
}
//?}
