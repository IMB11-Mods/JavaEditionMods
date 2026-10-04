package dev.imb11.sounds.mixin.ui;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.imb11.sounds.config.SoundsConfig;
import dev.imb11.sounds.config.UISoundsConfig;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = CreativeModeInventoryScreen.class, priority = 900)
public abstract class CreativeInventoryScrollSoundMixin {
    @Shadow
    private float scrollOffs;

    @Unique
    private long sounds$lastScrollSoundTime = System.nanoTime() - 50_000_000L;

    @WrapMethod(method = "mouseScrolled")
    private boolean sounds$mouseScrolled(double x, double y, double scrollX, double scrollY, Operation<Boolean> original) {
        float previousScroll = scrollOffs;
        boolean result = original.call(x, y, scrollX, scrollY);
        sounds$playScrollSound(previousScroll);
        return result;
    }

    @WrapMethod(method = "mouseDragged")
    private boolean sounds$mouseDragged(MouseButtonEvent event, double dx, double dy, Operation<Boolean> original) {
        float previousScroll = scrollOffs;
        boolean result = original.call(event, dx, dy);
        sounds$playScrollSound(previousScroll);
        return result;
    }

    @Unique
    private void sounds$playScrollSound(float previousScroll) {
        if (previousScroll == scrollOffs) {
            return;
        }
        long currentTime = System.nanoTime();
        if (currentTime - sounds$lastScrollSoundTime >= 50_000_000L) {
            SoundsConfig.get(UISoundsConfig.class).inventoryScrollSoundEffect.playSound();
            sounds$lastScrollSoundTime = currentTime;
        }
    }
}
