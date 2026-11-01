//? fabric {
package dev.imb11.snow.datagen;

import dev.imb11.snow.snowlogging.Snowlogging;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SnowBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
    private static final List<Block> SNOWLOGGABLE = List.of(
            Blocks.SHORT_GRASS,
            Blocks.FERN,
            Blocks.SHORT_DRY_GRASS,
            Blocks.TALL_DRY_GRASS,
            Blocks.BUSH,
            //? if >=26.3 {
            /*Blocks.RED_SHRUB,
            *///?}
            Blocks.FIREFLY_BUSH,
            Blocks.NETHER_SPROUTS,
            Blocks.CRIMSON_ROOTS,
            Blocks.WARPED_ROOTS,
            Blocks.DANDELION,
            Blocks.GOLDEN_DANDELION,
            Blocks.POPPY,
            Blocks.BLUE_ORCHID,
            Blocks.ALLIUM,
            Blocks.AZURE_BLUET,
            Blocks.RED_TULIP,
            Blocks.ORANGE_TULIP,
            Blocks.WHITE_TULIP,
            Blocks.PINK_TULIP,
            Blocks.OXEYE_DAISY,
            Blocks.CORNFLOWER,
            Blocks.LILY_OF_THE_VALLEY,
            Blocks.WITHER_ROSE,
            Blocks.TORCHFLOWER,
            Blocks.OPEN_EYEBLOSSOM,
            Blocks.CLOSED_EYEBLOSSOM,
            Blocks.BROWN_MUSHROOM,
            Blocks.RED_MUSHROOM,
            Blocks.CRIMSON_FUNGUS,
            Blocks.WARPED_FUNGUS
    );

    public SnowBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        this.builder(Snowlogging.SNOWLOGGABLE)
                .addAll(SNOWLOGGABLE.stream().map(block -> block.builtInRegistryHolder().key()));
    }
}
//?}
