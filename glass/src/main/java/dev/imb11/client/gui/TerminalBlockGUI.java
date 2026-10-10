package dev.imb11.client.gui;

import dev.imb11.blocks.GBlocks;
import dev.imb11.platform.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public final class TerminalBlockGUI extends ChannelMenu {
    public static final MenuType<TerminalBlockGUI> SCREEN_HANDLER_TYPE = Platform.menuType(TerminalBlockGUI::new, BlockPos.STREAM_CODEC);

    public TerminalBlockGUI(int id, Inventory inventory, BlockPos pos) {
        super(SCREEN_HANDLER_TYPE, id, inventory, pos, GBlocks.TERMINAL);
    }
}
