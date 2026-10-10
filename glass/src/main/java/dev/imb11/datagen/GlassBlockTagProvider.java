//? fabric {
package dev.imb11.datagen;

import dev.imb11.blocks.GBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

public class GlassBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
    public GlassBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        builder(GBlocks.GLASS_BLOCKS).add(GBlocks.POWERABLE_GLASS.builtInRegistryHolder().key());
        builder(BlockTags.MINEABLE_WITH_SHOVEL).add(GBlocks.REDSTONE_INFUSED_SAND.builtInRegistryHolder().key());
        builder(BlockTags.MINEABLE_WITH_PICKAXE).add(GBlocks.PROJECTOR.builtInRegistryHolder().key(), GBlocks.TERMINAL.builtInRegistryHolder().key());
        builder(BlockTags.NEEDS_DIAMOND_TOOL).add(GBlocks.TERMINAL.builtInRegistryHolder().key());
    }
}
//?}
