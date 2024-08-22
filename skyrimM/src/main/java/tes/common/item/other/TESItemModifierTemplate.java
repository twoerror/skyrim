package tes.common.item.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESCreativeTabs;
import tes.common.database.TESItems;
import tes.common.enchant.TESEnchantment;
import tes.common.enchant.TESEnchantmentHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.WeightedRandom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class TESItemModifierTemplate extends Item {
	public TESItemModifierTemplate() {
		setMaxStackSize(1);
		setCreativeTab(TESCreativeTabs.TAB_TOOLS);
	}

	public static TESEnchantment getModifier(ItemStack itemstack) {
		NBTTagCompound nbt = itemstack.getTagCompound();
		if (nbt != null) {
			String s = nbt.getString("ScrollModifier");
			return TESEnchantment.getEnchantmentByName(s);
		}
		return null;
	}

	public static ItemStack getRandomCommonTemplate(Random random) {
		Collection<TESEnchantmentHelper.WeightedRandomEnchant> applicable = new ArrayList<>();
		for (TESEnchantment ench : TESEnchantment.CONTENT) {
			if (!ench.hasTemplateItem()) {
				continue;
			}
			int weight = TESEnchantmentHelper.getSkilfulWeight(ench);
			TESEnchantmentHelper.WeightedRandomEnchant wre = new TESEnchantmentHelper.WeightedRandomEnchant(ench, weight);
			applicable.add(wre);
		}
		TESEnchantmentHelper.WeightedRandomEnchant chosenWre = (TESEnchantmentHelper.WeightedRandomEnchant) WeightedRandom.getRandomItem(random, applicable);
		TESEnchantment chosenEnch = chosenWre.getTheEnchant();
		ItemStack itemstack = new ItemStack(TESItems.smithScroll);
		setModifier(itemstack, chosenEnch);
		return itemstack;
	}

	public static void setModifier(ItemStack itemstack, TESEnchantment ench) {
		String s = ench.getEnchantName();
		itemstack.setTagInfo("ScrollModifier", new NBTTagString(s));
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("rawtypes")
	public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
		super.addInformation(itemstack, entityplayer, list, flag);
		TESEnchantment mod = getModifier(itemstack);
		if (mod != null) {
			String desc = mod.getNamedFormattedDescription(itemstack);
			list.add(desc);
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		String s = super.getItemStackDisplayName(itemstack);
		TESEnchantment mod = getModifier(itemstack);
		if (mod != null) {
			return String.format(s, mod.getDisplayName());
		}
		return s;
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("rawtypes")
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (TESEnchantment ench : TESEnchantment.CONTENT) {
			if (!ench.hasTemplateItem()) {
				continue;
			}
			ItemStack itemstack = new ItemStack(this);
			setModifier(itemstack, ench);
			list.add(itemstack);
		}
	}
}