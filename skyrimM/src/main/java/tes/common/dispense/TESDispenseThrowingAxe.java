package tes.common.dispense;

import tes.common.entity.other.inanimate.TESEntityThrowingAxe;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class TESDispenseThrowingAxe extends BehaviorDefaultDispenseItem {
	@Override
	public ItemStack dispenseStack(IBlockSource dispense, ItemStack itemstack) {
		World world = dispense.getWorld();
		IPosition iposition = BlockDispenser.func_149939_a(dispense);
		EnumFacing enumfacing = BlockDispenser.func_149937_b(dispense.getBlockMetadata());
		TESEntityThrowingAxe axe = new TESEntityThrowingAxe(world, itemstack.copy(), iposition.getX(), iposition.getY(), iposition.getZ());
		axe.setThrowableHeading(enumfacing.getFrontOffsetX(), enumfacing.getFrontOffsetY() + 0.1f, enumfacing.getFrontOffsetZ(), 1.1f, 6.0f);
		axe.setCanBePickedUp(1);
		world.spawnEntityInWorld(axe);
		itemstack.splitStack(1);
		return itemstack;
	}

	@Override
	public void playDispenseSound(IBlockSource dispense) {
		dispense.getWorld().playAuxSFX(1002, dispense.getXInt(), dispense.getYInt(), dispense.getZInt(), 0);
	}
}