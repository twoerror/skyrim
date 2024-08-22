package tes.common.item.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.block.other.TESBlockTallGrass;
import tes.common.database.TESBlocks;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class TESItemTallGrass extends TESItemBlockMetadata {
	public TESItemTallGrass(Block block) {
		super(block);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getColorFromItemStack(ItemStack itemstack, int pass) {
		if (pass > 0) {
			return 16777215;
		}
		return super.getColorFromItemStack(itemstack, pass);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamageForRenderPass(int meta, int pass) {
		if (pass > 0) {
			return TESBlocks.tallGrass.getIcon(-1, meta);
		}
		return super.getIconFromDamageForRenderPass(meta, pass);
	}

	@Override
	public int getRenderPasses(int meta) {
		return TESBlockTallGrass.GRASS_OVERLAY[meta] ? 2 : 1;
	}

	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}
}