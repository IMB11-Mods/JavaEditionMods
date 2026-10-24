package dev.imb11.snow.harvesting;

import dev.imb11.snow.config.SnowConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

public class SnowHarvesting {
    public static @Nullable InteractionResult useOn(UseOnContext context) {
        if (!SnowConfig.get().harvesting.shovelHarvesting || context.getClickedFace() == Direction.DOWN) {
            return null;
        }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!state.is(Blocks.SNOW) || !level.getBlockState(pos.above()).isAir()) {
            return null;
        }

        if (level instanceof ServerLevel serverLevel) {
            Player player = context.getPlayer();
            ItemStack tool = context.getItemInHand();
            Block.dropResources(state, serverLevel, pos, null, player, tool);
            level.removeBlock(pos, false);
            level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
            level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(player, state));
            if (player != null) {
                tool.hurtAndBreak(1, player, context.getHand().asEquipmentSlot());
            }
        }

        return InteractionResult.SUCCESS;
    }
}
