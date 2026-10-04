package cc.cassian.mru.mixin;

import cc.cassian.mru.util.Identifiable;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SoundEvent.class)
public class SoundEventMixin implements Identifiable {

	@Shadow
	@Final
	private Identifier location;

	@Override
	public Identifier mru$identifier() {
		return location;
	}
}
