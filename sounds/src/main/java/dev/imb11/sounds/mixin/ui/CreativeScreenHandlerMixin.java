package dev.imb11.sounds.mixin.ui;

import dev.imb11.sounds.config.SoundsConfig;
import dev.imb11.sounds.config.UISoundsConfig;
import dev.imb11.sounds.dynamic.DynamicSoundHelper;
import dev.imb11.sounds.sound.context.ItemStackSoundContext;
import dev.imb11.sounds.util.MixinStatics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.ItemPickerMenu.class)
public abstract class CreativeScreenHandlerMixin {

    @Shadow
    public abstract ItemStack getCarried();

    @Inject(method = "setCarried", at = @At("HEAD"), cancellable = false)
    public void $item_delete_sound_effect(ItemStack stack, CallbackInfo ci) {
        if (MixinStatics.CURRENT_SLOT == MixinStatics.DELETE_ITEM_SLOT && !getCarried().isEmpty())
            SoundsConfig.get(UISoundsConfig.class).itemDeleteSoundEffect.playDynamicSound(getCarried(), ItemStackSoundContext.of(DynamicSoundHelper.BlockSoundType.HIT));
    }
}
