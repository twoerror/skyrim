package tes.common.item.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.tileentity.TESTileEntityKebabStand;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import java.util.List;

public class TESItemKebabStand extends ItemBlock {
	@SuppressWarnings("unused")
	public TESItemKebabStand(Block block) {
		super(block);
	}

	private static NBTTagCompound getKebabData(ItemStack itemstack) {
		if (itemstack.getTagCompound() != null && itemstack.getTagCompound().hasKey("TESKebabData")) {
			return itemstack.getTagCompound().getCompoundTag("TESKebabData");
		}
		return null;
	}

	public static void loadKebabData(ItemStack itemstack, TESTileEntityKebabStand kebabStand) {
		NBTTagCompound kebabData = getKebabData(itemstack);
		if (kebabData != null) {
			kebabStand.readKebabStandFromNBT(kebabData);
		}
	}

	public static void setKebabData(ItemStack itemstack, TESTileEntityKebabStand kebabStand) {
		if (kebabStand.shouldSaveBlockData()) {
			NBTTagCompound kebabData = new NBTTagCompound();
			kebabStand.writeKebabStandToNBT(kebabData);
			setKebabData(itemstack, kebabData);
		}
	}

	private static void setKebabData(ItemStack itemstack, NBTTagCompound kebabData) {
		if (itemstack.getTagCompound() == null) {
			itemstack.setTagCompound(new NBTTagCompound());
		}
		itemstack.getTagCompound().setTag("TESKebabData", kebabData);
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("rawtypes")
	public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
		NBTTagCompound kebabData = getKebabData(itemstack);
		if (kebabData != null) {
			TESTileEntityKebabStand kebabStand = new TESTileEntityKebabStand();
			kebabStand.readKebabStandFromNBT(kebabData);
			int meats = kebabStand.getMeatAmount();
			list.add(StatCollector.translateToLocalFormatted("tile.tes.kebabStand.meats", meats));
			if (kebabStand.isCooked()) {
				list.add(StatCollector.translateToLocal("tile.tes.kebabStand.cooked"));
			}
		}
	}
}