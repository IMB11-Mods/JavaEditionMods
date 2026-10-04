package dev.imb11.fog.client.util.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.attribute.EnvironmentAttributes;
import org.jetbrains.annotations.NotNull;

public class ClientWorldUtil {
    public static boolean isFogDenseAtPosition(@NotNull ClientLevel level, @NotNull BlockPos position) {
        return level.environmentAttributes().getValue(EnvironmentAttributes.FOG_END_DISTANCE, position.getCenter()) <= 96.0F
                || Minecraft.getInstance().gui.getBossOverlay().shouldCreateWorldFog();
    }
}
