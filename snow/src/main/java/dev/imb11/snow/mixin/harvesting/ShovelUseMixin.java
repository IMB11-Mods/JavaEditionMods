package dev.imb11.snow.mixin.harvesting;

import dev.imb11.snow.harvesting.SnowHarvesting;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if >=26.3 {
/*import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
*///?} else {
import net.minecraft.world.item.ShovelItem;
//?}

//? if >=26.3 {
/*@Mixin(Item.class)
*///?} else {
@Mixin(ShovelItem.class)
//?}
public class ShovelUseMixin {
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void snow$harvest(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        //? if >=26.3 {
        /*if (!context.getItemInHand().is(ItemTags.SHOVELS)) {
            return;
        }
        *///?}
        InteractionResult result = SnowHarvesting.useOn(context);
        if (result != null) {
            cir.setReturnValue(result);
        }
    }
}
