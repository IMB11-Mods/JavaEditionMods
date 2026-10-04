package dev.imb11.fog.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.imb11.fog.client.FogManager;
import dev.imb11.fog.client.util.TickUtil;
import dev.imb11.fog.client.util.color.Color;
import dev.imb11.fog.config.FogConfig;
//? fabric {
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;
//?} else {
/*
import net.minecraft.commands.CommandSourceStack;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
*///?}
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FogClientCommands {
	//~ if fabric 'CommandSourceStack'->'FabricClientCommandSource' {
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		LiteralArgumentBuilder<FabricClientCommandSource> fogNode = literal("fog");

		fogNode = fogNode.then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("reset").executes(commandContext -> reset(new ClientCommandContext(commandContext))));

		fogNode = fogNode.then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("toggle").executes(commandContext -> toggle(new ClientCommandContext(commandContext))));

		fogNode = fogNode.then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("debug").executes(commandContext -> outputDebug(new ClientCommandContext(commandContext))));

		dispatcher.register(fogNode);
	}
	//~}

	private static int outputDebug(ClientCommandContext commandContext) {
		@NotNull var client = Minecraft.getInstance();
		@Nullable ClientLevel clientWorld = client.level;
		if (clientWorld == null) {
			commandContext.sendFailure(Component.translatable("fog.command.debug.failure"));
			return 0;
		}

		@NotNull FogManager manager = FogManager.INSTANCE;
		float tickDelta = TickUtil.getTickDelta();
		@NotNull Color fogColor = new Color((int) (manager.fogColorRed.get(tickDelta) * 255),
				(int) (manager.fogColorGreen.get(tickDelta) * 255), (int) (manager.fogColorBlue.get(tickDelta) * 255));
		@NotNull String fogColorHex = String.format("#§c%02x§a%02x§9%02x", fogColor.red, fogColor.green, fogColor.blue);

		@SuppressWarnings("TextBlockMigration") @NotNull String debugInfoTable = String.format(
				"§b§7[§rFog§b§7]§r Current Fog Manager State:\n" +
						"§b§7[§rFog§b§7]§r Raininess: §6%.2f§r\n" +
						"§b§7[§rFog§b§7]§r \"Undergroundness\": §6%.2f§r\n" +
						"§b§7[§rFog§b§7]§r Fog Start: §6%.2f§r\n" +
						"§b§7[§rFog§b§7]§r Fog End: §6%.2f§r\n" +
						"§b§7[§rFog§b§7]§r Darkness: §6%.2f§r\n" +
						"§b§7[§rFog§b§7]§r Fog Color: §6%s§r\n" +
						"§b§7[§rFog§b§7]§r Current Sky Light: §6%.2f§r\n" +
						"§b§7[§rFog§b§7]§r Current Block Light: §6%.2f§r\n" +
						"§b§7[§rFog§b§7]§r Current Light: §6%.2f§r\n" +
						"§b§7[§rFog§b§7]§r Current Start Multiplier: §6%.2f§r\n" +
						"§b§7[§rFog§b§7]§r Current End Multiplier: §6%.2f§r",
				manager.raininess.get(tickDelta),
				manager.undergroundness.get(tickDelta),
				manager.fogStart.get(tickDelta),
				manager.fogEnd.get(tickDelta),
				manager.darkness.get(tickDelta),
				fogColorHex,
				manager.currentSkyLight.get(tickDelta),
				manager.currentBlockLight.get(tickDelta),
				manager.currentLight.get(tickDelta),
				manager.currentStartMultiplier.get(tickDelta),
				manager.currentEndMultiplier.get(tickDelta)
		);

		commandContext.sendFeedback(Component.literal(debugInfoTable));
		return 1;
	}

	private static int reset(@NotNull ClientCommandContext commandContext) {
		FogConfig.load();
		FogManager.INSTANCE = new FogManager();
		commandContext.sendFeedback(
				 Component.literal("§b§7[§rFog§b§7]§r ").append(Component.translatable("fog.command.reset").withStyle(ChatFormatting.GOLD)));
		return 1;
	}

	private static int toggle(@NotNull ClientCommandContext commandContext) {
		FogConfig config = FogConfig.getInstance();
		config.enableMod = !config.enableMod;

		FogConfig.save();

		commandContext.sendFeedback(Component.literal("§b§7[§rFog§b§7]§r ").append(
				Component.translatable("fog.command.toggle." + (config.enableMod ? "enabled" : "disabled")).withStyle(ChatFormatting.GOLD)));

		return 1;
	}

	//? fabric {

	private record ClientCommandContext(CommandContext<FabricClientCommandSource> commandContext) {
		public void sendFailure(MutableComponent failure) {
			commandContext.getSource().sendError(failure);
		}

		public void sendFeedback(MutableComponent feedback) {
			commandContext.getSource().sendFeedback(feedback);

		}
	}
	//?} else {

	/*private record ClientCommandContext(CommandContext<CommandSourceStack> commandContext) {

		public void sendFailure(MutableComponent failure) {
			commandContext.getSource().sendFailure(failure);
		}

		public void sendFeedback(MutableComponent feedback) {
			commandContext.getSource().sendSuccess(()-> feedback, false);
		}
	}
	*///?}
}
