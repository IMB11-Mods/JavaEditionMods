package dev.imb11.fog.client.util;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.imb11.fog.config.FogConfig;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
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

		KeyMappingRegistry.register(toggleKeybind);

		ClientTickEvent.CLIENT_POST.register(client -> {
			while (toggleKeybind.consumeClick()) {
				FogConfig config = FogConfig.getInstance();
				config.enableMod = !config.enableMod;

				FogConfig.save();

				client.gui.getChat().addClientSystemMessage(Component.literal("§b§7[§rFog§b§7]§r ").append(
						Component.translatable("fog.command.toggle." + (config.enableMod ? "enabled" : "disabled")).withStyle(ChatFormatting.GOLD)));
			}
		});
	}
}
