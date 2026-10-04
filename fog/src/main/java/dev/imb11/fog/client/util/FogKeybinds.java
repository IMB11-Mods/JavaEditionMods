package dev.imb11.fog.client.util;

import dev.imb11.fog.config.FogConfig;
//? fabric {
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
//?}
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class FogKeybinds {
	public static KeyMapping toggleKeybind;

	public static void init() {

		toggleKeybind = new KeyMapping(
				"key.fog.toggle",
				InputConstants.UNKNOWN.getValue(),

				KeyMapping.Category.MISC

		);

		//? fabric {
		KeyMappingHelper.registerKeyMapping(toggleKeybind);
		ClientTickEvents.END_LEVEL_TICK.register((level)->FogKeybinds.tickKeyMapping());
		//?}
	}

	public static void tickKeyMapping() {
		while (toggleKeybind.consumeClick()) {
			FogConfig config = FogConfig.getInstance();
			config.enableMod = !config.enableMod;

			FogConfig.save();

			Minecraft.getInstance().gui.getChat().addClientSystemMessage(Component.literal("§b§7[§rFog§b§7]§r ").append(
					Component.translatable("fog.command.toggle." + (config.enableMod ? "enabled" : "disabled")).withStyle(ChatFormatting.GOLD)));
		}
	}
}
