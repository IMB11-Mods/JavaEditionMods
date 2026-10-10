package dev.imb11.items;

import dev.imb11.Glass;
import dev.imb11.blocks.GBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class GItems {
    public static final BlockItem TERMINAL = new BlockItem(GBlocks.TERMINAL, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Glass.MOD_ID, "terminal"))).useBlockDescriptionPrefix());
    public static final BlockItem PROJECTOR = new BlockItem(GBlocks.PROJECTOR, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Glass.MOD_ID, "projector"))).useBlockDescriptionPrefix());
    public static final BlockItem REDSTONE_INFUSED_SAND = new BlockItem(GBlocks.REDSTONE_INFUSED_SAND, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Glass.MOD_ID, "redstone_infused_sand"))).useBlockDescriptionPrefix());
    public static final BlockItem POWERABLE_GLASS = new BlockItem(GBlocks.POWERABLE_GLASS, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Glass.MOD_ID, "powerable_glass"))).useBlockDescriptionPrefix());

    public static void init() {
        register("terminal", TERMINAL);
        register("projector", PROJECTOR);
        register("redstone_infused_sand", REDSTONE_INFUSED_SAND);
        register("powerable_glass", POWERABLE_GLASS);
    }

    private static <T extends Item> T register(String id, T item) {
        return Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Glass.MOD_ID, id), item);
    }
}
