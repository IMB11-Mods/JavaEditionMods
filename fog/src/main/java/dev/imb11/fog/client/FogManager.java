package dev.imb11.fog.client;

import dev.imb11.fog.api.CustomFogDefinition;
import dev.imb11.fog.api.FogColors;
import dev.imb11.fog.client.compat.polytone.PolytoneCompat;
import dev.imb11.fog.client.registry.FogRegistry;
import dev.imb11.fog.client.util.TickUtil;
import dev.imb11.fog.client.util.color.Color;
import dev.imb11.fog.client.util.math.DarknessCalculation;
import dev.imb11.fog.client.util.math.InterpolatedValue;
import dev.imb11.fog.client.util.math.MathUtil;
import dev.imb11.fog.client.util.player.PlayerUtil;
import dev.imb11.fog.client.util.world.ClientWorldUtil;
import dev.imb11.fog.config.FogConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FogManager {
	public static FogManager INSTANCE = new FogManager();

	public final InterpolatedValue raininess;
	public final InterpolatedValue undergroundness;
	public final InterpolatedValue fogStart;
	public final InterpolatedValue fogEnd;
	public final InterpolatedValue darkness;
	public final InterpolatedValue fogColorRed;
	public final InterpolatedValue fogColorGreen;
	public final InterpolatedValue fogColorBlue;
	public final InterpolatedValue currentSkyLight;
	public final InterpolatedValue currentBlockLight;
	public final InterpolatedValue currentLight;
	public final InterpolatedValue currentStartMultiplier;
	public final InterpolatedValue currentEndMultiplier;

	public boolean hasSetup = false;

	public FogManager() {
		@NotNull FogConfig config = FogConfig.getInstance();

		raininess = new InterpolatedValue(0.0f, config.raininessTransitionSpeed);
		undergroundness = new InterpolatedValue(0.0f, config.undergroundnessTransitionSpeed);
		fogStart = new InterpolatedValue(config.initialFogStart, config.fogStartTransitionSpeed);
		fogEnd = new InterpolatedValue(config.initialFogEnd, config.fogEndTransitionSpeed);
		darkness = new InterpolatedValue(0.0f, config.darknessTransitionSpeed);
		fogColorRed = new InterpolatedValue(-1.0f, config.fogColorTransitionSpeed);
		fogColorGreen = new InterpolatedValue(-1.0f, config.fogColorTransitionSpeed);
		fogColorBlue = new InterpolatedValue(-1.0f, config.fogColorTransitionSpeed);
		currentSkyLight = new InterpolatedValue(16.0F);
		currentBlockLight = new InterpolatedValue(16.0F);
		currentLight = new InterpolatedValue(16.0F);
		currentStartMultiplier = new InterpolatedValue(1.0F, config.startMultiplierTransitionSpeed);
		currentEndMultiplier = new InterpolatedValue(1.0F, config.endMultiplierTransitionSpeed);

		fogStart.resetTo(config.initialFogStart);
		fogEnd.resetTo(config.initialFogEnd);
	}

	public static @NotNull FogManager getInstance() {
		return INSTANCE;
	}

	public static boolean isInDisabledBiome() {
		@NotNull var client = Minecraft.getInstance();
		@Nullable var world = client.level;
		@Nullable var player = client.player;
		if (world == null || player == null) {
			return false;
		}

		return FogConfig.getInstance().disabledBiomes.contains(world.getBiome(player.blockPosition()).unwrapKey().map(key -> key.identifier().toString()).orElse(""));
	}

	private static float getBlendFactor(@NotNull ClientLevel world) {
		long time = Math.floorMod(world.getDefaultClockTime(), 24000);
		float blendFactor;
		if (time < 11000) {
			// Daytime
			blendFactor = 1.0f;
		} else if (time < 13000) {
			// Blend from day to night
			blendFactor = MathUtil.lerp(1.0f, 0.0f, (time - 11000) / 2000f);
		} else if (time < 22000) {
			// Nighttime
			blendFactor = 0.0f;
		} else if (time < 23000) {
			// Blend from night to day
			blendFactor = MathUtil.lerp(0.0f, 1.0f, (time - 22000) / 1000f);
		} else {
			// Constant day from 23000 to 24000 ticks
			blendFactor = 1.0f;
		}
		return blendFactor;
	}

	public void onEndTick(@NotNull ClientLevel clientWorld) {
		@NotNull final var client = Minecraft.getInstance();
		@Nullable final var clientPlayer = client.player;
		if (clientPlayer == null) {
			return;
		}

		@Nullable final BlockPos clientPlayerBlockPosition = clientPlayer.blockPosition();
		if (clientPlayerBlockPosition == null) {
			return;
		}

		var isClientPlayerAboveGround = PlayerUtil.isPlayerAboveGround(clientPlayer);
		if (isClientPlayerAboveGround) {
			this.undergroundness.interpolate(0f);
		} else {
			this.undergroundness.interpolate(1f);
		}

		if (isClientPlayerAboveGround && clientWorld.getBiome(
				clientPlayer.blockPosition()).value().hasPrecipitation() && clientWorld.isRaining()) {
			raininess.interpolate(clientWorld.isThundering() ? 1f : 0.5f);
		} else {
			raininess.interpolate(0f);
		}

		float density = ClientWorldUtil.isFogDenseAtPosition(clientWorld, clientPlayerBlockPosition) ? 0.9f : 1.0f;
		float tickDelta = TickUtil.getTickDelta();

		DarknessCalculation darknessCalculation = DarknessCalculation.of(
				client, fogStart.getDefaultValue(), fogEnd.getDefaultValue() * density, tickDelta);
		var biomeKey = clientWorld.getBiome(clientPlayer.blockPosition());
		@NotNull var clientPlayerBiomeKeyOptional = biomeKey.unwrapKey();
		if (clientPlayerBiomeKeyOptional.isEmpty()) {
			return;
		}

		CustomFogDefinition fogDefinition = FogRegistry.getFogDefinitionOrDefault(
				clientPlayerBiomeKeyOptional.get().identifier(), clientWorld);
		@Nullable FogColors colors = fogDefinition.colors();
		if (colors == null || !FogConfig.getInstance().enableBiomeSpecificFogColors) {
			colors = FogColors.getDefault(clientWorld);
		}
		float blendFactor = getBlendFactor(clientWorld);
		Color finalNightColor = getFinalNightColor(clientWorld, colors);

		// Base day/night color blending
		float red = Mth.lerp(blendFactor, finalNightColor.red / 255f, colors.getDayColor().red / 255f);
		float green = Mth.lerp(blendFactor, finalNightColor.green / 255f, colors.getDayColor().green / 255f);
		float blue = Mth.lerp(blendFactor, finalNightColor.blue / 255f, colors.getDayColor().blue / 255f);

		Color polytoneColor = PolytoneCompat.getFogColorFromPolytone(tickDelta);
		if (polytoneColor != null) {
			red = polytoneColor.red / 255f;
			green = polytoneColor.green / 255f;
			blue = polytoneColor.blue / 255f;
		}

		if (!hasSetup) {
			this.fogColorRed.set(red);
			this.fogColorGreen.set(green);
			this.fogColorBlue.set(blue);
			hasSetup = true;
		} else {
			this.fogColorRed.interpolate(red);
			this.fogColorGreen.interpolate(green);
			this.fogColorBlue.interpolate(blue);
		}

		this.currentStartMultiplier.interpolate(fogDefinition.startMultiplier());
		this.currentEndMultiplier.interpolate(fogDefinition.endMultiplier());

		this.fogStart.interpolate(darknessCalculation.fogStart());
		this.fogEnd.interpolate(darknessCalculation.fogEnd());
		this.darkness.interpolate(darknessCalculation.darknessValue());

		this.currentSkyLight.interpolate(clientWorld.getBrightness(LightLayer.SKY, clientPlayerBlockPosition));
		this.currentBlockLight.interpolate(clientWorld.getBrightness(LightLayer.BLOCK, clientPlayerBlockPosition));
		this.currentLight.interpolate(clientWorld.getMaxLocalRawBrightness(clientPlayerBlockPosition, 0));
	}

	private @NotNull Color getFinalNightColor(@NotNull ClientLevel world, FogColors fogColors) {
		if (!FogConfig.getInstance().enableMoonFogColorInfluence) {
			return fogColors.getNightColor();
		}

		@NotNull Color newMoonColor = Color.from(FogConfig.getInstance().newMoonColor);
		float blendFactor = switch (world.environmentAttributes().getValue(EnvironmentAttributes.MOON_PHASE, Minecraft.getInstance().gameRenderer.getMainCamera().position()).index()) {
			case 0 -> 0.0f;
			case 1, 7 -> 0.25f;
			case 2, 6 -> 0.5f;
			case 3, 5 -> 0.75f;
			case 4 -> 1.0f;
			default -> 1.0f;
		};
		return fogColors.getNightColor().lerp(newMoonColor, blendFactor);
	}

	public float getUndergroundFactor(@NotNull Minecraft client, float tickDelta) {
		@Nullable var clientCamera = client.getCameraEntity();
		@Nullable var clientWorld = client.level;
		if (clientCamera == null || clientWorld == null) {
			return 0.0F;
		}

		float clientCameraYPosition = (float) clientCamera.getY();
		float seaLevel = clientWorld.getSeaLevel();
		// Map the client camera's Y position to a factor between 0 and 1 based on the sea level (+/- 32)
		float yFactor = Mth.clamp(
				MathUtil.mapRange(seaLevel - 32.0F, seaLevel + 32.0F, 1.0F, 0.0F, clientCameraYPosition), 0.0F, 1.0F);
		float undergroundnessValue = this.undergroundness.get(tickDelta);
		float skyLight = this.currentSkyLight.get(tickDelta);
		// Calculate the underground factor by lerping between yFactor, undergroundness, and sky light
		return Mth.lerp(yFactor, 1.0F - undergroundnessValue, skyLight / 16.0F);
	}

	public @NotNull FogSettings getFogSettings(float tickDelta, float viewDistance) {
		@NotNull var fogConfig = FogConfig.getInstance();
		float fogStart = this.fogStart.get(tickDelta) * this.currentStartMultiplier.get(tickDelta);
		float fogEnd = this.fogEnd.get(tickDelta) * this.currentEndMultiplier.get(tickDelta);
		float fogRed = this.fogColorRed.get(tickDelta);
		float fogGreen = this.fogColorGreen.get(tickDelta);
		float fogBlue = this.fogColorBlue.get(tickDelta);

		// Sunrise/sunset
		float[] sunriseSunsetAdjustedFogColors = blendFogColorWithSunriseSunsetColors(
				Minecraft.getInstance(), fogRed, fogGreen, fogBlue, tickDelta);
		fogRed = sunriseSunsetAdjustedFogColors[0];
		fogGreen = sunriseSunsetAdjustedFogColors[1];
		fogBlue = sunriseSunsetAdjustedFogColors[2];

		// Darkness
		float darknessFogColorMultiplier = 1f - this.darkness.get(tickDelta);
		fogRed *= darknessFogColorMultiplier;
		fogGreen *= darknessFogColorMultiplier;
		fogBlue *= darknessFogColorMultiplier;

		// "Undergroundness"
		float undergroundness = this.undergroundness.get(tickDelta);
		float undergroundFogMultiplier = 1f - undergroundness * fogConfig.undergroundFogMultiplier;
		fogStart *= undergroundFogMultiplier;
		fogEnd *= undergroundFogMultiplier;

		// Raininess
		float rainFogMultiplier = 1f - Math.clamp(this.raininess.get(tickDelta) - undergroundness, 0f, 1f) * fogConfig.rainFogMultiplier;
		fogStart *= rainFogMultiplier;
		fogEnd *= Math.clamp(rainFogMultiplier, 0.5f, 1f);
		fogRed *= rainFogMultiplier;
		fogGreen *= rainFogMultiplier;
		fogBlue *= rainFogMultiplier;

		return new FogSettings(fogStart * viewDistance, fogEnd * viewDistance, fogRed, fogGreen, fogBlue);
	}

	/**
	 * Applies sunrise/sunset color blending to fog color.
	 * Uses vanilla Minecraft's sunset detection for accurate timing.
	 */
	private float[] blendFogColorWithSunriseSunsetColors(@NotNull Minecraft client, float red, float green, float blue, float tickDelta) {
		if (!FogConfig.getInstance().enableSunFogColorInfluence || client.level == null) {
			return new float[]{red, green, blue};
		}

        var probe = client.gameRenderer.getMainCamera().attributeProbe();
        int sunsetColor = probe.getValue(EnvironmentAttributes.SUNRISE_SUNSET_COLOR, tickDelta);
        float alpha = ARGB.alphaFloat(sunsetColor);
        if (alpha > 0.0F) {
            float sunAngle = probe.getValue(EnvironmentAttributes.SUN_ANGLE, tickDelta) * Mth.DEG_TO_RAD;
            float blend = Mth.clamp(1.0F - Math.abs(Mth.cos(sunAngle)) / 0.4F, 0.0F, 1.0F);
            blend = blend * blend * (3.0F - 2.0F * blend) * 0.7F;
            red = Mth.lerp(blend, red, ARGB.redFloat(sunsetColor));
            green = Mth.lerp(blend, green, ARGB.greenFloat(sunsetColor));
            blue = Mth.lerp(blend, blue, ARGB.blueFloat(sunsetColor));
        }
		return new float[]{red, green, blue};
	}

	public record FogSettings(double fogStart, double fogEnd, float fogRed, float fogGreen, float fogBlue) {}
}
