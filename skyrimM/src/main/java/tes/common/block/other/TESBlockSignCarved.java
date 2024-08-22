package tes.common.block.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESBlocks;
import tes.common.database.TESItems;
import tes.common.tileentity.TESTileEntitySignCarved;
import tes.common.util.TESCommonIcons;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSign;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class TESBlockSignCarved extends BlockSign {
	public TESBlockSignCarved(Class<? extends TESTileEntitySignCarved> cls) {
		super(cls, false);
		setStepSound(soundTypeStone);
		setHardness(0.5f);
	}

	public static IIcon getOnBlockIcon(IBlockAccess world, int i, int j, int k, int side) {
		int onX = i - Facing.offsetsXForSide[side];
		int onY = j - Facing.offsetsYForSide[side];
		int onZ = k - Facing.offsetsZForSide[side];
		Block onBlock = world.getBlock(onX, onY, onZ);
		IIcon icon = onBlock.getIcon(world, onX, onY, onZ, side);
		if (icon == null) {
			return Blocks.stone.getIcon(0, 0);
		}
		return icon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		return TESCommonIcons.iconEmptyBlock;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Item getItem(World world, int i, int j, int k) {
		if (this == TESBlocks.signCarvedGlowing) {
			return TESItems.chisel;
		}
		return TESItems.chisel;
	}

	@Override
	public Item getItemDropped(int i, Random random, int j) {
		return null;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int i, int j, int k) {
		super.setBlockBoundsBasedOnState(world, i, j, k);
		setBlockBounds((float) minX, 0.0f, (float) minZ, (float) maxX, 1.0f, (float) maxZ);
	}
}