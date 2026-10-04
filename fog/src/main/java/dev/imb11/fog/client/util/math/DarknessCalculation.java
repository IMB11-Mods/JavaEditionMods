package dev.imb11.fog.client.util.math;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record DarknessCalculation(float fogStart, float fogEnd, float darknessValue) {
	@Contract("_, _, _, _ -> new")
	public static @NotNull DarknessCalculation of(@NotNull Minecraft client, float fogStart, float fogEnd, float deltaTick) {
		float renderDistance = client.options.getEffectiveRenderDistance() * 16.0F;
		Entity entity = client.getCameraEntity();
		float darknessValue = 0.0F;
		if (!(entity instanceof LivingEntity livingEntity)) {
			return new DarknessCalculation(fogStart, fogEnd, darknessValue);
		}

		if (livingEntity.hasEffect(MobEffects.BLINDNESS)) {
			fogStart = (4 * 16) / renderDistance;
			fogEnd = (8 * 16) / renderDistance;
			darknessValue = 1.0F;
		} else if (livingEntity.hasEffect(MobEffects.DARKNESS)) {
			MobEffectInstance effect = livingEntity.getEffect(MobEffects.DARKNESS);
			if (effect != null) {
				float factor = client.options.darknessEffectScale().get().floatValue();
				float intensity = effect.getBlendFactor(livingEntity, deltaTick) * factor;

				float darknessScale = calculateDarknessScale(livingEntity, deltaTick);
				fogStart = ((8.0F * 16) / renderDistance) * (1 - darknessScale);
				fogEnd = (15.0F * 16) / renderDistance;
				darknessValue = intensity;
			}
		}

		return new DarknessCalculation(fogStart, fogEnd, darknessValue);
	}

	private static float calculateDarknessScale(@NotNull LivingEntity entity, float deltaTick) {
		float darknessFactor = entity.getEffect(MobEffects.DARKNESS).getBlendFactor(entity, deltaTick);

		float factor = 0.45F * darknessFactor;
		return Math.max(0.0F, Mth.cos((entity.tickCount - deltaTick) * (float) Math.PI * 0.025F) * factor);
	}
}
