package dev.imb11.snow.mixin.snowlogging;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.imb11.snow.snowlogging.Snowlogging;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @Inject(method = "placeBlock", at = @At("HEAD"), cancellable = true)
    private void snow$placeIntoSnow(BlockPlaceContext context, BlockState placementState, CallbackInfoReturnable<Boolean> cir) {
        if (Snowlogging.placeInto(context, placementState)) {
            cir.setReturnValue(true);
        }
    }

    @WrapOperation(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private void snow$playPlantSound(Level level, Entity except, BlockPos pos, SoundEvent sound, SoundSource source, float volume, float pitch, Operation<Void> original, @Local(ordinal = 0) BlockState placementState, @Local(ordinal = 1) BlockState placedState) {
        if (placedState.is(Blocks.SNOW) && placementState.is(Snowlogging.SNOWLOGGABLE)) {
            SoundType soundType = placementState.getSoundType();
            original.call(level, except, pos, soundType.getPlaceSound(), source, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
            return;
        }

        original.call(level, except, pos, sound, source, volume, pitch);
    }
}
