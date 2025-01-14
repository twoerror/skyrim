package tes.common.block.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.TES;
import tes.common.database.TESCreativeTabs;
import tes.common.database.TESGuiId;
import tes.common.item.other.TESItemBarrel;
import tes.common.item.other.TESItemBottlePoison;
import tes.common.item.other.TESItemMug;
import tes.common.recipe.TESRecipeBrewing;
import tes.common.tileentity.TESTileEntityBarrel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.util.Random;

public class TESBlockBarrel extends BlockContainer {
	@SideOnly(Side.CLIENT)
	private IIcon[] barrelIcons;

	public TESBlockBarrel() {
		super(Material.wood);
		setCreativeTab(TESCreativeTabs.TAB_FOOD);
		setBlockBounds(0.125f, 0.0f, 0.125f, 0.875f, 0.8125f, 0.875f);
		setHardness(3.0f);
		setResistance(5.0f);
		setStepSound(soundTypeWood);
	}

	@Override
	public void breakBlock(World world, int i, int j, int k, Block block, int meta) {
		IInventory barrel = (IInventory) world.getTileEntity(i, j, k);
		if (barrel != null) {
			ItemStack brewing = barrel.getStackInSlot(9);
			barrel.setInventorySlotContents(9, null);
			TES.dropContainerItems(barrel, world, i, j, k);
			for (int slot = 0; slot < barrel.getSizeInventory(); ++slot) {
				barrel.setInventorySlotContents(slot, null);
			}
			barrel.setInventorySlotContents(9, brewing);
			if (!world.isRemote && (meta & 8) == 0) {
				dropBlockAsItem(world, i, j, k, getBarrelDrop(world, i, j, k));
			}
		}
		super.breakBlock(world, i, j, k, block, meta);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TESTileEntityBarrel();
	}

	private ItemStack getBarrelDrop(World world, int i, int j, int k) {
		ItemStack itemstack = new ItemStack(Item.getItemFromBlock(this));
		TESTileEntityBarrel barrel = (TESTileEntityBarrel) world.getTileEntity(i, j, k);
		if (barrel != null && barrel.getBarrelMode() != 0) {
			TESItemBarrel.setBarrelDataFromTE(itemstack, barrel);
		}
		return itemstack;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		if (i == -1) {
			return barrelIcons[2];
		}
		if (i < 2) {
			return barrelIcons[1];
		}
		return barrelIcons[0];
	}

	@Override
	public Item getItemDropped(int i, Random random, int j) {
		return null;
	}

