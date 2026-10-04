package dev.imb11.fog.client.compat.polytone;

import dev.imb11.fog.client.FogClient;
import dev.imb11.fog.config.FogConfig;
import net.irisshaders.iris.api.v0.IrisApi;

public class IrisCompat {
	public static boolean shouldDisableMod() {
		return FogClient.isModInstalled("iris")
				&& FogConfig.getInstance().disableModWhenIrisShaderPackIsEnabled
				&& IrisApi.getInstance().isShaderPackInUse();
	}
}
