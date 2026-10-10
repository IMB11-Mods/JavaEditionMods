package dev.imb11.fog.client.util.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ClientWorldUtil {
    public static boolean isFogDenseAtPosition(@NotNull ClientLevel level, @NotNull BlockPos position) {
        return level.environmentAttributes().getValue(EnvironmentAttributes.FOG_END_DISTANCE, Vec3.atCenterOf(position)) <= 96.0F
                || Minecraft.getInstance().gui.hud.getBossOverlay().shouldCreateWorldFog();
    }
}
