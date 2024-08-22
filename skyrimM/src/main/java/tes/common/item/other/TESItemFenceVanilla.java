package tes.common.item.other;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class TESItemFenceVanilla extends TESItemBlockMetadata {
	public TESItemFenceVanilla(Block block) {
		super(block);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return "tile.tes.fence_vanilla." + itemstack.getItemDamage();
	}
}