package tes.common.block.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.TES;
import tes.common.TESSquadrons;
import tes.common.database.TESCreativeTabs;
import tes.common.database.TESGuiId;
import tes.common.tileentity.TESTileEntityCommandTable;
import tes.common.world.map.TESConquestGrid;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class TESBlockCommandTable extends BlockContainer {
	@SideOnly(Side.CLIENT)
	private IIcon topIcon;

	@SideOnly(Side.CLIENT)
	private IIcon sideIcon;

	public TESBlockCommandTable() {
		super(Material.iron);
		setCreativeTab(TESCreativeTabs.TAB_UTIL);
		setHardness(2.5f);
		setStepSound(soundTypeMetal);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TESTileEntityCommandTable();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		if (i == 1 || i == 0) {
			return topIcon;
		}
		return sideIcon;
	}

	@Override
	public int getRenderType() {
		return TES.proxy.getCommandTableRenderID();
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int side, float f, float f1, float f2) {
		TESTileEntityCommandTable table;
		if (entityplayer.isSneaking() && (table = (TESTileEntityCommandTable) world.getTileEntity(i, j, k)) != null) {
			if (!world.isRemote) {
				table.toggleZoomExp();
			}
			return true;
		}
		ItemStack itemstack = entityplayer.getCurrentEquippedItem();
		if (itemstack != null && itemstack.getItem() instanceof TESSquadrons.SquadronItem) {
			entityplayer.openGui(TES.instance, TESGuiId.SQUADRON_ITEM.ordinal(), world, 0, 0, 0);
			if (!world.isRemote) {
				world.playSoundEffect(i + 0.5, j + 0.5, k + 0.5, stepSound.getBreakSound(), (stepSound.getVolume() + 1.0f) / 2.0f, stepSound.getPitch() * 0.5f);
			}
			return true;
		}
		if (TESConquestGrid.conquestEnabled(world)) {
			entityplayer.openGui(TES.instance, TESGuiId.CONQUEST.ordinal(), world, 0, 0, 0);
			if (!world.isRemote) {
				world.playSoundEffect(i + 0.5, j + 0.5, k + 0.5, stepSound.getBreakSound(), (stepSound.getVolume() + 1.0f) / 2.0f, stepSound.getPitch() * 0.5f);
			}
			return true;
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconregister) {
		sideIcon = iconregister.registerIcon(getTextureName() + "_side");
		topIcon = iconregister.registerIcon(getTextureName() + "_top");
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
}