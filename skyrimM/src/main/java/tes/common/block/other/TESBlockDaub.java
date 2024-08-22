package tes.common.block.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.client.render.other.TESConnectedTextures;
import tes.common.block.TESConnectedBlock;
import tes.common.database.TESCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class TESBlockDaub extends Block implements TESConnectedBlock {
	public TESBlockDaub() {
		super(Material.grass);
		setCreativeTab(TESCreativeTabs.TAB_BLOCK);
		setHardness(1.0f);
		setStepSound(soundTypeGrass);
	}

	@Override
	public boolean areBlocksConnected(IBlockAccess world, int i, int j, int k, int i1, int j1, int k1) {
		int meta = world.getBlockMetadata(i, j, k);
		Block otherBlock = world.getBlock(i1, j1, k1);
		int otherMeta = world.getBlockMetadata(i1, j1, k1);
		return otherBlock == this && otherMeta == meta;
	}

	@Override
	public String getConnectedName(int meta) {
		return textureName;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int i, int j, int k, int side) {
		return TESConnectedTextures.getConnectedIconBlock(this, world, i, j, k, side, false);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		return TESConnectedTextures.getConnectedIconItem(this, j);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconregister) {
		TESConnectedTextures.registerConnectedIcons(iconregister, this, 0, false);
	}
}