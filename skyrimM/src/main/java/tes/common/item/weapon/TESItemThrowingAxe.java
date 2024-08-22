package tes.common.item.weapon;

import tes.common.database.TESCreativeTabs;
import tes.common.dispense.TESDispenseThrowingAxe;
import tes.common.enchant.TESEnchantment;
import tes.common.enchant.TESEnchantmentHelper;
import tes.common.entity.other.inanimate.TESEntityThrowingAxe;
import tes.common.item.TESMaterialFinder;
import tes.common.recipe.TESRecipe;
import net.minecraft.block.BlockDispenser;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TESItemThrowingAxe extends Item implements TESMaterialFinder {
	private final Item.ToolMaterial tesMaterial;
	private final Item.ToolMaterial axeMaterial;

	public TESItemThrowingAxe(Item.ToolMaterial material) {
		axeMaterial = material;
		setMaxStackSize(1);
		setMaxDamage(material.getMaxUses());
		setFull3D();
		setCreativeTab(TESCreativeTabs.TAB_COMBAT);
		BlockDispenser.dispenseBehaviorRegistry.putObject(this, new TESDispenseThrowingAxe());
		tesMaterial = material;
	}

	public Item.ToolMaterial getAxeMaterial() {
		return axeMaterial;
	}

	@Override
	public boolean getIsRepairable(ItemStack itemstack, ItemStack repairItem) {
		return TESRecipe.checkItemEquals(axeMaterial.getRepairItemStack(), repairItem) || super.getIsRepairable(itemstack, repairItem);
	}

	@Override
	public Item.ToolMaterial getMaterial() {
		return tesMaterial;
	}

	public float getRangedDamageMultiplier(ItemStack itemstack, Entity shooter, Entity hit) {
		float damage = axeMaterial.getDamageVsEntity() + 4.0f;
		damage = damage + (shooter instanceof EntityLivingBase && hit instanceof EntityLivingBase ? EnchantmentHelper.getEnchantmentModifierLiving((EntityLivingBase) shooter, (EntityLivingBase) hit) : EnchantmentHelper.func_152377_a(itemstack, EnumCreatureAttribute.UNDEFINED));
		return damage * 0.5f;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		TESEntityThrowingAxe axe = new TESEntityThrowingAxe(world, entityplayer, itemstack.copy(), 2.0f);
		axe.setIsCritical(true);
		int fireAspect = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, itemstack) + TESEnchantmentHelper.calcFireAspect(itemstack);
		if (fireAspect > 0) {
			axe.setFire(100);
		}
		for (TESEnchantment ench : TESEnchantment.CONTENT) {
			if (!ench.getApplyToProjectile() || !TESEnchantmentHelper.hasEnchant(itemstack, ench)) {
				continue;
			}
			TESEnchantmentHelper.setProjectileEnchantment(axe, ench);
		}
		if (entityplayer.capabilities.isCreativeMode) {
			axe.setCanBePickedUp(2);
		}
		world.playSoundAtEntity(entityplayer, "random.bow", 1.0f, 1.0f / (itemRand.nextFloat() * 0.4f + 1.2f) + 0.25f);
		if (!world.isRemote) {
			world.spawnEntityInWorld(axe);
		}
		if (!entityplayer.capabilities.isCreativeMode) {
			--itemstack.stackSize;
		}
		return itemstack;
	}
}