package tes.common.item.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESCreativeTabs;
import tes.common.database.TESMaterial;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import java.util.List;

public class TESItemRobes extends TESItemArmor {
	public TESItemRobes(int slot) {
		this(TESMaterial.ROBES, slot);
	}

	protected TESItemRobes(ArmorMaterial material, int slot) {
		super(material, slot);
		setCreativeTab(TESCreativeTabs.TAB_MISC);
	}

	public static boolean areRobesDyed(ItemStack itemstack) {
		return getSavedDyeColor(itemstack) != -1;
	}

	public static int getRobesColor(ItemStack itemstack) {
		int dye = getSavedDyeColor(itemstack);
		if (dye != -1) {
			return dye;
		}
		return 16777215;
	}

	private static int getSavedDyeColor(ItemStack itemstack) {
		if (itemstack.getTagCompound() != null && itemstack.getTagCompound().hasKey("RobesColor")) {
			return itemstack.getTagCompound().getInteger("RobesColor");
		}
		return -1;
	}

	public static void removeRobeDye(ItemStack itemstack) {
		if (itemstack.getTagCompound() != null) {
			itemstack.getTagCompound().removeTag("RobesColor");
		}
	}

	public static void setRobesColor(ItemStack itemstack, int i) {
		if (itemstack.getTagCompound() == null) {
			itemstack.setTagCompound(new NBTTagCompound());
		}
		itemstack.getTagCompound().setInteger("RobesColor", i);
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("rawtypes")
	public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
		if (areRobesDyed(itemstack)) {
			list.add(StatCollector.translateToLocal("item.tes.robes.dyed"));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getColorFromItemStack(ItemStack itemstack, int pass) {
		return getRobesColor(itemstack);
	}
}