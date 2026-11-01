package dev.imb11.snow.client.frost.compat;

import dev.imb11.snow.client.frost.FrostBackend;
import dev.imb11.snow.client.frost.LeafFrost;
import net.caffeinemc.mods.sodium.client.model.color.ColorProvider;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public record SodiumFrostColorProvider(ColorProvider<BlockState> delegate) implements ColorProvider<BlockState> {
    @Override
    public void getColors(LevelSlice slice, BlockPos pos, BlockPos.MutableBlockPos scratch, BlockState state, ModelQuadView quad, int[] colors, boolean blend) {
        this.delegate.getColors(slice, pos, scratch, state, quad, colors, blend);
        float weight = LeafFrost.weight(slice, state, pos);
        if (weight <= 0.0F) {
            return;
        }

        for (int i = 0; i < colors.length; i++) {
            colors[i] = FrostBackend.INSTANCE.meshColor(colors[i], weight, pos);
        }
    }
}
