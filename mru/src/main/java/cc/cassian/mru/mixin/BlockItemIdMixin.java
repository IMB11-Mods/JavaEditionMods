package cc.cassian.mru.mixin;

import cc.cassian.mru.util.Identifiable;
//~ if >=26.2 'cc.cassian.mru.util'->'net.minecraft.references'
import cc.cassian.mru.util.BlockItemId;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockItemId.class)
public abstract class BlockItemIdMixin implements Identifiable {

	//? if >26.1 {
	/*@Shadow
	public abstract ResourceKey<Block> block();

	@Override
	public Identifier mru$identifier() {
		return block().identifier();
	}
	*///?}
}
