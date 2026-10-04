//? fabric {
//~ if <26 'FabricTagsProvider'->'FabricTagProvider' {
//~ if <26 '.BlockTagsProvider'->'.BlockTagProvider' {
//~ if <26 'FabricPackOutput'->'FabricDataOutput' {
package cc.cassian.mru.fabric.datagen;

import cc.cassian.mru.util.ItemLikeEntry;
//~ if >=26.2 'cc.cassian.mru.util'->'net.minecraft.references'
import cc.cassian.mru.util.BlockItemId;
import cc.cassian.mru.util.Identifiable;
//~ if >=26.2 'cc.cassian.mru.util'->'net.minecraft.tags'
import cc.cassian.mru.util.BlockItemTagId;
import cc.cassian.mru.util.CommonUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
//? if >=26.2 {
/*import net.minecraft.core.registries.BuiltInRegistries;
*///?} else if >26 {
import net.minecraft.data.tags.TagAppender;
 //?}
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class MultiversionedBlockTagsProvider extends FabricTagsProvider.BlockTagsProvider {

	public MultiversionedBlockTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
		super(output, registryLookupFuture);
	}

	public class MultiversionedBlockTagBuilder {
		//? if >1.21.2 && <26.2 {
		private TagAppender<Block, Block> valueLookupBuilder;
		 //?}
		//? if <1.21.2 {
		/*private FabricTagsProvider<Block>.FabricTagBuilder valueLookupBuilder;
		*///?}

		private TagBuilder rawBuilder;

		public MultiversionedBlockTagBuilder(TagKey<Block> tag) {
			//? if >1.21.2 && <26.2 {
			this.valueLookupBuilder = valueLookupBuilder(tag);
			 //?}
			//? if <1.21.2 {
			/*this.valueLookupBuilder = getOrCreateTagBuilder(tag);
			*///?}

			this.rawBuilder = getOrCreateRawBuilder(tag);
		}

		public MultiversionedBlockTagBuilder add(Block block) {
			//? if >=26.2 {
			/*rawBuilder = rawBuilder.addElement(BuiltInRegistries.BLOCK.getKey(block));
			 *///?} else {
			valueLookupBuilder = valueLookupBuilder.add(block);
			//?}
			return this;
		}

		public MultiversionedBlockTagBuilder add(Supplier<Block> block) {
			add(block.get());
			return this;
		}

		public MultiversionedBlockTagBuilder add(ResourceKey<Block> block) {
			add(block.mru$identifier());
			return this;
		}

		public MultiversionedBlockTagBuilder add(Holder<Block> block) {
			add(block.value());
			return this;
		}

		public MultiversionedBlockTagBuilder add(Identifiable... block) {
			for (Identifiable identifiable : block) {
				if (identifiable instanceof TagKey<?>)
					rawBuilder = rawBuilder.addOptionalTag(identifiable.mru$identifier());
				else rawBuilder = rawBuilder.addElement(identifiable.mru$identifier());
			}
			return this;
		}

		public MultiversionedBlockTagBuilder add(Stream<? extends Identifiable> block) {
			block.forEach(identifiable -> {
				if (identifiable instanceof TagKey<?>)
					rawBuilder = rawBuilder.addOptionalTag(identifiable.mru$identifier());
				else rawBuilder = rawBuilder.addElement(identifiable.mru$identifier());
			});
			return this;
		}

		public MultiversionedBlockTagBuilder addAll(Stream<? extends Identifiable> block) {
			return add(block);
		}

		public MultiversionedBlockTagBuilder addAll(Collection<? extends Identifiable> block) {
			return add(block.stream());
		}

		public MultiversionedBlockTagBuilder add(BlockItemId item) {
			rawBuilder = rawBuilder.addElement(item.block().mru$identifier());
			return this;
		}

		public MultiversionedBlockTagBuilder addTag(TagKey<Block> blockTagKey) {
			rawBuilder = rawBuilder.addTag(blockTagKey.location());
			return this;
		}

		public MultiversionedBlockTagBuilder addTag(BlockItemTagId blockItemTagId) {
			return addTag(blockItemTagId.block());
		}

		public MultiversionedBlockTagBuilder addOptionalTag(TagKey<Block> blockTagKey) {
			rawBuilder = rawBuilder.addOptionalTag(blockTagKey.location());
			return this;
		}

		public MultiversionedBlockTagBuilder addOptionalTag(BlockItemTagId blockItemTagId) {
			return addOptionalTag(blockItemTagId.block());
		}

		public MultiversionedBlockTagBuilder add(Block... blocks) {
			//? if >=26.2 {
			/*for (Block block : blocks) {
				rawBuilder = rawBuilder.addElement(BuiltInRegistries.BLOCK.getKey(block));
			}
			*///?} else {
			valueLookupBuilder = valueLookupBuilder.add(blocks);
			//?}
			return this;
		}

		public MultiversionedBlockTagBuilder addOptional(Identifier item) {
			rawBuilder = rawBuilder.addOptionalElement(item);
			return this;
		}

		public MultiversionedBlockTagBuilder add(Identifier item) {
			rawBuilder = rawBuilder.addElement(item);
			return this;
		}

		public MultiversionedBlockTagBuilder add(String item) {
			rawBuilder = rawBuilder.addElement(CommonUtils.parseId(item));
			return this;
		}
	}

	protected MultiversionedBlockTagBuilder tagBuilder(TagKey<Block> tag) {
		return new MultiversionedBlockTagBuilder(tag);
	}

	protected MultiversionedBlockTagBuilder tagBuilder(BlockItemTagId tag) {
		return new MultiversionedBlockTagBuilder(tag.block());
	}

	public static TagKey<Block> conventionTag(String id) {
		return CommonUtils.blockTag("c", id);
	}
}
//~}
//~}
//~}
//?}