//? fabric {
package dev.imb11.snow.datagen;

import dev.imb11.snow.Snow;
import dev.imb11.snow.accumulation.SnowProfiles;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import org.jetbrains.annotations.Nullable;

public class SnowDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();
        pack.addProvider(SnowProfileProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(SnowProfiles.KEY, SnowProfileProvider::bootstrap);
    }

    @Override
    public @Nullable String getEffectiveModId() {
        return Snow.MOD_ID;
    }
}
//?}
