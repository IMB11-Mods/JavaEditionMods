package dev.imb11.snow.gametest;

import dev.imb11.snow.gametest.SnowGameTests.TestDefinition;
import dev.imb11.snow.gametest.support.SnowTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static dev.imb11.snow.gametest.support.SnowTestEnvironments.HARVESTING_OFF;
import static dev.imb11.snow.gametest.support.SnowTestEnvironments.MELTING;
import static dev.imb11.snow.gametest.support.SnowTestHelper.PLATFORM;
import static dev.imb11.snow.gametest.support.SnowTestHelper.SURFACE;

public class HarvestingTests {
    public static List<TestDefinition> all() {
        return List.of(
                new TestDefinition("harvesting/removes_layers", MELTING, PLATFORM, 20, HarvestingTests::removesLayers),
                new TestDefinition("harvesting/blocked_above", MELTING, PLATFORM, 20, HarvestingTests::blockedAbove),
                new TestDefinition("harvesting/stacked_column", MELTING, PLATFORM, 20, HarvestingTests::stackedColumn),
                new TestDefinition("harvesting/requires_shovel", MELTING, PLATFORM, 20, HarvestingTests::requiresShovel),
                new TestDefinition("harvesting/disabled", HARVESTING_OFF, PLATFORM, 20, HarvestingTests::disabled)
        );
    }

    private static void removesLayers(GameTestHelper helper) {
        SnowTestHelper.place(helper, SURFACE, 4);
        ItemStack shovel = use(helper, SURFACE, Items.IRON_SHOVEL);
        helper.assertBlockPresent(Blocks.AIR, SURFACE);
        helper.assertItemEntityPresent(Items.SNOWBALL, SURFACE, 2);
        helper.assertValueEqual(shovel.getDamageValue(), 1, "shovel damage");
        helper.succeed();
    }

    private static void blockedAbove(GameTestHelper helper) {
        SnowTestHelper.place(helper, SURFACE, 4);
        helper.setBlock(SURFACE.above(), Blocks.STONE);
        use(helper, SURFACE, Items.IRON_SHOVEL);
        SnowTestHelper.assertLayers(helper, SURFACE, 4);
        helper.assertItemEntityNotPresent(Items.SNOWBALL, SURFACE, 2);
        helper.succeed();
    }

    private static void stackedColumn(GameTestHelper helper) {
        SnowTestHelper.place(helper, SURFACE, 12);
        use(helper, SURFACE, Items.IRON_SHOVEL);
        SnowTestHelper.assertLayers(helper, SURFACE, 12);
        use(helper, SURFACE.above(), Items.IRON_SHOVEL);
        SnowTestHelper.assertLayers(helper, SURFACE, 8);
        helper.succeed();
    }

    private static void requiresShovel(GameTestHelper helper) {
        SnowTestHelper.place(helper, SURFACE, 4);
        use(helper, SURFACE, Items.STICK);
        SnowTestHelper.assertLayers(helper, SURFACE, 4);
        helper.succeed();
    }

    private static void disabled(GameTestHelper helper) {
        SnowTestHelper.place(helper, SURFACE, 4);
        use(helper, SURFACE, Items.IRON_SHOVEL);
        SnowTestHelper.assertLayers(helper, SURFACE, 4);
        helper.succeed();
    }

    private static ItemStack use(GameTestHelper helper, BlockPos pos, Item item) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack stack = new ItemStack(item);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        BlockPos absolute = helper.absolutePos(pos);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(absolute), Direction.UP, absolute, false);
        stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
        return stack;
    }
}
