package dev.imb11.snow.mixin.frost.client;

import dev.imb11.snow.client.frost.FrostedLeafTintSource;
import dev.imb11.snow.client.frost.FrostedLeaves;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(BlockColors.class)
public class BlockColorsMixin {
    @Unique
    private final Map<List<BlockTintSource>, List<BlockTintSource>> snow$frostedSources = new ConcurrentHashMap<>();

    @Inject(method = "getTintSources", at = @At("RETURN"), cancellable = true)
    private void snow$frostLeaves(BlockState state, CallbackInfoReturnable<List<BlockTintSource>> cir) {
        List<BlockTintSource> sources = cir.getReturnValue();
        if (!sources.isEmpty() && FrostedLeaves.LEAVES.contains(state.getBlock())) {
            cir.setReturnValue(this.snow$frostedSources.computeIfAbsent(sources, original -> {
                List<BlockTintSource> frosted = new ArrayList<>(original);
                frosted.set(0, new FrostedLeafTintSource(original.getFirst()));
                return List.copyOf(frosted);
            }));
        }
    }
}
