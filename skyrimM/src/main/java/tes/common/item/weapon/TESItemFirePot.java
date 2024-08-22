package tes.common.item.weapon;

import tes.common.database.TESCreativeTabs;
import tes.common.dispense.TESDispenseFirePot;
import tes.common.entity.other.inanimate.TESEntityFirePot;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TESItemFirePot extends Item {
	public TESItemFirePot() {
		setMaxStackSize(4);
		setCreativeTab(TESCreativeTabs.TAB_COMBAT);
		BlockDispenser.dispenseBehaviorRegistry.putObject(this, new TESDispenseFirePot());
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (!world.isRemote) {
			world.spawnEntityInWorld(new TESEntityFirePot(world, entityplayer));
			world.playSoundAtEntity(entityplayer, "random.bow", 0.5f, 0.4f / (itemRand.nextFloat() * 0.4f + 0.8f));
			if (!entityplayer.capabilities.isCreativeMode) {
				--itemstack.stackSize;
			}
		}
		return itemstack;
	}
}