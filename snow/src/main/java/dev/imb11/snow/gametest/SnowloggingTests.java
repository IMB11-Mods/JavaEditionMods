package dev.imb11.snow.gametest;

import dev.imb11.snow.accumulation.SnowAccumulation;
import dev.imb11.snow.gametest.SnowGameTests.TestDefinition;
import dev.imb11.snow.gametest.support.SnowTestHelper;
import dev.imb11.snow.snowlogging.SnowloggedChunkData;
import dev.imb11.snow.snowlogging.Snowlogging;
import dev.imb11.snow.snowlogging.SnowloggingStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static dev.imb11.snow.gametest.support.SnowTestEnvironments.MELTING;
import static dev.imb11.snow.gametest.support.SnowTestEnvironments.SNOWLOGGING_OFF;
import static dev.imb11.snow.gametest.support.SnowTestHelper.PLATFORM;
import static dev.imb11.snow.gametest.support.SnowTestHelper.SURFACE;

public class SnowloggingTests {
    public static List<TestDefinition> all() {
        return List.of(
                new TestDefinition("snowlogging/place_on_flower", MELTING, PLATFORM, 20, SnowloggingTests::placeOnFlower),
                new TestDefinition("snowlogging/place_on_grass", MELTING, PLATFORM, 20, SnowloggingTests::placeOnGrass),
                new TestDefinition("snowlogging/adds_layers", MELTING, PLATFORM, 20, SnowloggingTests::addsLayers),
                new TestDefinition("snowlogging/plant_into_snow", MELTING, PLATFORM, 20, SnowloggingTests::plantIntoSnow),
                new TestDefinition("snowlogging/accumulation", MELTING, PLATFORM, 20, SnowloggingTests::accumulation),
                new TestDefinition("snowlogging/break_restores_plant", MELTING, PLATFORM, 20, SnowloggingTests::breakRestoresPlant),
                new TestDefinition("snowlogging/destroy_restores_plant", MELTING, PLATFORM, 20, SnowloggingTests::destroyRestoresPlant),
                new TestDefinition("snowlogging/shovel_restores_plant", MELTING, PLATFORM, 20, SnowloggingTests::shovelRestoresPlant),
                new TestDefinition("snowlogging/replaced_drops_plant", MELTING, PLATFORM, 20, SnowloggingTests::replacedDropsPlant),
                new TestDefinition("snowlogging/command_clears_plant", MELTING, PLATFORM, 20, SnowloggingTests::commandClearsPlant),
                new TestDefinition("snowlogging/saves_plant", MELTING, PLATFORM, 20, SnowloggingTests::savesPlant),
                new TestDefinition("snowlogging/disabled", SNOWLOGGING_OFF, PLATFORM, 20, SnowloggingTests::disabled)
        );
    }

    private static void placeOnFlower(GameTestHelper helper) {
        plant(helper, Blocks.POPPY);
        use(helper, Items.SNOW);
        SnowTestHelper.assertLayers(helper, SURFACE, 1);
        assertPlant(helper, Blocks.POPPY);
        helper.succeed();
    }

    private static void placeOnGrass(GameTestHelper helper) {
        plant(helper, Blocks.SHORT_GRASS);
        use(helper, Items.SNOW);
        SnowTestHelper.assertLayers(helper, SURFACE, 1);
        assertPlant(helper, Blocks.SHORT_GRASS);
        helper.succeed();
    }

    private static void addsLayers(GameTestHelper helper) {
        plant(helper, Blocks.POPPY);
        use(helper, Items.SNOW);
        use(helper, Items.SNOW);
        SnowTestHelper.assertLayers(helper, SURFACE, 2);
        assertPlant(helper, Blocks.POPPY);
        helper.succeed();
    }

    private static void plantIntoSnow(GameTestHelper helper) {
        helper.setBlock(SURFACE.below(), Blocks.GRASS_BLOCK);
        SnowTestHelper.place(helper, SURFACE, 3);
        use(helper, Items.POPPY);
        SnowTestHelper.assertLayers(helper, SURFACE, 3);
        assertPlant(helper, Blocks.POPPY);
        helper.succeed();
    }

    private static void accumulation(GameTestHelper helper) {
        plant(helper, Blocks.POPPY);
        SnowAccumulation.accumulate(helper.getLevel(), helper.absolutePos(SURFACE));
        SnowTestHelper.assertLayers(helper, SURFACE, 1);
        assertPlant(helper, Blocks.POPPY);
        helper.succeed();
    }

    private static void breakRestoresPlant(GameTestHelper helper) {
        snowlog(helper, Blocks.POPPY, 3);
        helper.getLevel().removeBlock(helper.absolutePos(SURFACE), false);
        helper.assertBlockPresent(Blocks.POPPY, SURFACE);
        assertPlant(helper, null);
        helper.succeed();
    }

