package dev.imb11.sounds.mixin.world.mechanics;

import dev.imb11.sounds.config.SoundsConfig;
import dev.imb11.sounds.config.WorldSoundsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
//? if <26.3 {
import org.spongepowered.asm.mixin.Shadow;
//?} else {
/*import net.minecraft.world.item.component.SwingAnimation;
*///?}
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class SwordSwingSoundEffect {
    //? if <26.3 {
    @Shadow
    public int swingTime;
    //?}
    @Unique
    private static long lastPlayed = 0;
    @Unique
    private static final long COOLDOWN = 250;

    //? if <26.3 {
    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At(value = "HEAD"))
    public void $start_sword_swoosh_sound(InteractionHand interactionHand, boolean bl, CallbackInfo ci) {
    //?} else {
    /*@Inject(method = "swingAndResetAttackStrength", at = @At(value = "HEAD"))
    public void $start_sword_swoosh_sound(InteractionHand interactionHand, SwingAnimation animation, boolean sendToSwingingEntity, CallbackInfo ci) {
    *///?}
        long currentTime = System.currentTimeMillis();
        var entity =  (LivingEntity) (Object) this;
        if (!entity.level().isClientSide()) return;
        if (entity instanceof LocalPlayer localPlayer && Minecraft.getInstance().player != null && localPlayer.getUUID().equals(Minecraft.getInstance().player.getUUID())) {
            if (entity.getItemInHand(interactionHand).is(ItemTags.SWORDS) && (currentTime - lastPlayed > COOLDOWN)) {
                SoundsConfig.get(WorldSoundsConfig.class).swordSwooshSoundEffect.playSound();
                lastPlayed = currentTime;
            }
        }
    }

}
