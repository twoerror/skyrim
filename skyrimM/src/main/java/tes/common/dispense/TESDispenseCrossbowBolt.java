package tes.common.dispense;

import tes.common.entity.other.inanimate.TESEntityCrossbowBolt;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TESDispenseCrossbowBolt extends BehaviorProjectileDispense {
	private final Item theBoltItem;

	public TESDispenseCrossbowBolt(Item item) {
		theBoltItem = item;
	}

	@Override
	public IProjectile getProjectileEntity(World world, IPosition iposition) {
		ItemStack itemstack = new ItemStack(theBoltItem);
		TESEntityCrossbowBolt bolt = new TESEntityCrossbowBolt(world, itemstack, iposition.getX(), iposition.getY(), iposition.getZ());
		bolt.setCanBePickedUp(1);
		return bolt;
	}

	@Override
	public void playDispenseSound(IBlockSource source) {
		source.getWorld().playSoundEffect(source.getXInt(), source.getYInt(), source.getZInt(), "tes:item.crossbow", 1.0f, 1.2f);
	}
}