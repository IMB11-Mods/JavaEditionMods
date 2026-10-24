package dev.imb11.snow.mixin.accumulation;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.imb11.snow.accumulation.SnowAccumulation;
import dev.imb11.snow.config.SnowConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @WrapOperation(method = "tickPrecipitation", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;shouldSnow(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z"))
    private boolean snow$accumulate(Biome biome, LevelReader level, BlockPos pos, Operation<Boolean> original) {
        boolean shouldSnow = original.call(biome, level, pos);
        if (!shouldSnow || !SnowConfig.get().accumulation) {
            return shouldSnow;
        }

        SnowAccumulation.accumulate((ServerLevel) (Object) this, pos);
        return false;
    }
}
