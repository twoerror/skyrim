package tes.common.item.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESCreativeTabs;
import tes.common.database.TESItems;
import tes.common.entity.other.inanimate.TESEntitySmokeRing;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class TESItemPipe extends Item {
	public TESItemPipe() {
		setMaxDamage(300);
		setMaxStackSize(1);
		setCreativeTab(TESCreativeTabs.TAB_MISC);
	}

	public static int getSmokeColor(ItemStack itemstack) {
		if (itemstack.getTagCompound() != null && itemstack.getTagCompound().hasKey("SmokeColour")) {
			return itemstack.getTagCompound().getInteger("SmokeColour");
		}
		return 0;
	}

	public static boolean isPipeDyed(ItemStack itemstack) {
		int color = getSmokeColor(itemstack);
		return color != 0 && color != 16;
	}

	public static void removePipeDye(ItemStack itemstack) {
		setSmokeColor(itemstack, 0);
	}

	public static void setSmokeColor(ItemStack itemstack, int i) {
		if (itemstack.getTagCompound() == null) {
			itemstack.setTagCompound(new NBTTagCompound());
		}
		itemstack.getTagCompound().setInteger("SmokeColour", i);
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("rawtypes")
	public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
		int color = getSmokeColor(itemstack);
		list.add(StatCollector.translateToLocal("item.tes.pipe.subtitle." + color));
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		return EnumAction.bow;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		return 40;
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("rawtypes")
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i <= 16; ++i) {
			ItemStack itemstack = new ItemStack(this);
			setSmokeColor(itemstack, i);
			list.add(itemstack);
		}
	}

	@Override
	public ItemStack onEaten(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (entityplayer.inventory.hasItem(TESItems.pipeweed) || entityplayer.capabilities.isCreativeMode) {
			itemstack.damageItem(1, entityplayer);
			if (!entityplayer.capabilities.isCreativeMode) {
				entityplayer.inventory.consumeInventoryItem(TESItems.pipeweed);
			}
			if (entityplayer.canEat(false)) {
				entityplayer.getFoodStats().addStats(2, 0.3f);
			}
			if (!world.isRemote) {
				TESEntitySmokeRing smoke = new TESEntitySmokeRing(world, entityplayer);
				int color = getSmokeColor(itemstack);
				smoke.setSmokeColour(color);
				world.spawnEntityInWorld(smoke);
			}
			world.playSoundAtEntity(entityplayer, "tes:item.puff", 1.0f, (itemRand.nextFloat() - itemRand.nextFloat()) * 0.2f + 1.0f);
		}
		return itemstack;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (entityplayer.inventory.hasItem(TESItems.pipeweed) || entityplayer.capabilities.isCreativeMode) {
			entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
		}
		return itemstack;
	}
}