    private static void destroyRestoresPlant(GameTestHelper helper) {
        snowlog(helper, Blocks.POPPY, 3);
        helper.getLevel().destroyBlock(helper.absolutePos(SURFACE), true);
        helper.assertBlockPresent(Blocks.POPPY, SURFACE);
        helper.assertItemEntityNotPresent(Items.POPPY, SURFACE, 2);
        helper.succeed();
    }

    private static void shovelRestoresPlant(GameTestHelper helper) {
        snowlog(helper, Blocks.POPPY, 4);
        use(helper, Items.IRON_SHOVEL);
        helper.assertBlockPresent(Blocks.POPPY, SURFACE);
        helper.assertItemEntityPresent(Items.SNOWBALL, SURFACE, 2);
        helper.succeed();
    }

    private static void replacedDropsPlant(GameTestHelper helper) {
        snowlog(helper, Blocks.POPPY, 1);
        helper.setBlock(SURFACE, Blocks.STONE);
        helper.assertBlockPresent(Blocks.STONE, SURFACE);
        helper.assertItemEntityPresent(Items.POPPY, SURFACE, 2);
        assertPlant(helper, null);
        helper.succeed();
    }

    private static void commandClearsPlant(GameTestHelper helper) {
        snowlog(helper, Blocks.POPPY, 1);
        helper.getLevel().setBlock(helper.absolutePos(SURFACE), Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
        helper.assertBlockPresent(Blocks.AIR, SURFACE);
        helper.assertItemEntityNotPresent(Items.POPPY, SURFACE, 2);
        assertPlant(helper, null);
        helper.succeed();
    }

    private static void savesPlant(GameTestHelper helper) {
        snowlog(helper, Blocks.POPPY, 5);
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(SURFACE);
        SerializableChunkData data = SerializableChunkData.copyOf(level, level.getChunkAt(pos));
        helper.assertValueEqual(sectionState(data, pos), Blocks.POPPY.defaultBlockState(), "saved block");
        SnowTestHelper.assertLayers(helper, SURFACE, 5);

        CompoundTag tag = data.write();
        helper.assertTrue(tag.contains(SnowloggingStorage.TAG), "Saved chunk should store snow layers");
        SerializableChunkData loaded = SerializableChunkData.parse(level, level.palettedContainerFactory(), tag);
        helper.assertTrue(loaded != null, "Saved chunk should parse");
        SnowloggingStorage.restore(loaded, ((SnowloggedChunkData) (Object) loaded).snow$getLayers());
        helper.assertValueEqual(sectionState(loaded, pos), Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 5), "loaded block");
        helper.succeed();
    }

    private static void disabled(GameTestHelper helper) {
        plant(helper, Blocks.SHORT_GRASS);
        use(helper, Items.SNOW);
        SnowTestHelper.assertLayers(helper, SURFACE, 1);
        assertPlant(helper, null);
        helper.getLevel().removeBlock(helper.absolutePos(SURFACE), false);
        helper.assertBlockPresent(Blocks.AIR, SURFACE);
        helper.succeed();
    }

    private static void plant(GameTestHelper helper, Block plant) {
        helper.setBlock(SURFACE.below(), Blocks.GRASS_BLOCK);
        helper.setBlock(SURFACE, plant);
    }

    private static void snowlog(GameTestHelper helper, Block plant, int layers) {
        plant(helper, plant);
        SnowTestHelper.place(helper, SURFACE, layers);
        assertPlant(helper, plant);
    }

    private static void assertPlant(GameTestHelper helper, @Nullable Block expected) {
        BlockState plant = Snowlogging.plantAt(helper.getLevel(), helper.absolutePos(SURFACE));
        Block actual = plant == null ? null : plant.getBlock();
        helper.assertTrue(Objects.equals(actual, expected), "Expected snowlogged plant " + expected + " but found " + actual);
    }

    private static BlockState sectionState(SerializableChunkData data, BlockPos pos) {
        LevelChunkSection section = data.sectionData().stream()
                .filter(sectionData -> sectionData.y() == SectionPos.blockToSectionCoord(pos.getY()))
                .findFirst()
                .orElseThrow()
                .chunkSection();
        return section.getBlockState(SectionPos.sectionRelative(pos.getX()), SectionPos.sectionRelative(pos.getY()), SectionPos.sectionRelative(pos.getZ()));
    }

    private static void use(GameTestHelper helper, Item item) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack stack = new ItemStack(item);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        BlockPos absolute = helper.absolutePos(SURFACE);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(absolute), Direction.UP, absolute, false);
        stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
    }
}
