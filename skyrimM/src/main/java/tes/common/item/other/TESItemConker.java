package tes.common.item.other;

import tes.common.database.TESCreativeTabs;
import tes.common.dispense.TESDispenseConker;
import tes.common.entity.other.inanimate.TESEntityConker;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TESItemConker extends Item {
	public TESItemConker() {
		setMaxStackSize(16);
		setCreativeTab(TESCreativeTabs.TAB_MISC);
		BlockDispenser.dispenseBehaviorRegistry.putObject(this, new TESDispenseConker());
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (!world.isRemote) {
			world.spawnEntityInWorld(new TESEntityConker(world, entityplayer));
			world.playSoundAtEntity(entityplayer, "random.bow", 0.5f, 0.4f / (itemRand.nextFloat() * 0.4f + 0.8f));
			if (!entityplayer.capabilities.isCreativeMode) {
				--itemstack.stackSize;
			}
		}
		return itemstack;
	}
}