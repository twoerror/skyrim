package tes.common.item.other;

import tes.common.block.other.TESBlockBed;
import tes.common.database.TESCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TESItemBed extends Item {
	private final TESBlockBed theBedBlock;

	public TESItemBed(Block block) {
		setMaxStackSize(1);
		setCreativeTab(TESCreativeTabs.TAB_UTIL);
		theBedBlock = (TESBlockBed) block;
		theBedBlock.setBedItem(this);
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int side, float f, float f1, float f2) {
		int j1 = j;
		if (world.isRemote) {
			return true;
		}
		if (side != 1) {
			return false;
		}
		++j1;
		int i1 = MathHelper.floor_double(entityplayer.rotationYaw * 4.0f / 360.0f + 0.5) & 3;
		int b0 = 0;
		int b1 = 0;
		if (i1 == 0) {
			b1 = 1;
		}
		if (i1 == 1) {
			b0 = -1;
		}
		if (i1 == 2) {
			b1 = -1;
		}
		if (i1 == 3) {
			b0 = 1;
		}
		if (entityplayer.canPlayerEdit(i, j1, k, side, itemstack) && entityplayer.canPlayerEdit(i + b0, j1, k + b1, side, itemstack)) {
			if (world.isAirBlock(i, j1, k) && world.isAirBlock(i + b0, j1, k + b1) && world.getBlock(i, j1 - 1, k).isSideSolid(world, i, j1 - 1, k, ForgeDirection.UP) && world.getBlock(i + b0, j1 - 1, k + b1).isSideSolid(world, i + b0, j1 - 1, k + b1, ForgeDirection.UP)) {
				world.setBlock(i, j1, k, theBedBlock, i1, 3);
				if (world.getBlock(i, j1, k) == theBedBlock) {
					world.setBlock(i + b0, j1, k + b1, theBedBlock, i1 + 8, 3);
				}
				world.playSoundEffect(i + 0.5, j1 + 0.5, k + 0.5, theBedBlock.stepSound.func_150496_b(), (theBedBlock.stepSound.getVolume() + 1.0f) / 2.0f, theBedBlock.stepSound.getPitch() * 0.8f);
				--itemstack.stackSize;
				return true;
			}
		}
		return false;
	}
}