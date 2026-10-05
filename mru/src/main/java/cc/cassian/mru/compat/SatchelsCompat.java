package cc.cassian.mru.compat;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.ItemStack;
import net.rose.satchels.common.init.ModDataComponents;

import java.util.stream.Stream;

public class SatchelsCompat {
	public static Stream<ItemStack> getContents(DataComponentMap components) {
		if (isSatchel(components)) {
			return components.get(ModDataComponents.SATCHEL_CONTENTS).stacks().stream();
		}
		return Stream.empty();
	}

	public static boolean isSatchel(DataComponentMap stack) {
		return stack.has(ModDataComponents.SATCHEL_CONTENTS);
	}
}
