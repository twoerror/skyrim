package tes.common.item.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESCreativeTabs;
import tes.common.entity.animal.*;
import tes.common.entity.other.iface.TESNPCMount;
import tes.common.util.TESReflection;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class TESItemMountArmor extends Item {
	private final ItemArmor.ArmorMaterial armorMaterial;
	private final Mount mountType;
	private final int damageReduceAmount;
	private final String textureName;
	private Item templateItem;

	public TESItemMountArmor(ItemArmor.ArmorMaterial material, Mount mount, String string) {
		armorMaterial = material;
		damageReduceAmount = material.getDamageReductionAmount(1) + material.getDamageReductionAmount(2);
		mountType = mount;
		setMaxStackSize(1);
		setCreativeTab(TESCreativeTabs.TAB_COMBAT);
		textureName = string;
	}

	private ItemStack createTemplateItemStack(ItemStack source) {
		ItemStack template = new ItemStack(templateItem);
		template.stackSize = source.stackSize;
		template.setItemDamage(source.getItemDamage());
		if (source.getTagCompound() != null) {
			template.setTagCompound(source.getTagCompound());
		}
		return template;
	}

	public String getArmorTexture() {
		if (templateItem != null) {
			int index = 0;
			if (templateItem == Items.iron_horse_armor) {
				index = 1;
			}
			if (templateItem == Items.golden_horse_armor) {
				index = 2;
			}
			if (templateItem == Items.diamond_horse_armor) {
				index = 3;
			}
			return TESReflection.getHorseArmorTextures()[index];
		}
		return "tes:textures/armor/mount/" + textureName + ".png";
	}

	public int getDamageReduceAmount() {
		return damageReduceAmount;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int i) {
		if (templateItem != null) {
			return templateItem.getIconFromDamage(i);
		}
		return super.getIconFromDamage(i);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconIndex(ItemStack itemstack) {
		if (templateItem != null) {
			return templateItem.getIconIndex(createTemplateItemStack(itemstack));
		}
		return super.getIconIndex(itemstack);
	}

	@Override
	public boolean getIsRepairable(ItemStack itemstack, ItemStack repairItem) {
		return armorMaterial.func_151685_b() == repairItem.getItem() || super.getIsRepairable(itemstack, repairItem);
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		if (templateItem != null) {
			return templateItem.getItemStackDisplayName(createTemplateItemStack(itemstack));
		}
		return super.getItemStackDisplayName(itemstack);
	}

	public ItemArmor.ArmorMaterial getMountArmorMaterial() {
		return armorMaterial;
	}

	public boolean isValid(TESNPCMount mount) {
		if (mount instanceof TESEntityGiraffe || mount instanceof TESEntityDeer || mount instanceof TESEntityElephant || mount instanceof TESEntityMammoth || mount instanceof TESEntityBoar || mount instanceof TESEntityCamel) {
			return false;
		}
		if (mount instanceof TESEntityRhino || mount instanceof TESEntityWoolyRhino) {
			return mountType == Mount.RHINO;
		}
		return mount instanceof TESEntityHorse && mountType == Mount.HORSE;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconregister) {
		if (templateItem == null) {
			super.registerIcons(iconregister);
		}
	}

	public TESItemMountArmor setTemplateItem(Item item) {
		templateItem = item;
		return this;
	}

	public enum Mount {
		HORSE, RHINO
	}
}