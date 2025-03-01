package tes.common.item.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.TES;
import tes.common.database.TESCreativeTabs;
import net.minecraft.block.BlockColored;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

public class TESItemDye extends Item {
	private final String[] dyeNames = {"elanor", "niphredil", "bluebell", "green", "charcoal", "brown"};

	@SideOnly(Side.CLIENT)
	private IIcon[] dyeIcons;

	public TESItemDye() {
		setHasSubtypes(true);
		setMaxDamage(0);
		setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
	}

	public static int isItemDye(ItemStack itemstack) {
		if (itemstack.getItem() == Items.dye) {
			return itemstack.getItemDamage();
		}
		for (int id : OreDictionary.getOreIDs(itemstack)) {
			String oreName = OreDictionary.getOreName(id);
			for (int j = 0; j <= 15; ++j) {
				ItemStack dye = new ItemStack(Items.dye, 1, j);
				if (!TES.isOreNameEqual(dye, oreName)) {
					continue;
				}
				return j;
			}
		}
		return -1;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int i) {
		int i1 = i;
		if (i1 >= dyeIcons.length) {
			i1 = 0;
		}
		return dyeIcons[i1];
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("rawtypes")
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < dyeNames.length; ++i) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return getUnlocalizedName() + '.' + itemstack.getItemDamage();
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer entityplayer, EntityLivingBase entityliving) {
		if (entityliving instanceof EntitySheep) {
			EntitySheep sheep = (EntitySheep) entityliving;
			int dye = isItemDye(itemstack);
			if (dye == -1) {
				return false;
			}
			int blockDye = BlockColored.func_150031_c(dye);
			if (!sheep.getSheared() && sheep.getFleeceColor() != blockDye) {
				sheep.setFleeceColor(blockDye);
				--itemstack.stackSize;
			}
			return true;
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconregister) {
		dyeIcons = new IIcon[dyeNames.length];
		for (int i = 0; i < dyeNames.length; ++i) {
			dyeIcons[i] = iconregister.registerIcon(getIconString() + '_' + dyeNames[i]);
		}
	}
}