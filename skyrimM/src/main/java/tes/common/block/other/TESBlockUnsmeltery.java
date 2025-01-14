package tes.common.block.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.TES;
import tes.common.database.TESGuiId;
import tes.common.tileentity.TESTileEntityUnsmeltery;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class TESBlockUnsmeltery extends TESBlockForgeBase {
	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TESTileEntityUnsmeltery();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int i, int j, int k, int side) {
		return getIcon(side, world.getBlockMetadata(i, j, k));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		return Blocks.cobblestone.getIcon(i, j);
	}

	@Override
	public int getRenderType() {
		return TES.proxy.getUnsmelteryRenderID();
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int side, float f, float f1, float f2) {
		if (!world.isRemote) {
			entityplayer.openGui(TES.instance, TESGuiId.UNSMELTERY.ordinal(), world, i, j, k);
		}
		return true;
	}

	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
		super.randomDisplayTick(world, i, j, k, random);
		if (isForgeActive(world, i, j, k)) {
			for (int l = 0; l < 3; ++l) {
				float f = i + 0.25f + random.nextFloat() * 0.5f;
				float f1 = j + 0.5f + random.nextFloat() * 0.5f;
				float f2 = k + 0.25f + random.nextFloat() * 0.5f;
				world.spawnParticle("largesmoke", f, f1, f2, 0.0, 0.0, 0.0);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconregister) {
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean useLargeSmoke() {
		return false;
	}
}