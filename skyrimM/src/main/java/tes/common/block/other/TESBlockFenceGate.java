package tes.common.block.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.util.IIcon;

public class TESBlockFenceGate extends BlockFenceGate {
	private final Block plankBlock;
	private final int plankMeta;

	public TESBlockFenceGate(Block block, int meta) {
		plankBlock = block;
		plankMeta = meta;
		setCreativeTab(TESCreativeTabs.TAB_UTIL);
		setHardness(2.0f);
		setResistance(5.0f);
		setStepSound(soundTypeWood);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		return plankBlock.getIcon(i, plankMeta);
	}
}