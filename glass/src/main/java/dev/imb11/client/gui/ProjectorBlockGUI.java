package dev.imb11.client.gui;

import dev.imb11.blocks.GBlocks;
import dev.imb11.platform.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public final class ProjectorBlockGUI extends ChannelMenu {
    public static final MenuType<ProjectorBlockGUI> SCREEN_HANDLER_TYPE = Platform.menuType(ProjectorBlockGUI::new, BlockPos.STREAM_CODEC);

    public ProjectorBlockGUI(int id, Inventory inventory, BlockPos pos) {
        super(SCREEN_HANDLER_TYPE, id, inventory, pos, GBlocks.PROJECTOR);
    }
}
