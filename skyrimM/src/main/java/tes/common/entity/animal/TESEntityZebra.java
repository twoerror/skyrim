package tes.common.entity.animal;

import tes.common.database.TESItems;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class TESEntityZebra extends TESEntityHorse {
	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityZebra(World world) {
		super(world);
	}

	@Override
	public void dropFewItems(boolean flag, int i) {
		int j = rand.nextInt(2) + rand.nextInt(1 + i);
		for (int k = 0; k < j; ++k) {
			dropItem(Items.leather, 1);
		}
		j = rand.nextInt(2) + 1 + rand.nextInt(1 + i);
		for (int l = 0; l < j; ++l) {
			if (isBurning()) {
				dropItem(TESItems.zebraCooked, 1);
				continue;
			}
			dropItem(TESItems.zebraRaw, 1);
		}
	}

	@Override
	public boolean func_110259_cr() {
		return false;
	}

	@Override
	public String getAngrySoundName() {
		return "tes:zebra.hurt";
	}

	@Override
	public String getCommandSenderName() {
		if (hasCustomNameTag()) {
			return getCustomNameTag();
		}
		String s = EntityList.getEntityString(this);
		return StatCollector.translateToLocal("entity." + s + ".name");
	}

	@Override
	public String getDeathSound() {
		return "tes:zebra.death";
	}

	@Override
	public int getHorseType() {
		return 0;
	}

	@Override
	public String getHurtSound() {
		return "tes:zebra.hurt";
	}

	@Override
	public String getLivingSound() {
		return "tes:zebra.say";
	}

	@Override
	public boolean interact(EntityPlayer entityplayer) {
		ItemStack itemstack = entityplayer.inventory.getCurrentItem();
		if (itemstack != null && itemstack.getItem() == Items.bucket && !entityplayer.capabilities.isCreativeMode) {
			--itemstack.stackSize;
			if (itemstack.stackSize <= 0) {
				entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, new ItemStack(Items.milk_bucket));
			} else if (!entityplayer.inventory.addItemStackToInventory(new ItemStack(Items.milk_bucket))) {
				entityplayer.dropPlayerItemWithRandomChoice(new ItemStack(Items.milk_bucket, 1, 0), false);
			}
			return true;
		}
		return super.interact(entityplayer);
	}
}