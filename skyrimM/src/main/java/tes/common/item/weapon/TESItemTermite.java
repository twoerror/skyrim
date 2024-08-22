package tes.common.item.weapon;

import tes.common.database.TESCreativeTabs;
import tes.common.dispense.TESDispenseTermite;
import tes.common.entity.other.inanimate.TESEntityThrownTermite;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TESItemTermite extends Item {
	public TESItemTermite() {
		setMaxStackSize(16);
		setCreativeTab(TESCreativeTabs.TAB_MISC);
		BlockDispenser.dispenseBehaviorRegistry.putObject(this, new TESDispenseTermite());
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (!world.isRemote) {
			world.spawnEntityInWorld(new TESEntityThrownTermite(world, entityplayer));
			world.playSoundAtEntity(entityplayer, "random.bow", 0.5f, 0.4f / (itemRand.nextFloat() * 0.4f + 0.8f));
			if (!entityplayer.capabilities.isCreativeMode) {
				--itemstack.stackSize;
			}
		}
		return itemstack;
	}
}