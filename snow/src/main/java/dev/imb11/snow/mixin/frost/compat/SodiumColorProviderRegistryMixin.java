package dev.imb11.snow.mixin.frost.compat;

import dev.imb11.snow.client.frost.compat.SodiumFrostColorProvider;
import dev.imb11.snow.frost.FrostableLeaves;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import net.caffeinemc.mods.sodium.client.model.color.ColorProvider;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.model.color.ColorProviderRegistry", remap = false)
abstract class SodiumColorProviderRegistryMixin {
    @Shadow
    @Final
    private Reference2ReferenceMap<Block, ColorProvider<BlockState>> blocks;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void snow$frostLeaves(BlockColors colors, CallbackInfo ci) {
        for (Block block : BuiltInRegistries.BLOCK) {
            ColorProvider<BlockState> provider = this.blocks.get(block);
            if (provider != null && FrostableLeaves.contains(block.defaultBlockState())) {
                this.blocks.put(block, new SodiumFrostColorProvider(provider));
            }
        }
    }
}
