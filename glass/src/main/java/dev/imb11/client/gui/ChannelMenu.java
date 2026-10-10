package dev.imb11.client.gui;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public abstract class ChannelMenu extends AbstractContainerMenu {
    public final BlockPos pos;
    private final ContainerLevelAccess access;
    private final Block block;

    protected ChannelMenu(MenuType<?> type, int id, Inventory inventory, BlockPos pos, Block block) {
        super(type, id);
        this.pos = pos.immutable();
        this.block = block;
        this.access = ContainerLevelAccess.create(inventory.player.level(), pos);
    }

    public boolean isFor(BlockPos pos) {
        return this.pos.equals(pos);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, block);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        return ItemStack.EMPTY;
    }
}
