//? fabric {
package dev.imb11.datagen;

import dev.imb11.blocks.GBlocks;
import dev.imb11.blocks.ProjectorBlock;
import dev.imb11.blocks.TerminalBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.minecraft.core.Direction;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.util.random.WeightedList;
import com.mojang.math.Quadrant;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class GlassModelProvider extends FabricModelProvider {
    public GlassModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        createCube(generator, GBlocks.REDSTONE_INFUSED_SAND, GBlocks.REDSTONE_INFUSED_SAND);
        createCube(generator, GBlocks.POWERABLE_GLASS, GBlocks.POWERABLE_GLASS);

        Identifier terminalModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath("glass", "block/terminal_camera")), Optional.empty())
                .create(GBlocks.TERMINAL, new TextureMapping(), generator.modelOutput);
        Identifier projectingTerminalModel = new ModelTemplate(Optional.of(terminalModel), Optional.of("_projecting"))
                .create(GBlocks.TERMINAL, new TextureMapping(), generator.modelOutput);
        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(GBlocks.TERMINAL)
                .with(PropertyDispatch.initial(TerminalBlock.PROJECTING)
                        .select(false, plain(terminalModel))
                        .select(true, plain(projectingTerminalModel)))
                .with(PropertyDispatch.modify(TerminalBlock.FACING)
                        .select(Direction.SOUTH, BlockModelGenerators.NOP)
                        .select(Direction.NORTH, VariantMutator.Y_ROT.withValue(Quadrant.R180))
                        .select(Direction.WEST, VariantMutator.Y_ROT.withValue(Quadrant.R90))
                        .select(Direction.EAST, VariantMutator.Y_ROT.withValue(Quadrant.R270))
                        .select(Direction.UP, VariantMutator.X_ROT.withValue(Quadrant.R90))
                        .select(Direction.DOWN, VariantMutator.X_ROT.withValue(Quadrant.R270).then(VariantMutator.Y_ROT.withValue(Quadrant.R180)))));

        Identifier projectorModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath("glass", "block/projector_assembly")), Optional.empty())
                .create(GBlocks.PROJECTOR, new TextureMapping(), generator.modelOutput);
        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(GBlocks.PROJECTOR)
                .with(PropertyDispatch.initial(ProjectorBlock.POWERED)
                        .select(false, plain(projectorModel))
                        .select(true, plain(projectorModel)))
                .with(PropertyDispatch.modify(ProjectorBlock.FACING)
                        .select(Direction.UP, BlockModelGenerators.NOP)
                        .select(Direction.DOWN, VariantMutator.X_ROT.withValue(Quadrant.R180))
                        .select(Direction.SOUTH, VariantMutator.X_ROT.withValue(Quadrant.R90))
                        .select(Direction.WEST, VariantMutator.X_ROT.withValue(Quadrant.R90).then(VariantMutator.Y_ROT.withValue(Quadrant.R90)))
                        .select(Direction.NORTH, VariantMutator.X_ROT.withValue(Quadrant.R90).then(VariantMutator.Y_ROT.withValue(Quadrant.R180)))
                        .select(Direction.EAST, VariantMutator.X_ROT.withValue(Quadrant.R90).then(VariantMutator.Y_ROT.withValue(Quadrant.R270)))));
        generator.registerSimpleItemModel(GBlocks.PROJECTOR, Identifier.fromNamespaceAndPath("glass", "item/projector"));
        generator.registerSimpleItemModel(GBlocks.TERMINAL, terminalModel);
    }

    @Override
    public void generateItemModels(ItemModelGenerators generator) {
    }

    private static void createCube(BlockModelGenerators generator, Block block, Block texture) {
        Identifier model = ModelTemplates.CUBE_ALL.create(block, TextureMapping.cube(texture), generator.modelOutput);
        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, plain(model)));
        generator.registerSimpleItemModel(block, model);
    }
    private static MultiVariant plain(Identifier model) {
        return new MultiVariant(WeightedList.of(new Variant(model)));
    }
}
//?}
