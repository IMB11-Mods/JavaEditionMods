package dev.imb11.fog.client.compat.polytone;

import dev.imb11.fog.client.FogClient;
import dev.imb11.fog.client.util.color.Color;
import dev.imb11.fog.config.FogConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.attribute.EnvironmentAttributes;
//? if <26.3 {
import net.mehvahdjukaar.polytone.Polytone;
//?}
import org.jetbrains.annotations.Nullable;

public class PolytoneCompat {
    public static boolean shouldUsePolytone() {
        //? if <26.3 {
        return FogClient.isModInstalled("polytone") && FogConfig.getInstance().prioritizePolytoneFogDefinitions;
        //?} else {
        /*return false;
        *///?}
    }

    public static @Nullable Color getFogColorFromPolytone(float partialTick) {
        //? if <26.3 {
        if (!shouldUsePolytone()) {
            return null;
        }

        var client = Minecraft.getInstance();
        var level = client.level;
        if (level == null) {
            return null;
        }

        var camera = client.gameRenderer.getMainCamera();
        var position = camera.position();
        var biome = level.getBiomeManager().getNoiseBiomeAtPosition(position.x, position.y, position.z).value();
        var effects = Polytone.BIOME_MODIFIERS.modifiersByBiome().get(biome);
        if (effects == null || !effects.attributeModifications().getAllModifiedAttributes().contains(EnvironmentAttributes.FOG_COLOR)) {
            return null;
        }

        return new Color(camera.attributeProbe().getValue(EnvironmentAttributes.FOG_COLOR, partialTick));
        //?} else {
        /*return null;
        *///?}
    }
}
