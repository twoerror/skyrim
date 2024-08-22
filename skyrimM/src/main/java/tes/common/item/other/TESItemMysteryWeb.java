package tes.common.item.other;

import tes.common.database.TESCreativeTabs;
import tes.common.dispense.TESDispenseMysteryWeb;
import tes.common.entity.other.inanimate.TESEntityMysteryWeb;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TESItemMysteryWeb extends Item {
	public TESItemMysteryWeb() {
		setCreativeTab(TESCreativeTabs.TAB_MISC);
		BlockDispenser.dispenseBehaviorRegistry.putObject(this, new TESDispenseMysteryWeb());
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (!entityplayer.capabilities.isCreativeMode) {
			--itemstack.stackSize;
		}
		world.playSoundAtEntity(entityplayer, "random.bow", 0.5f, 0.4f / (itemRand.nextFloat() * 0.4f + 0.8f));
		if (!world.isRemote) {
			world.spawnEntityInWorld(new TESEntityMysteryWeb(world, entityplayer));
		}
		return itemstack;
	}
}