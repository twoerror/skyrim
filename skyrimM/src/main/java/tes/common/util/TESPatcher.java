package tes.common.util;

import net.minecraft.block.Block;
import net.minecraftforge.common.IShearable;

import java.util.Set;

public class TESPatcher {
	private TESPatcher() {
	}

	public static void onInit() {
		if (TESModChecker.hasLOTR()) {
			try {
				Class<?> registry = Class.forName("lotr.common.LOTRMod");
				Set<Block> blocks = TESAPI.getObjectFieldsOfType(registry, Block.class);
				for (Block block : blocks) {
					if (block instanceof IShearable) {
						block.setCreativeTab(null);
					}
				}
			} catch (Exception ignored) {
			}
		}
	}
}