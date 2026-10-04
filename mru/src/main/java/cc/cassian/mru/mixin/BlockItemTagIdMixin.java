package cc.cassian.mru.mixin;

import cc.cassian.mru.util.Identifiable;
//~ if >=26.2 'cc.cassian.mru.util'->'net.minecraft.tags'
import cc.cassian.mru.util.BlockItemTagId;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockItemTagId.class)
public abstract class BlockItemTagIdMixin implements Identifiable {

	//? if >26.1 {
	/*@Shadow
	public abstract TagKey<Block> block();

	@Override
	public Identifier mru$identifier() {
		return block().location();
	}
	*///?}
}
