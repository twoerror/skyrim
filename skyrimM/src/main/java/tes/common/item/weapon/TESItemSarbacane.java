package tes.common.item.weapon;

import tes.common.database.TESBlocks;
import tes.common.database.TESCreativeTabs;
import tes.common.database.TESItems;
import tes.common.enchant.TESEnchantment;
import tes.common.enchant.TESEnchantmentHelper;
import tes.common.entity.other.inanimate.TESEntityDart;
import tes.common.item.other.TESItemDart;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TESItemSarbacane extends Item {
	public TESItemSarbacane(Item.ToolMaterial material) {
		setMaxStackSize(1);
		setMaxDamage(material.getMaxUses());
		setCreativeTab(TESCreativeTabs.TAB_COMBAT);
		setFull3D();
	}

	public static void applySarbacaneModifiers(TESEntityDart dart, ItemStack itemstack) {
		int punch = TESEnchantmentHelper.calcRangedKnockback(itemstack);
		if (punch > 0) {
			dart.setKnockbackStrength(punch);
		}
		if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, itemstack) + TESEnchantmentHelper.calcFireAspect(itemstack) > 0) {
			dart.setFire(100);
		}
		for (TESEnchantment ench : TESEnchantment.CONTENT) {
			if (!ench.getApplyToProjectile() || !TESEnchantmentHelper.hasEnchant(itemstack, ench)) {
				continue;
			}
			TESEnchantmentHelper.setProjectileEnchantment(dart, ench);
		}
	}

	public static float getSarbacaneLaunchSpeedFactor(ItemStack itemstack) {
		float f = 1.0f;
		if (itemstack != null) {
			f *= TESEnchantmentHelper.calcRangedDamageFactor(itemstack);
		}
		return f;
	}

	@Override
	public boolean getIsRepairable(ItemStack itemstack, ItemStack repairItem) {
		return repairItem.getItem() == getItemFromBlock(TESBlocks.reeds);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		return EnumAction.bow;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		return 72000;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		boolean anyDart = false;
		for (ItemStack invItem : entityplayer.inventory.mainInventory) {
			if (invItem == null || !(invItem.getItem() instanceof TESItemDart)) {
				continue;
			}
			anyDart = true;
			break;
		}
		if (anyDart || entityplayer.capabilities.isCreativeMode) {
			entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
		}
		return itemstack;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityPlayer entityplayer, int i) {
		ItemStack dartItem = null;
		int dartSlot = -1;
		for (int l = 0; l < entityplayer.inventory.mainInventory.length; ++l) {
			ItemStack invItem = entityplayer.inventory.mainInventory[l];
			if (invItem == null || !(invItem.getItem() instanceof TESItemDart)) {
				continue;
			}
			dartItem = invItem;
			dartSlot = l;
			break;
		}
		if (dartItem == null && entityplayer.capabilities.isCreativeMode) {
			dartItem = new ItemStack(TESItems.dart);
		}
		if (dartItem != null) {
			int useTick = getMaxItemUseDuration(itemstack) - i;
			float charge = (float) useTick / 5;
			if (charge < 0.65f) {
				return;
			}
			charge = charge * (charge + 2.0f) / 3.0f;
			charge = Math.min(charge, 1.0f);
			itemstack.damageItem(1, entityplayer);
			if (!entityplayer.capabilities.isCreativeMode && dartSlot >= 0) {
				--dartItem.stackSize;
				if (dartItem.stackSize <= 0) {
					entityplayer.inventory.mainInventory[dartSlot] = null;
				}
			}
			world.playSoundAtEntity(entityplayer, "tes:item.dart", 1.0f, 1.0f / (itemRand.nextFloat() * 0.4f + 1.2f) + charge * 0.5f);
			if (!world.isRemote) {
				ItemStack shotDart = dartItem.copy();
				shotDart.stackSize = 1;
				TESEntityDart dart = TESItemDart.createDart(world, entityplayer, shotDart, charge * 2.0f * getSarbacaneLaunchSpeedFactor(itemstack));
				if (dart.getDartDamageFactor() < 1.0f) {
					dart.setDartDamageFactor(1.0f);
				}
				if (charge >= 1.0f) {
					dart.setIsCritical(true);
				}
				applySarbacaneModifiers(dart, itemstack);
				if (entityplayer.capabilities.isCreativeMode) {
					dart.setCanBePickedUp(2);
				}
				world.spawnEntityInWorld(dart);
			}
		}
	}
}