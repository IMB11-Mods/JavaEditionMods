package dev.imb11.snow.mixin.snowlogging.client;

import dev.imb11.snow.client.SnowloggedTintSource;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BlockColors.class)
public class BlockColorsMixin {
    @Inject(method = "createDefault", at = @At("RETURN"))
    private static void snow$tintSnowloggedPlants(CallbackInfoReturnable<BlockColors> cir) {
        cir.getReturnValue().register(List.of(new SnowloggedTintSource()), Blocks.SNOW);
    }
}
