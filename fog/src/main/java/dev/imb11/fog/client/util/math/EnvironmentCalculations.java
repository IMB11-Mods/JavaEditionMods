package dev.imb11.fog.client.util.math;

import dev.imb11.fog.api.FogColors;
import dev.imb11.fog.client.FogManager;
import dev.imb11.fog.client.util.color.Color;
import dev.imb11.fog.config.FogConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import org.jetbrains.annotations.NotNull;

public class EnvironmentCalculations {
    public static FogManager.FogSettings apply(float undergroundFactor, FogManager.FogSettings settings, float tickDelta) {
	    return fixElytraColor(
				applyCaveFog(undergroundFactor, settings),
			    tickDelta,
			    Minecraft.getInstance(),
			    Minecraft.getInstance().player,
			    Minecraft.getInstance().level
	    );
    }

	private static FogManager.@NotNull FogSettings applyCaveFog(float undergroundFactor, FogManager.FogSettings settings) {
		FogColors belowGroundColors = FogColors.DEFAULT_CAVE;
		Color belowColor = belowGroundColors.getNightColor();

		float fogColorR = Mth.lerp(undergroundFactor, belowColor.red / 255f, settings.fogRed()),
				fogColorG = Mth.lerp(undergroundFactor, belowColor.green / 255f, settings.fogGreen()),
				fogColorB = Mth.lerp(undergroundFactor, belowColor.blue / 255f, settings.fogBlue());

		return new FogManager.FogSettings(settings.fogStart(), settings.fogEnd(), fogColorR, fogColorG, fogColorB);
	}

	public static FogManager.FogSettings fixElytraColor(FogManager.FogSettings input, float tickDelta, Minecraft client, LocalPlayer player, ClientLevel world) {
		if (player == null || world == null || !FogConfig.getInstance().enableHighAltitudeFogColorInfluence) return input;

		float fogColorR = input.fogRed();
		float fogColorG = input.fogGreen();
		float fogColorB = input.fogBlue();

		int surfaceTopLevel = world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) player.getX(), (int) player.getZ());
		int viewCutoff = surfaceTopLevel + (client.options.renderDistance().get() * 32);
		int distancePastCutoff = (int) (player.getY() - viewCutoff);

		// TODO: Try and do something with fogStart here, eg: when fogStart as a blockpos is above the top level, start lerping.
		// Check if player cannot see the top of the surface using their viewDistance option
		if (client.player.getY() > viewCutoff) {
			// Range from viewCutoff to viewCutoff + 25
			float percentageCutoff = Mth.clamp(distancePastCutoff / 25f, 0, 1);
			Vec3 skyColour;

			skyColour = new Color(client.gameRenderer.mainCamera().attributeProbe().getValue(EnvironmentAttributes.SKY_COLOR, tickDelta)).asVec3d();

			// Lerp between the fog color and the sky color
			fogColorR = (float) Mth.lerp(percentageCutoff, fogColorR, skyColour.x);
			fogColorG = (float) Mth.lerp(percentageCutoff, fogColorG, skyColour.y);
			fogColorB = (float) Mth.lerp(percentageCutoff, fogColorB, skyColour.z);
		}

		return new FogManager.FogSettings(input.fogStart(), input.fogEnd(), fogColorR, fogColorG, fogColorB);
	}
}
