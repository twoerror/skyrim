package tes.common.dispense;

import tes.common.entity.other.inanimate.TESEntityDart;
import tes.common.item.other.TESItemDart;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TESDispenseDart extends BehaviorProjectileDispense {
	private final TESItemDart theDartItem;

	public TESDispenseDart(TESItemDart item) {
		theDartItem = item;
	}

	@Override
	public float func_82500_b() {
		return 1.5f;
	}

	@Override
	public IProjectile getProjectileEntity(World world, IPosition iposition) {
		ItemStack itemstack = new ItemStack(theDartItem);
		TESEntityDart dart = TESItemDart.createDart(world, itemstack, iposition.getX(), iposition.getY(), iposition.getZ());
		dart.setCanBePickedUp(1);
		return dart;
	}

	@Override
	public void playDispenseSound(IBlockSource source) {
		source.getWorld().playSoundEffect(source.getXInt(), source.getYInt(), source.getZInt(), "tes:item.dart", 1.0f, 1.2f);
	}
}