package dev.imb11.shields.items;

import cc.cassian.mru.tags.CommonItemTags;
import dev.imb11.shields.Shields;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ShieldsItemTags {
    /// Shields from this mod.
    public static TagKey<Item> SHIELDS = TagKey.create(Registries.ITEM, Shields.of("shields"));
    /// Shields from any mod.
    public static TagKey<Item> CONVENTIONAL_SHIELDS = CommonItemTags.SHIELD_TOOLS;

}
