package tes.common.item.weapon;

import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TESItemHammer extends TESItemSword {
	public TESItemHammer(Item.ToolMaterial material) {
		super(material);
		tesWeaponDamage += 2.0f;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		return EnumAction.none;
	}
}