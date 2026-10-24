package dev.imb11.snow.mixin.generation;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.imb11.snow.config.SnowConfig;
import dev.imb11.snow.generation.SnowGeneration;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.SnowAndFreezeFeature;
//? if <26.3 {
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SnowAndFreezeFeature.class)
public class SnowAndFreezeFeatureMixin {
    @WrapOperation(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    //? if >=26.3 {
    /*private boolean snow$placeLayers(WorldGenLevel level, BlockPos pos, BlockState state, int flags, Operation<Boolean> original, @Local(argsOnly = true) RandomSource random) {
    *///?} else {
    private boolean snow$placeLayers(WorldGenLevel level, BlockPos pos, BlockState state, int flags, Operation<Boolean> original, @Local(argsOnly = true) FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource random = context.random();
    //?}
        if (state.is(Blocks.SNOW) && SnowConfig.get().generation && SnowGeneration.place(level, pos, random, flags)) {
            return true;
        }

        return original.call(level, pos, state, flags);
    }
}
