package tes.common.dispense;

import tes.common.entity.other.TESEntityNPC;
import tes.common.item.other.TESItemSpawnEgg;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class TESDispenseSpawnEgg extends BehaviorDefaultDispenseItem {
	@Override
	public ItemStack dispenseStack(IBlockSource dispenser, ItemStack itemstack) {
		EnumFacing enumfacing = BlockDispenser.func_149937_b(dispenser.getBlockMetadata());
		double d = dispenser.getX() + enumfacing.getFrontOffsetX();
		double d1 = dispenser.getYInt() + 0.2;
		double d2 = dispenser.getZ() + enumfacing.getFrontOffsetZ();
		Entity entity = TESItemSpawnEgg.spawnCreature(dispenser.getWorld(), itemstack.getItemDamage(), d, d1, d2);
		if (entity instanceof EntityLiving && itemstack.hasDisplayName()) {
			((EntityLiving) entity).setCustomNameTag(itemstack.getDisplayName());
		}
		if (entity instanceof TESEntityNPC) {
			((TESEntityNPC) entity).setNPCPersistent(true);
		}
		itemstack.splitStack(1);
		return itemstack;
	}
}