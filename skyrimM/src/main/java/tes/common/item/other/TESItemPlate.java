package tes.common.item.other;

import tes.common.block.other.TESBlockPlate;
import tes.common.database.TESCreativeTabs;
import tes.common.dispense.TESDispensePlate;
import tes.common.entity.other.inanimate.TESEntityPlate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemReed;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TESItemPlate extends ItemReed {
	private final Block plateBlock;

	public TESItemPlate(Block block) {
		super(block);
		plateBlock = block;
		((TESBlockPlate) plateBlock).setPlateItem(this);
		setCreativeTab(TESCreativeTabs.TAB_FOOD);
		BlockDispenser.dispenseBehaviorRegistry.putObject(this, new TESDispensePlate(plateBlock));
	}

	@Override
	public boolean isValidArmor(ItemStack itemstack, int armorType, Entity entity) {
		return armorType == 0;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		TESEntityPlate plate = new TESEntityPlate(world, plateBlock, entityplayer);
		world.playSoundAtEntity(entityplayer, "random.bow", 1.0f, 1.0f / (itemRand.nextFloat() * 0.4f + 1.2f) + 0.25f);
		if (!world.isRemote) {
			world.spawnEntityInWorld(plate);
		}
		if (!entityplayer.capabilities.isCreativeMode) {
			--itemstack.stackSize;
		}
		return itemstack;
	}

	public Block getPlateBlock() {
		return plateBlock;
	}
}
