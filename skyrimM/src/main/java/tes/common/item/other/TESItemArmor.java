package tes.common.item.other;

import tes.common.database.TESCreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;

import java.util.Locale;

public class TESItemArmor extends ItemArmor {
	private final String extraName;

	public TESItemArmor(ArmorMaterial material, int slotType) {
		this(material, slotType, "");
	}

	public TESItemArmor(ArmorMaterial material, int slotType, String s) {
		super(material, 0, slotType);
		setCreativeTab(TESCreativeTabs.TAB_COMBAT);
		extraName = s;
	}

	private String getArmorName() {
		String prefix = getArmorMaterial().name().substring("tes".length() + 1).toLowerCase(Locale.ROOT);
		String suffix = armorType == 2 ? "2" : "1";
		if (!StringUtils.isNullOrEmpty(extraName)) {
			suffix = extraName;
		}
		return prefix + '_' + suffix;
	}

	@Override
	public String getArmorTexture(ItemStack itemstack, Entity entity, int slot, String type) {
		String path = "tes:textures/armor/";
		String armorName = getArmorName();
		StringBuilder texture = new StringBuilder(path).append(armorName);
		if (type != null) {
			texture.append('_').append(type);
		}
		return texture.append(".png").toString();
	}
}