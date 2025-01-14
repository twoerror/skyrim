package tes.common.dispense;

import tes.common.entity.other.inanimate.TESEntityBomb;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class TESDispenseBomb extends BehaviorDefaultDispenseItem {
	@Override
	public ItemStack dispenseStack(IBlockSource dispense, ItemStack itemstack) {
		EnumFacing enumfacing = BlockDispenser.func_149937_b(dispense.getBlockMetadata());
		World world = dispense.getWorld();
		int i = dispense.getXInt() + enumfacing.getFrontOffsetX();
		int j = dispense.getYInt() + enumfacing.getFrontOffsetY();
		int k = dispense.getZInt() + enumfacing.getFrontOffsetZ();
		TESEntityBomb bomb = new TESEntityBomb(world, i + 0.5f, j + 0.5f, k + 0.5f, null);
		bomb.fuse += itemstack.getItemDamage() * 10;
		bomb.setBombStrengthLevel(itemstack.getItemDamage());
		bomb.setDroppedByPlayer(true);
		world.spawnEntityInWorld(bomb);
		--itemstack.stackSize;
		return itemstack;
	}
}