	@Override
	@SuppressWarnings("deprecation")
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int i, int j, int k) {
		world.markBlockForUpdate(i, j, k);
		return getBarrelDrop(world, i, j, k);
	}

	@Override
	public int getRenderType() {
		return TES.proxy.getBarrelRenderID();
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int side, float f, float f1, float f2) {
		TESTileEntityBarrel barrel = (TESTileEntityBarrel) world.getTileEntity(i, j, k);
		ItemStack barrelDrink = barrel.getBrewedDrink();
		ItemStack itemstack = entityplayer.inventory.getCurrentItem();
		Item item = itemstack == null ? null : itemstack.getItem();
		if (side == world.getBlockMetadata(i, j, k)) {
			if (barrelDrink != null && TESItemMug.isItemEmptyDrink(itemstack)) {
				ItemStack playerDrink = barrelDrink.copy();
				playerDrink.stackSize = 1;
				TESItemMug.Vessel v = TESItemMug.getVessel(itemstack);
				TESItemMug.setVessel(playerDrink, v, true);
				--itemstack.stackSize;
				if (itemstack.stackSize <= 0) {
					entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, playerDrink);
				} else if (!entityplayer.inventory.addItemStackToInventory(playerDrink)) {
					entityplayer.dropPlayerItemWithRandomChoice(playerDrink, false);
				}
				barrel.consumeMugRefill();
				world.playSoundEffect(i + 0.5, j + 0.5, k + 0.5, "tes:item.mug_fill", 0.5f, 0.8f + world.rand.nextFloat() * 0.4f);
				return true;
			}
			if (itemstack != null && item instanceof TESItemMug && ((TESItemMug) item).isBrewable()) {
				boolean match = false;
				if (barrel.getBarrelMode() == 0) {
					match = true;
				} else if (barrelDrink != null && barrelDrink.stackSize < TESRecipeBrewing.BARREL_CAPACITY) {
					match = barrelDrink.getItem() == itemstack.getItem() && TESItemMug.getStrength(barrelDrink) == TESItemMug.getStrength(itemstack);
				}
				if (match) {
					if (barrelDrink == null) {
						ItemStack barrelFill = itemstack.copy();
						barrelFill.stackSize = 1;
						TESItemMug.setVessel(barrelFill, TESItemMug.Vessel.MUG, false);
						barrel.setInventorySlotContents(9, barrelFill);
					} else {
						++barrelDrink.stackSize;
						barrel.setInventorySlotContents(9, barrelDrink);
					}
					barrel.setBarrelMode(2);
					if (!entityplayer.capabilities.isCreativeMode) {
						TESItemMug.Vessel v = TESItemMug.getVessel(itemstack);
						ItemStack emptyMug = v.getEmptyVessel();
						--itemstack.stackSize;
						if (itemstack.stackSize <= 0) {
							entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, emptyMug);
						} else if (!entityplayer.inventory.addItemStackToInventory(emptyMug)) {
							entityplayer.dropPlayerItemWithRandomChoice(emptyMug, false);
						}
					}
					world.playSoundEffect(i + 0.5, j + 0.5, k + 0.5, "tes:item.mug_fill", 0.5f, 0.8f + world.rand.nextFloat() * 0.4f);
					return true;
				}
			}
		}
		if (itemstack != null && item instanceof TESItemBottlePoison && barrel.canPoisonBarrel()) {
			if (!world.isRemote) {
				barrel.poisonBarrel(entityplayer);
				if (!entityplayer.capabilities.isCreativeMode) {
					ItemStack containerItem = itemstack.getItem().getContainerItem(itemstack);
					entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, containerItem);
				}
				entityplayer.openContainer.detectAndSendChanges();
				((EntityPlayerMP) entityplayer).sendContainerToPlayer(entityplayer.openContainer);
			}
			return true;
		}
		if (!world.isRemote) {
			entityplayer.openGui(TES.instance, TESGuiId.BARREL.ordinal(), world, i, j, k);
		}
		return true;
	}

	@Override
	public void onBlockHarvested(World world, int i, int j, int k, int meta, EntityPlayer entityplayer) {
		int meta1 = meta;
		if (entityplayer.capabilities.isCreativeMode) {
			world.setBlockMetadataWithNotify(i, j, k, meta1 |= 8, 4);
		}
		super.onBlockHarvested(world, i, j, k, meta1, entityplayer);
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entity, ItemStack itemstack) {
		int rotation = MathHelper.floor_double(entity.rotationYaw * 4.0f / 360.0f + 0.5) & 3;
		int meta = 0;
		switch (rotation) {
			case 0:
				meta = 2;
				break;
			case 1:
				meta = 5;
				break;
			case 2:
				meta = 3;
				break;
			case 3:
				meta = 4;
				break;
		}
		world.setBlockMetadataWithNotify(i, j, k, meta, 2);
		if (itemstack.hasDisplayName()) {
			((TESTileEntityBarrel) world.getTileEntity(i, j, k)).setBarrelName(itemstack.getDisplayName());
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconregister) {
		barrelIcons = new IIcon[3];
		barrelIcons[0] = iconregister.registerIcon(getTextureName() + "_side");
		barrelIcons[1] = iconregister.registerIcon(getTextureName() + "_top");
		barrelIcons[2] = iconregister.registerIcon(getTextureName() + "_tap");
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
}