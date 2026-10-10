package dev.imb11.platform;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

@FunctionalInterface
public interface MenuFactory<T extends AbstractContainerMenu, D> {
    T create(int id, Inventory inventory, D data);
}
