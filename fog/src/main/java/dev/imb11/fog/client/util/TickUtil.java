package dev.imb11.fog.client.util;

import net.minecraft.client.Minecraft;

public class TickUtil {
	public static float getTickDelta() {

		return Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);

	}
}
