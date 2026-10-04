package dev.imb11.sounds.mixin.ui;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.imb11.sounds.config.SoundsConfig;
import dev.imb11.sounds.config.UISoundsConfig;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EditBox.class)
public abstract class EditBoxSoundEffects {
    @Shadow
    public abstract String getValue();

    @WrapMethod(method = "keyPressed")
    private boolean sounds$keyPressed(KeyEvent event, Operation<Boolean> original) {
        String previousValue = getValue();
        boolean result = original.call(event);
        sounds$playTypingSound(previousValue);
        return result;
    }

    @WrapMethod(method = "charTyped")
    private boolean sounds$charTyped(CharacterEvent event, Operation<Boolean> original) {
        String previousValue = getValue();
        boolean result = original.call(event);
        sounds$playTypingSound(previousValue);
        return result;
    }

    @Unique
    private void sounds$playTypingSound(String previousValue) {
        if (!previousValue.equals(getValue())) {
            SoundsConfig.get(UISoundsConfig.class).typingSoundEffect.playSound();
        }
    }
}
