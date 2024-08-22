package tes.common.item.other;

import tes.common.database.TESCreativeTabs;
import tes.common.dispense.TESDispenseDart;
import tes.common.entity.other.inanimate.TESEntityDart;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TESItemDart extends Item {
	private boolean isPoisoned;

	public TESItemDart() {
		setCreativeTab(TESCreativeTabs.TAB_COMBAT);
		BlockDispenser.dispenseBehaviorRegistry.putObject(this, new TESDispenseDart(this));
	}

	public static TESEntityDart createDart(World world, EntityLivingBase entity, EntityLivingBase target, ItemStack itemstack, float charge, float inaccuracy) {
		return new TESEntityDart(world, entity, target, itemstack, charge, inaccuracy);
	}

	public static TESEntityDart createDart(World world, EntityLivingBase entity, ItemStack itemstack, float charge) {
		return new TESEntityDart(world, entity, itemstack, charge);
	}

	public static TESEntityDart createDart(World world, ItemStack itemstack, double d, double d1, double d2) {
		return new TESEntityDart(world, itemstack, d, d1, d2);
	}

	public TESItemDart setPoisoned() {
		isPoisoned = true;
		return this;
	}

	public boolean isPoisoned() {
		return isPoisoned;
	}
}